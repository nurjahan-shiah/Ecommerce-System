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

    /**
     * UC4/UC5: Pay Now endpoint
     * Only winner can pay for ended auction with optional expedited shipping
     */
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

    /**
     * GET payment status for a specific auction (bonus endpoint)
     */
    @GetMapping("/{auctionId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable Long auctionId,
                                            @RequestHeader("X-User-Id") Long userId) {
        try {
            // This would call a service method to check payment status
            Map<String, Object> status = new HashMap<>();
            status.put("auctionId", auctionId);
            status.put("status", "COMPLETED"); // Placeholder
            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
