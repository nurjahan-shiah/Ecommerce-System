package com.yorku.auction.service;

import com.yorku.auction.pubsub.AuctionEvent;
import com.yorku.auction.pubsub.AuctionEventType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BidService {

    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher eventPublisher;

    public BidService(JdbcTemplate jdbc, ApplicationEventPublisher eventPublisher) {
        this.jdbc           = jdbc;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Map<String, Object> placeBid(String sessionId, Long userId, double bidAmount) {

        // Get selected auction for this session
        Long selectedAuctionId = jdbc.query(
                "SELECT selected_auction_id FROM session_selection WHERE session_id = ?",
                new Object[]{ sessionId },
                rs -> rs.next() ? rs.getLong("selected_auction_id") : null
        );

        if (selectedAuctionId == null) {
            throw new RuntimeException("No auction selected for this session. Select an item first (UC2.3).");
        }

        // Fetch auction state — validate active and not expired
        Map<String, Object> auction = jdbc.queryForMap(
                "SELECT a.auction_id, a.current_price, a.status, a.highest_bidder_id, " +
                "CAST(strftime('%s', a.end_time) AS INTEGER) AS end_epoch, " +
                "ci.item_name " +
                "FROM auctions a " +
                "JOIN catalogue_items ci ON ci.item_id = a.item_id " +
                "WHERE a.auction_id = ?",
                selectedAuctionId
        );

        double currentPrice   = ((Number) auction.get("current_price")).doubleValue();
        String status         = (String)  auction.get("status");
        long   endEpoch       = ((Number) auction.get("end_epoch")).longValue();
        long   nowEpoch       = System.currentTimeMillis() / 1000;
        String itemName       = (String)  auction.get("item_name");
        Object prevHighBidder = auction.get("highest_bidder_id");

        if (!"ACTIVE".equalsIgnoreCase(status)) {
            throw new RuntimeException("Auction is not ACTIVE.");
        }
        if (endEpoch <= nowEpoch) {
            throw new RuntimeException("Auction has ended.");
        }
        if (bidAmount <= currentPrice) {
            throw new RuntimeException("Bid must be greater than current price (" + currentPrice + ").");
        }

        // Persist bid
        jdbc.update("INSERT INTO bids (auction_id, bidder_id, bid_amount) VALUES (?, ?, ?)",
                selectedAuctionId, userId, bidAmount);
        jdbc.update("UPDATE auctions SET current_price = ?, highest_bidder_id = ? WHERE auction_id = ?",
                bidAmount, userId, selectedAuctionId);

        long remaining = Math.max(0, endEpoch - nowEpoch);

        // ── PUBLISH: BID_PLACED ──────────────────────────────────────────────
        Map<String, Object> bidPayload = new LinkedHashMap<>();
        bidPayload.put("auctionId",        selectedAuctionId);
        bidPayload.put("itemName",         itemName);
        bidPayload.put("newPrice",         bidAmount);
        bidPayload.put("oldPrice",         currentPrice);
        bidPayload.put("bidderId",         userId);
        bidPayload.put("remainingSeconds", remaining);
        if (prevHighBidder != null) {
            bidPayload.put("outbidUserId", ((Number) prevHighBidder).longValue());
        }
        eventPublisher.publishEvent(
            new AuctionEvent(this, AuctionEventType.BID_PLACED, selectedAuctionId, bidPayload));

        // ── PUBLISH: AUCTION_ENDED (lazy close) ──────────────────────────────
        if (remaining == 0) {
            jdbc.update("UPDATE auctions SET status = 'ENDED' WHERE auction_id = ?", selectedAuctionId);
            Map<String, Object> endedPayload = new LinkedHashMap<>();
            endedPayload.put("auctionId",  selectedAuctionId);
            endedPayload.put("itemName",   itemName);
            endedPayload.put("winnerId",   userId);
            endedPayload.put("finalPrice", bidAmount);
            eventPublisher.publishEvent(
                new AuctionEvent(this, AuctionEventType.AUCTION_ENDED, selectedAuctionId, endedPayload));
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("message",          "Bid placed successfully");
        resp.put("auctionId",        selectedAuctionId);
        resp.put("oldPrice",         currentPrice);
        resp.put("newPrice",         bidAmount);
        resp.put("remainingSeconds", remaining);
        return resp;
    }

    public List<Map<String, Object>> getBidsForAuction(Long auctionId) {
        return jdbc.queryForList(
            "SELECT bid_id, auction_id, bidder_id, bid_amount, bid_time " +
            "FROM bids WHERE auction_id = ? ORDER BY bid_time DESC",
            auctionId
        );
    }
}
