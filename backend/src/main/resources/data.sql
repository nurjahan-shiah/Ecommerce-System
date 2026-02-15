-- Sample users (password should be hashed in real implementation)
INSERT OR IGNORE INTO users (username, password, first_name, last_name, city, country) 
VALUES 
('testuser1', 'password123', 'John', 'Doe', 'Toronto', 'Canada'),
('testuser2', 'password123', 'Jane', 'Smith', 'Toronto', 'Canada');

-- Sample catalogue items
INSERT OR IGNORE INTO catalogue_items (item_name, description, starting_price, shipping_price, keywords)
VALUES 
('Laptop Dell XPS 15', 'High-performance laptop', 1200.00, 25.00, 'laptop,computer,dell'),
('iPhone 14 Pro', 'Latest Apple smartphone', 999.00, 15.00, 'phone,iphone,apple,smartphone'),
('Sony WH-1000XM5', 'Noise-cancelling headphones', 350.00, 10.00, 'headphones,audio,sony');
