package com.yorku.auction.dto;

import jakarta.validation.constraints.NotNull;

public class SelectionRequest {
    @NotNull
    public Long auctionId;

    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }
}
