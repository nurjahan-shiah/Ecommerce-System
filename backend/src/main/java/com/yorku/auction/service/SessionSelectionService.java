package com.yorku.auction.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class SessionSelectionService {

    private final JdbcTemplate jdbc;

    public SessionSelectionService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    //enforce single selection per session
    public void setSelection(String sessionId, Long userId, Long auctionId) {
        String sql =
            "INSERT INTO session_selection(session_id, user_id, selected_auction_id) " +
            "VALUES(?,?,?) " +
            "ON CONFLICT(session_id) DO UPDATE SET " +
            "user_id=excluded.user_id, selected_auction_id=excluded.selected_auction_id, updated_at=CURRENT_TIMESTAMP";

        jdbc.update(sql, sessionId, userId, auctionId);
    }

    public Optional<Map<String, Object>> getSelection(String sessionId) {
        String sql = """
            SELECT a.auction_id, a.item_id, a.seller_id, a.current_price, a.status, a.highest_bidder_id,
                   a.end_time, ci.item_name, ci.description, ci.shipping_price, ci.expedited_shipping_price,
                   ci.shipping_days, u.first_name || ' ' || u.last_name as seller_name
            FROM session_selection ss
            JOIN auctions a ON ss.selected_auction_id = a.auction_id
            JOIN catalogue_items ci ON a.item_id = ci.item_id
            JOIN users u ON a.seller_id = u.user_id
            WHERE ss.session_id = ?
            """;
        
        try {
            return Optional.ofNullable(jdbc.queryForMap(sql, sessionId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void clearSelection(String sessionId) {
        jdbc.update("DELETE FROM session_selection WHERE session_id = ?", sessionId);
    }
}