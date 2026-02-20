package com.yorku.auction.controller;

import com.yorku.auction.dto.SelectionRequest;
import com.yorku.auction.service.SessionSelectionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/session")
@CrossOrigin(origins="*")
public class SessionSelectionController {

    private final SessionSelectionService selectionService;

    public SessionSelectionController(SessionSelectionService selectionService) {
        this.selectionService = selectionService;
    }

    //set selection (one per session)
    @PostMapping("/selection")
    public ResponseEntity<?> setSelection(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody SelectionRequest request
    ) {
        selectionService.setSelection(sessionId, userId, request.auctionId);

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Selection saved");
        resp.put("selectedAuctionId", request.auctionId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/selection")
    public ResponseEntity<?> getSelection(@RequestHeader("X-Session-Id") String sessionId) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("selectedAuctionId", selectionService.getSelection(sessionId).orElse(null));
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/selection")
    public ResponseEntity<?> clear(@RequestHeader("X-Session-Id") String sessionId) {
        selectionService.clearSelection(sessionId);
        return ResponseEntity.noContent().build();
    }
}