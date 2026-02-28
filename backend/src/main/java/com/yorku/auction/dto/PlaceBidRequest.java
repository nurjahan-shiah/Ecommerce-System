package com.yorku.auction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PlaceBidRequest {

    @NotNull
    @Positive(message = "bidAmount must be > 0")
    public Double bidAmount;

    public Double getBidAmount() { return bidAmount; }
    public void setBidAmount(Double bidAmount) { this.bidAmount = bidAmount; }
}
