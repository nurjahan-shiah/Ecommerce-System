package com.yorku.auction.controller;

import com.yorku.auction.dto.CreateAuctionRequest;
import com.yorku.auction.service.AuctionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/seller")
@CrossOrigin(origins = "*")
public class SellerController {

    private final AuctionService auctionService;

    public SellerController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    // UC7: Seller creates a new auction item listing
    @PostMapping("/auctions")
    public ResponseEntity<?> createAuction(
            @RequestHeader("X-User-Id") Long sellerId,
            @Valid @RequestBody CreateAuctionRequest request) {
        try {
            Map<String, Object> result = new LinkedHashMap<>(
                auctionService.createAuction(sellerId, request)
            );
            // HATEOAS _links (UC7)
            Map<String, Object> links = new LinkedHashMap<>();
            links.put("self",       Map.of("href", "/api/seller/auctions", "method", "POST"));
            links.put("browse",     Map.of("href", "/api/catalogue/items/active", "method", "GET"));
            links.put("auctionBids",Map.of("href", "/api/bids/{auctionId}", "method", "GET", "templated", true));
            result.put("_links", links);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }
}
