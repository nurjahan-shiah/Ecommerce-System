-- Sample users (password should be hashed in real implementation)
INSERT OR IGNORE INTO users (username, password, role, email, first_name, last_name, street_number, street_name, city, country, postal_code) 
VALUES 
('testuser1', 'password123', 'BUYER', 'testuser1@gmail.com','John', 'Doe', '123', 'Main St', 'Toronto', 'Canada', 'M5V 2T6'),
('testuser2', 'password123', 'BUYER', 'testuser2@gmail.com','Jane', 'Smith', '456', 'Oak Ave', 'Toronto', 'Canada', 'M4W 1A5');

INSERT OR IGNORE INTO users (username, password, role, email, first_name, last_name, street_number, street_name, city, country, postal_code) 
VALUES ('seller1', 'password123', 'SELLER', 'seller1@gmail.com','Sam', 'Seller', '10', 'Seller St', 'Toronto', 'Canada', 'M1M 1M1');

-- Sample catalogue items
INSERT OR IGNORE INTO catalogue_items (item_name, description, starting_price, shipping_price, keywords)
VALUES 
('Laptop Dell XPS 15', 'High-performance laptop', 1200.00, 25.00, 'laptop,computer,dell'),
('iPhone 14 Pro', 'Latest Apple smartphone', 999.00, 15.00, 'phone,iphone,apple,smartphone'),
('Sony WH-1000XM5', 'Noise-cancelling headphones', 350.00, 10.00, 'headphones,audio,sony');

INSERT OR IGNORE INTO auctions (item_id, seller_id, auction_type, end_time, current_price, status)
VALUES 
(1, (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+2 hours'), 1200.00, 'ACTIVE'),
(2, (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+1 day'), 999.00, 'ACTIVE'),
(3, (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+3 hours'), 350.00, 'ACTIVE');


-- User 4 
INSERT OR IGNORE INTO users (username, password, role, email, first_name, last_name, 
                            street_number, street_name, city, country, postal_code) 
VALUES ('testuser4', 'password123', 'BUYER', 'testuser4@gmail.com', 'Winner', 'User', 
        '999', 'Victory St', 'Toronto', 'Canada', 'M1W 1W1');

-- ENDED AUCTION - Auction ID 5 (User 4 won)
INSERT OR IGNORE INTO catalogue_items (item_name, description, starting_price, shipping_price, 
                                       expedited_shipping_price, shipping_days, keywords) 
VALUES ('Test Winner Laptop', 'Perfect for testing UC4', 1000.00, 25.00, 20.00, 3, 'test,winner');

INSERT OR IGNORE INTO auctions (auction_id, item_id, seller_id, auction_type, end_time, current_price, 
                                status, highest_bidder_id) 
VALUES (5, 
        (SELECT item_id FROM catalogue_items WHERE item_name = 'Test Winner Laptop'),
        (SELECT user_id FROM users WHERE username = 'seller1'),
        'FORWARD', '2026-02-28 10:00:00', 1250.00, 'ENDED', 
        (SELECT user_id FROM users WHERE username = 'testuser4'));

-- BIDS HISTORY for ENDED auction (5)
INSERT OR IGNORE INTO bids (auction_id, bidder_id, bid_amount, bid_time) VALUES 
(5, (SELECT user_id FROM users WHERE username = 'testuser4'), 1200.00, '2026-02-28 09:50:00'),
(5, (SELECT user_id FROM users WHERE username = 'testuser4'), 1225.00, '2026-02-28 09:55:00'),
(5, (SELECT user_id FROM users WHERE username = 'testuser4'), 1250.00, '2026-02-28 09:58:00');


-- COMPLETED AUCTION - Auction ID 6
INSERT OR IGNORE INTO catalogue_items (item_name, description, starting_price, shipping_price, 
                                       expedited_shipping_price, shipping_days, keywords) 
VALUES ('Test Complete iPhone', 'User 4 already paid for this', 900.00, 15.00, 10.00, 2, 'test,complete');

INSERT OR IGNORE INTO auctions (auction_id, item_id, seller_id, auction_type, end_time, current_price, 
                                status, highest_bidder_id) 
VALUES (6, 
        (SELECT item_id FROM catalogue_items WHERE item_name = 'Test Complete iPhone'),
        (SELECT user_id FROM users WHERE username = 'seller1'),
        'FORWARD', '2026-02-28 09:00:00', 1100.00, 'COMPLETED', 
        (SELECT user_id FROM users WHERE username = 'testuser4'));

-- BIDS HISTORY for COMPLETED auction
INSERT OR IGNORE INTO bids (auction_id, bidder_id, bid_amount, bid_time) VALUES 
(6, (SELECT user_id FROM users WHERE username = 'testuser4'), 1000.00, '2026-02-28 08:40:00'),
(6, (SELECT user_id FROM users WHERE username = 'testuser4'), 1050.00, '2026-02-28 08:45:00'),
(6, (SELECT user_id FROM users WHERE username = 'testuser4'), 1100.00, '2026-02-28 08:58:00');

-- PAYMENT for COMPLETED auction
INSERT OR IGNORE INTO payments (auction_id, user_id, total_amount, card_number, card_name, 
                                expedited_shipping, payment_status, payment_date) 
VALUES (6, (SELECT user_id FROM users WHERE username = 'testuser4'), 1125.00, '4111111111111111', 
        'Winner User', 0, 'COMPLETED', '2026-02-28 09:10:00');
