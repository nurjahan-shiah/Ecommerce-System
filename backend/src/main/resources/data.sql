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