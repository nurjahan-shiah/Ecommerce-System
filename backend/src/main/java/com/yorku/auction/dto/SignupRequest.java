package com.yorku.auction.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SignupRequest {
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    public String email;
    
    @NotBlank(message = "Password is required")
    public String password;
    
    @NotBlank(message = "Username is required")
    public String username;
    
    @NotBlank(message = "First Name is required")
    public String first_name;
    
    @NotBlank(message = "Last Name is required")
    public String last_name;
    
    @NotBlank(message = "Street Name is required")
    public String street_name;
    
    @NotBlank(message = "Street Number is required")
    public String street_number;
    
    @NotBlank(message = "City is required")
    public String city;
    
    @NotBlank(message = "Country is required")
    public String country;

	@NotBlank(message = "Postal Code is required")
    public String postal_code;
    
    public String role = "BUYER"; // default role: BUYER or SELLER
    
    // Constructors
    public SignupRequest() {}
    
    public SignupRequest(String email, 
    					 String password, 
    					 String role, 
    					 String username, 
    					 String first_name, 
    					 String last_name, 
    					 String street_name, 
    					 String street_number, 
    					 String city, 
    					 String country, 
    					 String postal_code) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.username = username;
        this.first_name = first_name;    
        this.last_name = last_name;
        this.street_name = street_name;
        this.street_number = street_number;
        this.city = city;
        this.country = country;
        this.postal_code = postal_code;
        
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
    
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getStreet_name() {
		return street_name;
	}

	public void setStreet_name(String street_name) {
		this.street_name = street_name;
	}

	public String getStreet_number() {
		return street_number;
	}

	public void setStreet_number(String street_number) {
		this.street_number = street_number;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}
}