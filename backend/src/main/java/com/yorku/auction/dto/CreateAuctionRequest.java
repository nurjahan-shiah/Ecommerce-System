package com.yorku.auction.dto;

import jakarta.validation.constraints.*;

public class CreateAuctionRequest {
    @NotBlank(message = "Item name is required")
    private String itemName;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Starting price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private Double startingPrice;
    
    private Integer durationHours;
    
    @DecimalMin(value = "0.1", message = "Duration minutes must be positive")
    private Double durationMinutes;
    
    @NotBlank(message = "Keywords are required")
    private String keywords;
    
    @DecimalMin(value = "0.0")
    private Double shippingPrice = 0.0;
    
    @DecimalMin(value = "0.0") 
    private Double expeditedShippingPrice = 0.0;
    
    private Integer shippingDays = 7;

    // Getters & Setters
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getStartingPrice() { return startingPrice; }
    public void setStartingPrice(Double startingPrice) { this.startingPrice = startingPrice; }
    
    public Integer getDurationHours() { return durationHours; }
    public void setDurationHours(Integer durationHours) { this.durationHours = durationHours; }
    
    public Double getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Double durationMinutes) { this.durationMinutes = durationMinutes; }
    
    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }
    
    public Double getShippingPrice() { return shippingPrice; }
    public void setShippingPrice(Double shippingPrice) { this.shippingPrice = shippingPrice; }
    
    public Double getExpeditedShippingPrice() { return expeditedShippingPrice; }
    public void setExpeditedShippingPrice(Double expeditedShippingPrice) { this.expeditedShippingPrice = expeditedShippingPrice; }
    
    public Integer getShippingDays() { return shippingDays; }
    public void setShippingDays(Integer shippingDays) { this.shippingDays = shippingDays; }
}
