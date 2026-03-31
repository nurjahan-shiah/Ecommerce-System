package com.yorku.auction.controller;

import com.yorku.auction.service.GeminiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AIChatController — UC8 Distinguishable Feature
 *
 * Exposes a REST endpoint that the frontend calls to get AI-powered
 * auction assistance via Google Gemini (Vertex AI).
 *
 * Endpoints:
 *   POST /api/ai/chat  — send a message, get a Gemini response
 *   GET  /api/ai/health — confirm the AI service is reachable
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIChatController {

    private final GeminiChatService geminiChatService;

    @Autowired
    public AIChatController(GeminiChatService geminiChatService) {
        this.geminiChatService = geminiChatService;
    }

    /**
     * POST /api/ai/chat
     * Body: { "message": "...", "context": "..." }
     * Returns: { "response": "..." }
     */
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> body) {
        String userMessage = body.get("message");
        String context     = body.getOrDefault("context", "");

        if (userMessage == null || userMessage.isBlank()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Message field is required");
            return ResponseEntity.badRequest().body(err);
        }

        try {
            String reply = geminiChatService.chat(userMessage, context);
            Map<String, String> response = new HashMap<>();
            response.put("response", reply);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "AI service unavailable: " + e.getMessage());
            return ResponseEntity.status(503).body(err);
        }
    }

    /**
     * GET /api/ai/health
     * Returns: "Atlas AI (Gemini) is running!"
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Atlas AI (Gemini) is running!");
    }
}
