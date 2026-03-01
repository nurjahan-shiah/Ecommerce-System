package com.yorku.auction.dto;


public class PayNowRequest {
    private Long auctionId;
    private boolean expeditedShipping;
    private String cardNumber;
    private String cardName;
    private String expiryDate;
    private String securityCode;
    // getters/setters
	public String getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public boolean isExpeditedShipping() {
		return expeditedShipping;
	}
	public void setExpeditedShipping(boolean expeditedShipping) {
		this.expeditedShipping = expeditedShipping;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public Long getAuctionId() {
		return auctionId;
	}
	public void setAuctionId(Long auctionId) {
		this.auctionId = auctionId;
	}
}
