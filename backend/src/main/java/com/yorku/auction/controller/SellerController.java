package com.yorku.auction.controller;

import com.yorku.auction.dto.CreateAuctionRequest;
import com.yorku.auction.service.AuctionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/seller")
@CrossOrigin(origins = "*")
public class SellerController {
    
    private final AuctionService auctionService;

    public SellerController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping("/auctions")
    public ResponseEntity<?> createAuction(
            @RequestHeader("X-User-Id") Long sellerId,
            @Valid @RequestBody CreateAuctionRequest request) {
        
        try {
            Map<String, Object> response = auctionService.createAuction(sellerId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
