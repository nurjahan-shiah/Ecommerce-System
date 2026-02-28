package com.yorku.auction.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            User user = userService.register(request);

            Map<String, Object> response = new HashMap<>();
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

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);

            String sessionId = UUID.randomUUID().toString();

            Map<String, Object> response = new HashMap<>();
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
            

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}