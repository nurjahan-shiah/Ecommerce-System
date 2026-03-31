package com.yorku.auction.pubsub;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * NotificationController
 *
 * Exposes two endpoints:
 *
 *   GET /api/notifications/subscribe
 *       Opens a persistent SSE stream for the caller.
 *       Query params:
 *         userId    (required) — the logged-in user's ID
 *         auctionId (optional) — the auction being viewed (enables bid-update pushes)
 *
 *   GET /api/notifications/status
 *       Returns how many users are currently connected (useful for debugging).
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Opens an SSE stream.
     *
     * Example (browser):
     *   const es = new EventSource('/api/notifications/subscribe?userId=5&auctionId=3');
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestParam Long userId,
            @RequestParam(required = false) Long auctionId) {

        return notificationService.subscribe(userId, auctionId);
    }

    /** Lightweight health check — returns live connection count. */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(Map.of(
            "connectedUsers", notificationService.connectedUserCount(),
            "transport", "SSE"
        ));
    }
}
