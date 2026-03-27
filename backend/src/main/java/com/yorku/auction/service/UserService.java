package com.yorku.auction.service;

import com.yorku.auction.dto.LoginRequest;
import com.yorku.auction.dto.SignupRequest;
import com.yorku.auction.model.User;
import com.yorku.auction.repository.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    
	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : "BUYER");
        user.setUsername(request.getUsername());
        user.setFirst_name(request.getFirst_name());
        user.setLast_name(request.getLast_name());
        user.setStreet_name(request.getStreet_name());
        user.setStreet_number(request.getStreet_number());
        user.setCity(request.getCity());
        user.setCountry(request.getCountry());
        user.setPostal_code(request.getPostal_code());
        
        return userRepository.save(user);
    }
    
    public User login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
//        // Check password (plaintext comparison for Deliverable 2)
//        if (!user.getPassword().equals(request.getPassword())) {
//            throw new RuntimeException("Invalid email or password");
//        }
        
        return user;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    if (updatedUser.getEmail() != null) {
                        existingUser.setEmail(updatedUser.getEmail());
                    }
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
                        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }
                    if (updatedUser.getRole() != null) {
                        existingUser.setRole(updatedUser.getRole());
                    }
                    if (updatedUser.getUsername() != null) {
                        existingUser.setUsername(updatedUser.getUsername());
                    }
                    if (updatedUser.getFirst_name() != null) {
                        existingUser.setFirst_name(updatedUser.getFirst_name());
                    }
                    if (updatedUser.getLast_name() != null) {
                        existingUser.setLast_name(updatedUser.getLast_name());
                    }
                    if (updatedUser.getStreet_name() != null) {
                        existingUser.setStreet_name(updatedUser.getStreet_name());
                    }
                    if (updatedUser.getStreet_number() != null) {
                        existingUser.setStreet_number(updatedUser.getStreet_number());
                    }
                    if (updatedUser.getCity() != null) {
                        existingUser.setCity(updatedUser.getCity());
                    }
                    if (updatedUser.getCountry() != null) {
                        existingUser.setCountry(updatedUser.getCountry());
                    }
                    if (updatedUser.getPostal_code() != null) {
                        existingUser.setPostal_code(updatedUser.getPostal_code());
                    }
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