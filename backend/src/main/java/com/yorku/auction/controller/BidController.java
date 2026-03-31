package com.yorku.auction.controller;

import com.yorku.auction.dto.PlaceBidRequest;
import com.yorku.auction.service.BidService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bids")
@CrossOrigin(origins = "*")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    // UC3: Place a bid on the session-selected auction
    @PostMapping
    public ResponseEntity<?> placeBid(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody PlaceBidRequest request
    ) {
        try {
            Map<String, Object> result = new LinkedHashMap<>(
                (Map<String, Object>) bidService.placeBid(sessionId, userId, request.bidAmount)
            );
            // HATEOAS _links
            Map<String, Object> links = new LinkedHashMap<>();
            links.put("self",      Map.of("href", "/api/bids", "method", "POST"));
            links.put("history",   Map.of("href", "/api/bids/{auctionId}", "method", "GET", "templated", true));
            links.put("selection", Map.of("href", "/api/session/selection", "method", "GET"));
            links.put("payment",   Map.of("href", "/api/payments/pay", "method", "POST"));
            result.put("_links", links);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    // Get bid history for a specific auction
    @GetMapping("/{auctionId}")
    public ResponseEntity<?> getBidsForAuction(@PathVariable Long auctionId) {
        return ResponseEntity.ok(bidService.getBidsForAuction(auctionId));
    }
}
