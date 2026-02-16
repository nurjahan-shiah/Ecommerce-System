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
    public String firstName;
    
    @NotBlank(message = "Last Name is required")
    public String lastName;
    
    @NotBlank(message = "Street Name is required")
    public String streetName;
    
    @NotBlank(message = "Street Number is required")
    public String streetNumber;
    
    @NotBlank(message = "City is required")
    public String city;
    
    @NotBlank(message = "Country is required")
    public String country;

	@NotBlank(message = "Postal Code is required")
    public String postalCode;
    
    public String role = "BUYER"; // default role: BUYER or SELLER
    
    // Constructors
    public SignupRequest() {}
    
    public SignupRequest(String email, 
    					 String password, 
    					 String role, 
    					 String username, 
    					 String firstName, 
    					 String lastName, 
    					 String streetName, 
    					 String streetNumber, 
    					 String city, 
    					 String country, 
    					 String postalCode) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
        
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
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

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
}