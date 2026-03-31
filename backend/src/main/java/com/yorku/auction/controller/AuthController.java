package com.yorku.auction.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import com.yorku.auction.dto.LoginRequest;
import com.yorku.auction.dto.SignupRequest;
import com.yorku.auction.model.User;
import com.yorku.auction.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    // UC1: Sign Up
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            User user = userService.register(request);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", user.getUser_id());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            response.put("username", user.getUsername());
            response.put("first_name", user.getFirst_name());
            response.put("last_name", user.getLast_name());
            response.put("street_name", user.getStreet_name());
            response.put("street_number", user.getStreet_number());
            response.put("city", user.getCity());
            response.put("country", user.getCountry());
            response.put("postal_code", user.getPostal_code());
            // HATEOAS _links
            Map<String, Object> links = new LinkedHashMap<>();
            links.put("self",   Map.of("href", "/api/auth/signup", "method", "POST"));
            links.put("login",  Map.of("href", "/api/auth/login",  "method", "POST"));
            links.put("browse", Map.of("href", "/api/catalogue/items/active", "method", "GET"));
            response.put("_links", links);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    // UC1: Sign In
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            User user = userService.getUserByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
            String sessionId = UUID.randomUUID().toString();
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Login successful");
            response.put("sessionId", sessionId);
            response.put("userId", user.getUser_id());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            response.put("username", user.getUsername());
            response.put("first_name", user.getFirst_name());
            response.put("last_name", user.getLast_name());
            response.put("street_name", user.getStreet_name());
            response.put("street_number", user.getStreet_number());
            response.put("city", user.getCity());
            response.put("country", user.getCountry());
            response.put("postal_code", user.getPostal_code());
            // HATEOAS _links - what caller can do next
            Map<String, Object> links = new LinkedHashMap<>();
            links.put("self",      Map.of("href", "/api/auth/login", "method", "POST"));
            links.put("browse",    Map.of("href", "/api/catalogue/items/active", "method", "GET"));
            links.put("search",    Map.of("href", "/api/catalogue/items?keyword={keyword}", "method", "GET", "templated", true));
            links.put("placeBid",  Map.of("href", "/api/bids", "method", "POST"));
            links.put("selection", Map.of("href", "/api/session/selection", "method", "POST"));
            if ("SELLER".equalsIgnoreCase(user.getRole())) {
                links.put("createAuction", Map.of("href", "/api/seller/auctions", "method", "POST"));
            }
            response.put("_links", links);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}
