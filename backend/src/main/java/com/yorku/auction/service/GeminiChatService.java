package com.yorku.auction.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * GeminiChatService — UC8 Distinguishable AI Feature
 */
@Service
public class GeminiChatService {

	private static final String GEMINI_URL =
		    "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent";

    @Value("${gemini.api.key:NOT_SET}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String userMessage, String context) {
        if ("NOT_SET".equals(apiKey) || apiKey.isBlank()) {
            return "AI assistant is not configured. Please set gemini.api.key in application.properties.";
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are Atlas AI for a forward auction platform. ")
              .append("Rules: bids are integers, must beat current price, highest at expiry wins. ")
              .append("Be brief and friendly (max 100 words).\n\n");

        if (context != null && !context.isBlank()) {
            String trimmedContext = context.length() > 300 ? context.substring(0, 300) : context;
            prompt.append("Auction data: ").append(trimmedContext).append("\n\n");
        }

        prompt.append("Question: ").append(userMessage);

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", prompt.toString())
                ))
            ),
            "generationConfig", Map.of("maxOutputTokens", 150)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-goog-api-key", apiKey);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                GEMINI_URL,
                new HttpEntity<>(requestBody, headers),
                Map.class
            );

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> candidates =
                (List<Map<String, Object>>) response.getBody().get("candidates");

            if (candidates == null || candidates.isEmpty()) {
                return "I couldn't generate a response. Please try again.";
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            return (String) parts.get(0).get("text");

        } catch (HttpClientErrorException e) {
            String body = e.getResponseBodyAsString();
            System.err.println("[GeminiChatService] HTTP " + e.getStatusCode() + ": " + body);
            if (body.contains("RESOURCE_EXHAUSTED")) {
                return "I'm a bit busy right now — please wait a moment and try again.";
            }
            if (body.contains("NOT_FOUND")) {
                return "AI model unavailable. Please try again shortly.";
            }
            return "I'm having trouble connecting right now. Please try again shortly.";
        } catch (Exception e) {
            System.err.println("[GeminiChatService] Error: " + e.getMessage());
            return "I'm having trouble connecting right now. Please try again shortly.";
        }
    }
}