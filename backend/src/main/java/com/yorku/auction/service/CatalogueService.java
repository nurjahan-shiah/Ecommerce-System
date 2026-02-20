package com.yorku.auction.service;

import com.yorku.auction.dto.CatalogueItemResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CatalogueService {

    private final JdbcTemplate jdbc;

    public CatalogueService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<CatalogueItemResponse> getActiveAuctions(String keyword) {
        long now = Instant.now().getEpochSecond();

        String sql =
            "SELECT ci.item_id, a.auction_id, ci.item_name, a.auction_type, a.current_price, " +
            "CAST(strftime('%s', a.end_time) AS INTEGER) AS end_epoch " +
            "FROM auctions a " +
            "JOIN catalogue_items ci ON ci.item_id = a.item_id " +
            "WHERE a.status='ACTIVE' AND datetime(a.end_time) > datetime('now') ";

        Object[] params;

        if (keyword == null || keyword.trim().isEmpty()) {
            params = new Object[] {};
        } else {
            sql += "AND (lower(ci.item_name) LIKE ? OR lower(ci.description) LIKE ? OR lower(ci.keywords) LIKE ?) ";
            String k = "%" + keyword.toLowerCase() + "%";
            params = new Object[] { k, k, k };
        }

        sql += "ORDER BY a.end_time ASC";

        return jdbc.query(sql, params, (rs, rowNum) -> {
            long endEpoch = rs.getLong("end_epoch");
            long remaining = Math.max(0, endEpoch - now);

            return new CatalogueItemResponse(
                rs.getLong("item_id"),
                rs.getLong("auction_id"),
                rs.getString("item_name"),
                rs.getString("auction_type"),
                rs.getDouble("current_price"),
                remaining
            );
        });
    }
}
