package com.yorku.auction.dto;

public class CatalogueItemResponse {
    public Long itemId;
    public Long auctionId;
    public String itemName;
    public String auctionType;
    public double currentPrice;
    public long remainingSeconds;
    public int highestBidderId;
    public String description;
    public double startingPrice;
    public double shippingPrice;
    public double expeditedShippingPrice;
    public int shippingDays;
    public String keywords;
    public String createdAt;
    public Long sellerId;
    public String startTime;
    public String endTime;
    public String status;

    public CatalogueItemResponse(Long itemId, Long auctionId, String itemName, String auctionType, 
                                double currentPrice, long remainingSeconds, int highestBidderId,
                                String description, double startingPrice, double shippingPrice,
                                double expeditedShippingPrice, int shippingDays, String keywords,
                                String createdAt, Long sellerId, String startTime, String endTime, String status) {
        this.itemId = itemId;
        this.auctionId = auctionId;
        this.itemName = itemName;
        this.auctionType = auctionType;
        this.currentPrice = currentPrice;
        this.remainingSeconds = remainingSeconds;
        this.highestBidderId = highestBidderId;
        this.description = description;
        this.startingPrice = startingPrice;
        this.shippingPrice = shippingPrice;
        this.expeditedShippingPrice = expeditedShippingPrice;
        this.shippingDays = shippingDays;
        this.keywords = keywords;
        this.createdAt = createdAt;
        this.sellerId = sellerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
}
