package com.yorku.auction.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionSelectionService {

    private final JdbcTemplate jdbc;

    public SessionSelectionService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // upsert = enforce single selection per session
    public void setSelection(String sessionId, Long userId, Long auctionId) {
        String sql =
            "INSERT INTO session_selection(session_id, user_id, selected_auction_id) " +
            "VALUES(?,?,?) " +
            "ON CONFLICT(session_id) DO UPDATE SET " +
            "user_id=excluded.user_id, selected_auction_id=excluded.selected_auction_id, updated_at=CURRENT_TIMESTAMP";

        jdbc.update(sql, sessionId, userId, auctionId);
    }

    public Optional<Long> getSelection(String sessionId) {
        String sql = "SELECT selected_auction_id FROM session_selection WHERE session_id = ?";
        return jdbc.query(sql, new Object[]{sessionId}, rs -> {
            if (rs.next()) return Optional.of(rs.getLong("selected_auction_id"));
            return Optional.empty();
        });
    }

    public void clearSelection(String sessionId) {
        jdbc.update("DELETE FROM session_selection WHERE session_id = ?", sessionId);
    }
}