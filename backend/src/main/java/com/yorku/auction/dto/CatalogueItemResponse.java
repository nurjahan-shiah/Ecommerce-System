package com.yorku.auction.dto;

public class CatalogueItemResponse {
    public Long itemId;
    public Long auctionId;
    public String itemName;
    public String auctionType;
    public double currentPrice;
    public long remainingSeconds;

    public CatalogueItemResponse(Long itemId, Long auctionId, String itemName,
                                 String auctionType, double currentPrice, long remainingSeconds) {
        this.itemId = itemId;
        this.auctionId = auctionId;
        this.itemName = itemName;
        this.auctionType = auctionType;
        this.currentPrice = currentPrice;
        this.remainingSeconds = remainingSeconds;
    }
}
