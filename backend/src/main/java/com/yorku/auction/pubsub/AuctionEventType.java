package com.yorku.auction.pubsub;

/**
 * All event types that can be published on the internal pub-sub bus
 * and pushed to connected clients via SSE.
 */
public enum AuctionEventType {
    /** Someone placed a new (higher) bid on an auction */
    BID_PLACED,

    /** Auction timer expired — winner is determined */
    AUCTION_ENDED,

    /** A seller listed a new auction item */
    AUCTION_CREATED,

    /** Winner's payment was confirmed */
    PAYMENT_CONFIRMED
}
