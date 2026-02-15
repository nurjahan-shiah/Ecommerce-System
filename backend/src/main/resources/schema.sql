-- Users table for Identity Service
CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    street_number VARCHAR(10),
    street_name VARCHAR(100),
    city VARCHAR(50),
    country VARCHAR(50),
    postal_code VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Catalogue items
CREATE TABLE IF NOT EXISTS catalogue_items (
    item_id INTEGER PRIMARY KEY AUTOINCREMENT,
    item_name VARCHAR(100) NOT NULL,
    description TEXT,
    starting_price DECIMAL(10,2) NOT NULL,
    shipping_price DECIMAL(10,2) DEFAULT 0,
    expedited_shipping_price DECIMAL(10,2) DEFAULT 0,
    shipping_days INTEGER DEFAULT 7,
    keywords TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Auctions
CREATE TABLE IF NOT EXISTS auctions (
    auction_id INTEGER PRIMARY KEY AUTOINCREMENT,
    item_id INTEGER NOT NULL,
    seller_id INTEGER NOT NULL,
    auction_type VARCHAR(20) DEFAULT 'FORWARD',
    start_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_time DATETIME NOT NULL,
    current_price DECIMAL(10,2) NOT NULL,
    highest_bidder_id INTEGER,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    FOREIGN KEY (item_id) REFERENCES catalogue_items(item_id),
    FOREIGN KEY (seller_id) REFERENCES users(user_id),
    FOREIGN KEY (highest_bidder_id) REFERENCES users(user_id)
);

-- Bids
CREATE TABLE IF NOT EXISTS bids (
    bid_id INTEGER PRIMARY KEY AUTOINCREMENT,
    auction_id INTEGER NOT NULL,
    bidder_id INTEGER NOT NULL,
    bid_amount DECIMAL(10,2) NOT NULL,
    bid_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (auction_id) REFERENCES auctions(auction_id),
    FOREIGN KEY (bidder_id) REFERENCES users(user_id)
);

-- Payments
CREATE TABLE IF NOT EXISTS payments (
    payment_id INTEGER PRIMARY KEY AUTOINCREMENT,
    auction_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    card_number VARCHAR(16),
    card_name VARCHAR(100),
    expedited_shipping BOOLEAN DEFAULT FALSE,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (auction_id) REFERENCES auctions(auction_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
