package com.yorku.auction.service;

import com.yorku.auction.dto.CreateAuctionRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class AuctionService {
    private final JdbcTemplate jdbc;

    public AuctionService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, Object> createAuction(Long sellerId, CreateAuctionRequest request) {
    // create catalogue item
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

    // get the end time
    long endTimeSeconds = System.currentTimeMillis() / 1000 + (request.getDurationHours() * 3600L);

    // create the auction
    jdbc.update(
        "INSERT INTO auctions (item_id, seller_id, auction_type, end_time, current_price, status) " +
        "VALUES (?, ?, 'FORWARD', datetime(?, 'unixepoch'), ?, 'ACTIVE')",
        itemId, sellerId, endTimeSeconds, request.getStartingPrice()  // ✅ current_price provided
    );

    // get the auction ID
    Long auctionId = jdbc.queryForObject(
        "SELECT auction_id FROM auctions WHERE item_id = ? ORDER BY auction_id DESC LIMIT 1", 
        Long.class, itemId
    );

    // response
    Map<String, Object> response = new HashMap<>();
    response.put("message", "Auction created successfully");
    response.put("auctionId", auctionId);
    response.put("itemId", itemId);
    response.put("itemName", request.getItemName());
    response.put("startingPrice", request.getStartingPrice());
    response.put("endTime", endTimeSeconds);
    response.put("remainingSeconds", endTimeSeconds - (System.currentTimeMillis() / 1000));
    
    return response;
}

}
