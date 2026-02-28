package com.yorku.auction.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BidService {

    private final JdbcTemplate jdbc;

    public BidService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public Map<String, Object> placeBid(String sessionId, Long userId, double bidAmount) {

        //Get selected auction for this session
        Long selectedAuctionId = jdbc.query(
                "SELECT selected_auction_id FROM session_selection WHERE session_id = ?",
                new Object[]{ sessionId },
                rs -> rs.next() ? rs.getLong("selected_auction_id") : null
        );

        if (selectedAuctionId == null) {
            throw new RuntimeException("No auction selected for this session. Select an item first (UC2.3).");
        }

        //Fetch auction state, validate active and not expired
        Map<String, Object> auction = jdbc.queryForMap(
                "SELECT auction_id, current_price, status, " +
                "CAST(strftime('%s', end_time) AS INTEGER) AS end_epoch " +
                "FROM auctions WHERE auction_id = ?",
                selectedAuctionId
        );

        double currentPrice = ((Number) auction.get("current_price")).doubleValue();
        String status = (String) auction.get("status");
        long endEpoch = ((Number) auction.get("end_epoch")).longValue();
        long nowEpoch = System.currentTimeMillis() / 1000;

        if (!"ACTIVE".equalsIgnoreCase(status)) {
            throw new RuntimeException("Auction is not ACTIVE.");
        }
        if (endEpoch <= nowEpoch) {
            throw new RuntimeException("Auction has ended.");
        }

        //Validate bid amount
        if (bidAmount <= currentPrice) {
            throw new RuntimeException("Bid must be greater than current price (" + currentPrice + ").");
        }

        //Insert bid record
        jdbc.update(
                "INSERT INTO bids (auction_id, bidder_id, bid_amount) VALUES (?, ?, ?)",
                selectedAuctionId, userId, bidAmount
        );

        //Update auction current price, highest bidder
        jdbc.update(
                "UPDATE auctions SET current_price = ?, highest_bidder_id = ? WHERE auction_id = ?",
                bidAmount, userId, selectedAuctionId
        );

        //JSON response
        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Bid placed successfully");
        resp.put("auctionId", selectedAuctionId);
        resp.put("oldPrice", currentPrice);
        resp.put("newPrice", bidAmount);
        resp.put("remainingSeconds", Math.max(0, endEpoch - nowEpoch));
        return resp;
    }

    //fetch bid history for an auction
    public List<Map<String, Object>> getBidsForAuction(Long auctionId) {
        String sql =
                "SELECT bid_id, auction_id, bidder_id, bid_amount, bid_time " +
                "FROM bids WHERE auction_id = ? " +
                "ORDER BY bid_time DESC";
        return jdbc.queryForList(sql, auctionId);
    }
}