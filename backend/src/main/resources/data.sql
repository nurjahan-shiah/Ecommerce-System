-- Sample users (password should be hashed in real implementation)
INSERT OR IGNORE INTO users (username, password, role, email, first_name, last_name, street_number, street_name, city, country, postal_code) 
VALUES 
('testuser1', 'password123', 'BUYER', 'testuser1@gmail.com','John', 'Doe', '123', 'Main St', 'Toronto', 'Canada', 'M5V 2T6'),
('testuser2', 'password123', 'BUYER', 'testuser2@gmail.com','Jane', 'Smith', '456', 'Oak Ave', 'Toronto', 'Canada', 'M4W 1A5');

-- Sample catalogue items
INSERT OR IGNORE INTO catalogue_items (item_name, description, starting_price, shipping_price, keywords)
VALUES 
('Laptop Dell XPS 15', 'High-performance laptop', 1200.00, 25.00, 'laptop,computer,dell'),
('iPhone 14 Pro', 'Latest Apple smartphone', 999.00, 15.00, 'phone,iphone,apple,smartphone'),
('Sony WH-1000XM5', 'Noise-cancelling headphones', 350.00, 10.00, 'headphones,audio,sony');
