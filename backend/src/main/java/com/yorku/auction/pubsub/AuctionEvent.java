package com.yorku.auction.pubsub;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * Immutable domain event published via Spring's ApplicationEventPublisher.
 *
 * Publishers: BidService, AuctionService, PaymentService
 * Subscriber: NotificationService (which fans the event out to SSE clients)
 *
 * @param type        what happened
 * @param auctionId   the auction this event concerns
 * @param payload     arbitrary key-value bag delivered to the browser as JSON
 */
public class AuctionEvent extends ApplicationEvent {

    private final AuctionEventType type;
    private final Long             auctionId;
    private final Map<String, Object> payload;

    public AuctionEvent(Object source,
                        AuctionEventType type,
                        Long auctionId,
                        Map<String, Object> payload) {
        super(source);
        this.type      = type;
        this.auctionId = auctionId;
        this.payload   = Map.copyOf(payload);   // immutable snapshot
    }

    public AuctionEventType      getType()      { return type;      }
    public Long                  getAuctionId() { return auctionId; }
    public Map<String, Object>   getPayload()   { return payload;   }
}
