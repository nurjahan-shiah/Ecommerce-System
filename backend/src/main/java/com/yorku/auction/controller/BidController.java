package com.yorku.auction.controller;

import com.yorku.auction.dto.PlaceBidRequest;
import com.yorku.auction.service.BidService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bids")
@CrossOrigin(origins = "*")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    //Place a bid on the selected auction per session
    @PostMapping
    public ResponseEntity<?> placeBid(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody PlaceBidRequest request
    ) {
        try {
            return ResponseEntity.ok(bidService.placeBid(sessionId, userId, request.bidAmount));
        } catch (RuntimeException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    //Get bid history for a specific auction
    @GetMapping("/{auctionId}")
    public ResponseEntity<?> getBidsForAuction(@PathVariable Long auctionId) {
        return ResponseEntity.ok(bidService.getBidsForAuction(auctionId));
    }
}