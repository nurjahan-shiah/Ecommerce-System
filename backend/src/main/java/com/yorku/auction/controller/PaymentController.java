package com.yorku.auction.controller;

import com.yorku.auction.dto.PayNowRequest;
import com.yorku.auction.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // pay now endpoint
    @PostMapping("/pay")
    public ResponseEntity<?> payNow(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody PayNowRequest request) {

        try {
            Map<String, Object> response = paymentService.payNow(
                    sessionId,
                    userId,
                    request.getAuctionId(),
                    request.isExpeditedShipping(),
                    request.getCardNumber(),
                    request.getCardName(),
                    request.getExpiryDate(),
                    request.getSecurityCode()
            );
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
