package com.yorku.auction.service;

import com.yorku.auction.dto.LoginRequest;
import com.yorku.auction.dto.SignupRequest;
import com.yorku.auction.model.User;
import com.yorku.auction.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    // =====================
    // AUTH METHODS
    // =====================
    
    public User register(SignupRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // NOTE: plaintext for Deliverable 2
        user.setRole(request.getRole() != null ? request.getRole() : "BUYER");
        
        // --- REQUIRED placeholders (because DB columns are NOT NULL) ---
        long ts = System.currentTimeMillis();
        user.setUsername("temp_" + ts);
        user.setFirst_name("TBD");
        user.setLast_name("TBD");
        user.setStreet_name("TBD");
        user.setStreet_number("TBD");
        user.setCity("TBD");
        user.setCountry("TBD");
        user.setPostal_code("TBD");

        
        return userRepository.save(user);
    }
    
    public User login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        // Check password (plaintext comparison for Deliverable 2)
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        return user;
    }
    
    // =====================
    // USER CRUD METHODS
    // =====================
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {

                    if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank()) {
                        existingUser.setEmail(updatedUser.getEmail());
                    }
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
                        existingUser.setPassword(updatedUser.getPassword());
                    }
                    if (updatedUser.getRole() != null && !updatedUser.getRole().isBlank()) {
                        existingUser.setRole(updatedUser.getRole());
                    }

                    if (updatedUser.getUsername() != null && !updatedUser.getUsername().isBlank())
                        existingUser.setUsername(updatedUser.getUsername());

                    if (updatedUser.getFirst_name() != null && !updatedUser.getFirst_name().isBlank())
                        existingUser.setFirst_name(updatedUser.getFirst_name());

                    if (updatedUser.getLast_name() != null && !updatedUser.getLast_name().isBlank())
                        existingUser.setLast_name(updatedUser.getLast_name());

                    if (updatedUser.getStreet_name() != null && !updatedUser.getStreet_name().isBlank())
                        existingUser.setStreet_name(updatedUser.getStreet_name());

                    if (updatedUser.getStreet_number() != null && !updatedUser.getStreet_number().isBlank())
                        existingUser.setStreet_number(updatedUser.getStreet_number());

                    if (updatedUser.getCity() != null && !updatedUser.getCity().isBlank())
                        existingUser.setCity(updatedUser.getCity());

                    if (updatedUser.getCountry() != null && !updatedUser.getCountry().isBlank())
                        existingUser.setCountry(updatedUser.getCountry());

                    if (updatedUser.getPostal_code() != null && !updatedUser.getPostal_code().isBlank())
                        existingUser.setPostal_code(updatedUser.getPostal_code());

                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}