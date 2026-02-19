package com.yorku.auction.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String role = "BUYER";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Profile fields

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String first_name;

    @Column(name = "last_name", nullable = false)
    private String last_name;

    @Column(name = "street_name", nullable = false)
    private String street_name;

    @Column(name = "street_number", nullable = false)
    private String street_number;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    @Column(name = "postal_code", nullable = false)
    private String postal_code;

    // =====================
    // Constructors
    // =====================

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    // =====================
    // Getters & Setters
    // =====================

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }

    public String getStreet_name() { return street_name; }
    public void setStreet_name(String street_name) { this.street_name = street_name; }

    public String getStreet_number() { return street_number; }
    public void setStreet_number(String street_number) { this.street_number = street_number; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPostal_code() { return postal_code; }
    public void setPostal_code(String postal_code) { this.postal_code = postal_code; }
}
