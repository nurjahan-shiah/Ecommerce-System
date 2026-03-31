package com.yorku.auction.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * NotificationService — the subscriber side of the pub-sub bus.
 *
 * Responsibilities:
 *  1. Maintain a registry of active SSE connections keyed by userId.
 *  2. Listen for AuctionEvents published anywhere in the application.
 *  3. Fan each event out to the right SSE clients:
 *       - BID_PLACED     → all users subscribed to that auctionId
 *       - AUCTION_ENDED  → all users subscribed to that auctionId
 *       - AUCTION_CREATED→ every connected user (global broadcast)
 *       - PAYMENT_CONFIRMED → only the paying user
 *
 * SSE emitters are stored in two indexes:
 *   userEmitters    : userId  → list of emitters (one user may have multiple tabs)
 *   auctionWatchers : auctionId → set of userIds watching that auction
 */
@Service
public class NotificationService {

    private static final long SSE_TIMEOUT_MS = 30 * 60 * 1000L; // 30 minutes

    private final ObjectMapper json = new ObjectMapper();

    // userId → list of live SseEmitters
    private final Map<Long, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    // auctionId → set of userIds currently watching
    private final Map<Long, Set<Long>> auctionWatchers = new ConcurrentHashMap<>();

    // ─── SUBSCRIBE ──────────────────────────────────────────────────────────────

    /**
     * Called by NotificationController when a client opens an SSE connection.
     *
     * @param userId     the logged-in user (0 = anonymous — receives AUCTION_CREATED only)
     * @param auctionId  optional: the auction the user is currently viewing (for bid updates)
     */
    public SseEmitter subscribe(Long userId, Long auctionId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);

        // register in user index
        userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        // register in auction-watcher index if viewing a specific auction
        if (auctionId != null) {
            auctionWatchers.computeIfAbsent(auctionId, k -> ConcurrentHashMap.newKeySet()).add(userId);
        }

        Runnable cleanup = () -> removeEmitter(userId, auctionId, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        // send a handshake comment so the browser knows the stream is alive
        try {
            emitter.send(SseEmitter.event()
                .comment("connected")
                .name("connected")
                .data("{\"status\":\"connected\",\"userId\":" + userId + "}"));
        } catch (IOException ignored) {
            cleanup.run();
        }

        return emitter;
    }

    private void removeEmitter(Long userId, Long auctionId, SseEmitter emitter) {
        List<SseEmitter> list = userEmitters.get(userId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) userEmitters.remove(userId);
        }
        if (auctionId != null) {
            Set<Long> watchers = auctionWatchers.get(auctionId);
            if (watchers != null) {
                // only remove from watcher set when no emitters remain for this user
                if (!userEmitters.containsKey(userId)) {
                    watchers.remove(userId);
                }
            }
        }
    }

    // ─── PUBLISH ────────────────────────────────────────────────────────────────

    /**
     * Listens for any AuctionEvent published via Spring's ApplicationEventPublisher
     * and routes it to the appropriate SSE clients.
     *
     * @Async keeps the publishing thread (e.g. BidService transaction) non-blocking.
     */
    @Async
    @EventListener
    public void onAuctionEvent(AuctionEvent event) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("type",      event.getType().name());
        envelope.put("auctionId", event.getAuctionId());
        envelope.put("data",      event.getPayload());
        envelope.put("ts",        System.currentTimeMillis());

        String jsonPayload;
        try {
            jsonPayload = json.writeValueAsString(envelope);
        } catch (Exception e) {
            return; // shouldn't happen with simple maps
        }

        switch (event.getType()) {

            case BID_PLACED:
            case AUCTION_ENDED:
                // notify every user currently watching this auction
                Set<Long> watchers = auctionWatchers.getOrDefault(event.getAuctionId(), Set.of());
                for (Long uid : watchers) {
                    pushToUser(uid, event.getType().name(), jsonPayload);
                }
                break;

            case AUCTION_CREATED:
                // global broadcast — tell everyone a new auction is live
                for (Long uid : userEmitters.keySet()) {
                    pushToUser(uid, event.getType().name(), jsonPayload);
                }
                break;

            case PAYMENT_CONFIRMED:
                // only the buyer who just paid
                Object buyerIdObj = event.getPayload().get("userId");
                if (buyerIdObj instanceof Number) {
                    pushToUser(((Number) buyerIdObj).longValue(), event.getType().name(), jsonPayload);
                }
                break;
        }
    }

    // ─── HELPERS ────────────────────────────────────────────────────────────────

    private void pushToUser(Long userId, String eventName, String jsonPayload) {
        List<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters == null || emitters.isEmpty()) return;

        List<SseEmitter> dead = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(jsonPayload));
            } catch (IOException | IllegalStateException e) {
                dead.add(emitter);
            }
        }
        emitters.removeAll(dead);
    }

    /** How many users are currently connected (useful for /actuator or tests). */
    public int connectedUserCount() {
        return userEmitters.size();
    }
}
