package com.yorku.auction.model;

import jakarta.persistence.*;

@Entity
@Table(name = "catalogue_items")
public class CatalogueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_name", nullable = false)
    private String item_name;

    @Column(nullable = false)
    private String description;

    @Column(name = "starting_price", nullable = false)
    private Double starting_price;
    
    @Column(name = "current_price", nullable = false)
    private Double current_price;

    @Column(name = "shipping_price", nullable = false)
    private Double shipping_price;

    @Column(name = "auction_type", nullable = false)
    private String auction_type = "FORWARD";

    @Column(nullable = false)
    private String keywords;

    // as stored in DB (e.g., "2h 15m" or "120" etc.)
    @Column(name = "time_remaining")
    private String time_remaining;

    public CatalogueItem() {}

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getItem_name() { return item_name; }
    public void setItem_name(String item_name) { this.item_name = item_name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getStarting_price() { return starting_price; }
    public void setStarting_price(Double starting_price) { this.starting_price = starting_price; }
    
    public Double getCurrent_price() { return current_price; }
    public void setCurrent_price(Double current_price) { this.current_price = current_price; }

    public Double getShipping_price() { return shipping_price; }
    public void setShipping_price(Double shipping_price) { this.shipping_price = shipping_price; }

    public String getAuction_type() { return auction_type; }
    public void setAuction_type(String auction_type) { this.auction_type = auction_type; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public String getTime_remaining() { return time_remaining; }
    public void setTime_remaining(String time_remaining) { this.time_remaining = time_remaining; }
}
