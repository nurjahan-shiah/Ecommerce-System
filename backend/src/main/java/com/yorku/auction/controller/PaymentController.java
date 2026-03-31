package com.yorku.auction.controller;

import com.yorku.auction.dto.PayNowRequest;
import com.yorku.auction.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // UC5: Process payment for won auction
    @PostMapping("/pay")
    public ResponseEntity<?> payNow(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody PayNowRequest request) {
        try {
            Map<String, Object> result = new LinkedHashMap<>(
                paymentService.payNow(
                    sessionId, userId,
                    request.getAuctionId(),
                    request.isExpeditedShipping(),
                    request.getCardNumber(),
                    request.getCardName(),
                    request.getExpiryDate(),
                    request.getSecurityCode()
                )
            );
            // HATEOAS _links (UC5-UC6)
            Map<String, Object> links = new LinkedHashMap<>();
            links.put("self",    Map.of("href", "/api/payments/pay", "method", "POST"));
            links.put("browse",  Map.of("href", "/api/catalogue/items/active", "method", "GET"));
            links.put("profile", Map.of("href", "/api/users/{id}", "method", "GET", "templated", true));
            result.put("_links", links);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
