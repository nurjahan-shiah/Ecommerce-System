package com.yorku.auction.service;

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
public class PaymentService {

    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentService(JdbcTemplate jdbc, ApplicationEventPublisher eventPublisher) {
        this.jdbc           = jdbc;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Map<String, Object> payNow(String sessionId,
                                      Long userId,
                                      Long auctionId,
                                      boolean expeditedShipping,
                                      String cardNumber,
                                      String cardName,
                                      String expiryDate,
                                      String securityCode) {

        Map<String, Object> resp = new HashMap<>();

        // Validate session selection
        Long selectedAuctionId = jdbc.query(
                "SELECT selected_auction_id FROM session_selection WHERE session_id = ?",
                new Object[]{sessionId},
                rs -> rs.next() ? rs.getLong("selected_auction_id") : null
        );
        
        // auction not selected
        if (selectedAuctionId == null) {
            throw new RuntimeException("No auction selected for this session. Select an item first.");
        }

        // selected doesnt match requested
        if (!selectedAuctionId.equals(auctionId)) {
            throw new RuntimeException("Selected auction does not match requested auction.");
        }

        // get the auction and it's status
        Map<String, Object> auction = jdbc.queryForMap(
                "SELECT auction_id, item_id, current_price, status, " +
                        "highest_bidder_id, " +
                        "CAST(strftime('%s', end_time) AS INTEGER) AS endepoch " +
                        "FROM auctions WHERE auction_id = ?",
                auctionId
        );

        String status = (String) auction.get("status");
        Long highestBidderId = auction.get("highest_bidder_id") == null
                ? null
                : ((Number) auction.get("highest_bidder_id")).longValue();
        double currentPrice = ((Number) auction.get("current_price")).doubleValue();
        long endEpoch = ((Number) auction.get("endepoch")).longValue();
        long nowEpoch = System.currentTimeMillis() / 1000;

        // mark it as ended if the time expired
        if (nowEpoch >= endEpoch && "ACTIVE".equalsIgnoreCase(status)) {
            jdbc.update("UPDATE auctions SET status = 'ENDED' WHERE auction_id = ?", auctionId);
            status = "ENDED";
        }
        
        // checks if the auction is completed (paid for)
        if ("COMPLETED".equalsIgnoreCase(status)) {
            throw new RuntimeException("Auction completed and has already been paid for");
        }
        
        // checks if the auction is not ended yet
        if (!"ENDED".equalsIgnoreCase(status)) {
            throw new RuntimeException("Auction is not ENDED. Cannot pay yet.");
        }

        // verify the requested payer is the winner
        if (highestBidderId == null || !highestBidderId.equals(userId)) {
            throw new RuntimeException("You are not the winning bidder for this auction. - Userid "+userId+" expecting "+highestBidderId);
        }

        // get the shipping details
        Long itemId = ((Number) auction.get("item_id")).longValue();
        Map<String, Object> item = jdbc.queryForMap(
                "SELECT item_name, shipping_price, expedited_shipping_price, shipping_days " +
                        "FROM catalogue_items WHERE item_id = ?",
                itemId
        );

        String itemName = (String) item.get("item_name");
        double shippingPrice = item.get("shipping_price") == null
                ? 0.0 : ((Number) item.get("shipping_price")).doubleValue();
        double expeditedShippingPrice = item.get("expedited_shipping_price") == null
                ? 0.0 : ((Number) item.get("expedited_shipping_price")).doubleValue();
        int shippingDays = item.get("shipping_days") == null
                ? 7 : ((Number) item.get("shipping_days")).intValue();

        double shippingCost = expeditedShipping 
                ? (shippingPrice + expeditedShippingPrice) 
                : shippingPrice;
        double totalAmount = currentPrice + shippingCost;

        // get the user's shipping address
        Map<String, Object> user = jdbc.queryForMap(
                "SELECT first_name, last_name, street_number, street_name, " +
                        "city, country, postal_code " +
                        "FROM users WHERE user_id = ?",
                userId
        );

        String fullName = ((String) user.get("first_name")) + " " + ((String) user.get("last_name"));
        String streetNumber = (String) user.get("street_number");
        String streetName = (String) user.get("street_name");
        String cityStr = (String) user.get("city");
        String countryStr = (String) user.get("country");
        String postalCode = (String) user.get("postal_code");

        StringBuilder address = new StringBuilder();
        if (streetNumber != null) address.append(streetNumber).append(" ");
        if (streetName != null) address.append(streetName).append(", ");
        if (cityStr != null) address.append(cityStr).append(", ");
        if (countryStr != null) address.append(countryStr).append(" ");
        if (postalCode != null) address.append(postalCode);
        String fullAddress = address.toString().replaceAll(",\\s+", ", ").trim();

        // record the payment in the payments database
        jdbc.update(
                "INSERT INTO payments (auction_id, user_id, total_amount, card_number, card_name, " +
                        "expedited_shipping, payment_status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                auctionId, userId, totalAmount, cardNumber, cardName,
                expeditedShipping ? 1 : 0, "COMPLETED"
        );

        // mark the auction as completed
        jdbc.update("UPDATE auctions SET status = 'COMPLETED' WHERE auction_id = ?", auctionId);

        // ── PUBLISH: AUCTION_ENDED ───────────────────────────────────────────
        // Notify every watcher of this auction that it's now closed
        Map<String, Object> endedPayload = new LinkedHashMap<>();
        endedPayload.put("auctionId",  auctionId);
        endedPayload.put("itemName",   itemName);
        endedPayload.put("winnerId",   highestBidderId);
        endedPayload.put("finalPrice", currentPrice);
        eventPublisher.publishEvent(
            new AuctionEvent(this, AuctionEventType.AUCTION_ENDED, auctionId, endedPayload));

        // ── PUBLISH: PAYMENT_CONFIRMED ───────────────────────────────────────
        // Notify only the winning buyer — their receipt is ready
        Map<String, Object> paidPayload = new LinkedHashMap<>();
        paidPayload.put("userId",      userId);
        paidPayload.put("auctionId",   auctionId);
        paidPayload.put("itemName",    itemName);
        paidPayload.put("totalAmount", totalAmount);
        paidPayload.put("shippingDays", shippingDays);
        eventPublisher.publishEvent(
            new AuctionEvent(this, AuctionEventType.PAYMENT_CONFIRMED, auctionId, paidPayload));

        // response for the Front end
        resp.put("message", "Payment completed successfully");
        resp.put("auctionId", auctionId);
        resp.put("itemName", itemName);
        resp.put("winnerId", highestBidderId);
        resp.put("itemPrice", currentPrice);
        resp.put("shippingCost", shippingCost);
        resp.put("totalAmount", totalAmount);
        resp.put("expeditedShipping", expeditedShipping);
        resp.put("shippingDays", shippingDays);
        resp.put("shippingAddress", fullAddress);
        resp.put("recipientName", fullName);

        return resp;
    }
}
