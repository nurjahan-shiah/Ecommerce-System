package com.yorku.auction.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SignupRequest {
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    public String email;
    
    @NotBlank(message = "Password is required")
    public String password;
    
    public String role = "BUYER"; // default role: BUYER or SELLER
    
    // Constructors
    public SignupRequest() {}
    
    public SignupRequest(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
}