package com.yorku.auction.service;

import com.yorku.auction.dto.CreateAuctionRequest;
import com.yorku.auction.pubsub.AuctionEvent;
import com.yorku.auction.pubsub.AuctionEventType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional
public class AuctionService {

    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher eventPublisher;

    public AuctionService(JdbcTemplate jdbc, ApplicationEventPublisher eventPublisher) {
        this.jdbc           = jdbc;
        this.eventPublisher = eventPublisher;
    }

    public Map<String, Object> createAuction(Long sellerId, CreateAuctionRequest request) {

        // Insert catalogue item
        Long itemId;
        try {
            itemId = jdbc.queryForObject(
                "INSERT INTO catalogue_items (item_name, description, starting_price, shipping_price, " +
                "expedited_shipping_price, shipping_days, keywords, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) RETURNING item_id",
                Long.class,
                request.getItemName(), request.getDescription(), request.getStartingPrice(),
                request.getShippingPrice(), request.getExpeditedShippingPrice(),
                request.getShippingDays(), request.getKeywords()
            );
        } catch (Exception e) {
            itemId = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        }

        // Calculate end time
        long nowSeconds = System.currentTimeMillis() / 1000;
        long durationSeconds;
        if (request.getDurationMinutes() != null && request.getDurationMinutes() > 0) {
            durationSeconds = Math.round(request.getDurationMinutes() * 60.0);
        } else if (request.getDurationHours() != null && request.getDurationHours() > 0) {
            durationSeconds = request.getDurationHours() * 3600L;
        } else {
            throw new IllegalArgumentException("Provide a positive durationMinutes or durationHours.");
        }
        long endTimeSeconds = nowSeconds + durationSeconds;

        // Insert auction
        jdbc.update(
            "INSERT INTO auctions (item_id, seller_id, auction_type, end_time, current_price, status) " +
            "VALUES (?, ?, 'FORWARD', datetime(?, 'unixepoch'), ?, 'ACTIVE')",
            itemId, sellerId, endTimeSeconds, request.getStartingPrice()
        );

        Long auctionId = jdbc.queryForObject(
            "SELECT auction_id FROM auctions WHERE item_id = ? ORDER BY auction_id DESC LIMIT 1",
            Long.class, itemId
        );

        long remaining = endTimeSeconds - (System.currentTimeMillis() / 1000);

        // ── PUBLISH: AUCTION_CREATED ─────────────────────────────────────────
        Map<String, Object> createdPayload = new LinkedHashMap<>();
        createdPayload.put("auctionId",        auctionId);
        createdPayload.put("itemName",         request.getItemName());
        createdPayload.put("startingPrice",    request.getStartingPrice());
        createdPayload.put("sellerId",         sellerId);
        createdPayload.put("remainingSeconds", remaining);
        eventPublisher.publishEvent(
            new AuctionEvent(this, AuctionEventType.AUCTION_CREATED, auctionId, createdPayload));

        Map<String, Object> response = new HashMap<>();
        response.put("message",          "Auction created successfully");
        response.put("auctionId",        auctionId);
        response.put("itemId",           itemId);
        response.put("itemName",         request.getItemName());
        response.put("startingPrice",    request.getStartingPrice());
        response.put("endTime",          endTimeSeconds);
        response.put("remainingSeconds", remaining);
        return response;
    }
}
