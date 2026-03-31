-- ─── USERS ───────────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO users (username, password, role, email, first_name, last_name, street_number, street_name, city, country, postal_code)
VALUES
  ('testuser1', '$2a$10$Crr5saarQ.rrZPkU2upRPOGr0H5GbZefTKthXCqrhGosS3i9WH4v6', 'BUYER',  'testuser1@gmail.com', 'John',   'Doe',    '123', 'Main St',    'Toronto', 'Canada', 'M5V 2T6'),
  ('testuser2', '$2a$10$Crr5saarQ.rrZPkU2upRPOGr0H5GbZefTKthXCqrhGosS3i9WH4v6', 'BUYER',  'testuser2@gmail.com', 'Jane',   'Smith',  '456', 'Oak Ave',    'Toronto', 'Canada', 'M4W 1A5'),
  ('testuser4', '$2a$10$Crr5saarQ.rrZPkU2upRPOGr0H5GbZefTKthXCqrhGosS3i9WH4v6', 'BUYER',  'testuser4@gmail.com', 'Winner', 'User',   '999', 'Victory St', 'Toronto', 'Canada', 'M1W 1W1'),
  ('seller1',   '$2a$10$Crr5saarQ.rrZPkU2upRPOGr0H5GbZefTKthXCqrhGosS3i9WH4v6', 'SELLER', 'seller1@gmail.com',   'Sam',    'Seller', '10',  'Seller St',  'Toronto', 'Canada', 'M1M 1M1');

-- ─── CATALOGUE ITEMS (UNIQUE on item_name — safe to re-run) ──────────────────
INSERT OR IGNORE INTO catalogue_items (item_name, description, starting_price, shipping_price, expedited_shipping_price, shipping_days, keywords) VALUES
  ('Laptop Dell XPS 15',    'High-performance 15" laptop with Intel Core i7, 16GB RAM, 512GB SSD. Perfect for developers and creators.',         1200.00, 25.00, 45.00, 5,  'laptop,computer,dell,xps'),
  ('iPhone 15 Pro',         'Latest Apple flagship with A17 Pro chip, titanium frame, 48MP camera system and USB-C.',                             999.00, 15.00, 25.00, 3,  'phone,iphone,apple,smartphone'),
  ('Sony WH-1000XM5',       'Industry-leading noise-cancelling wireless headphones with 30hr battery and multipoint connection.',                  350.00, 10.00, 18.00, 7,  'headphones,audio,sony,wireless'),
  ('Samsung 65" QLED TV',   '65-inch 4K QLED Smart TV with Quantum HDR, 120Hz refresh rate and built-in Alexa.',                                1500.00, 0.00,  80.00, 10, 'tv,television,samsung,qled,4k'),
  ('Canon EOS R6 Mark II',  'Full-frame mirrorless camera with 40fps burst, in-body stabilization and 6K RAW video.',                            2500.00, 20.00, 40.00, 5,  'camera,canon,mirrorless,photography'),
  ('PlayStation 5',         'Sony PS5 console with DualSense controller, 825GB SSD and ray-tracing support.',                                     499.00, 15.00, 30.00, 4,  'gaming,playstation,ps5,sony,console'),
  ('Apple Watch Ultra 2',   '49mm titanium smartwatch with precision dual-frequency GPS, 60hr battery and depth gauge.',                          799.00, 12.00, 22.00, 3,  'watch,apple,smartwatch,wearable'),
  ('MacBook Air M3',        '15" MacBook Air with Apple M3 chip, 18hr battery, 8GB RAM and Liquid Retina display.',                              1299.00, 20.00, 38.00, 4,  'laptop,macbook,apple,m3,computer'),
  ('Bose QuietComfort 45',  'Premium wireless headphones with adjustable EQ, 24hr battery and comfortable over-ear design.',                      279.00,  8.00, 15.00, 7,  'headphones,audio,bose,wireless,noise-cancelling'),
  ('LG C3 OLED 55"',        '55-inch OLED evo TV with α9 AI Processor 4K, Dolby Vision IQ and NVIDIA G-Sync compatible.',                       1200.00, 0.00,  75.00, 10, 'tv,oled,lg,television,4k'),
  ('Nintendo Switch OLED',  'Nintendo Switch with vibrant 7-inch OLED screen, 64GB storage and enhanced audio.',                                  349.00, 12.00, 22.00, 5,  'gaming,nintendo,switch,console,portable'),
  ('iPad Pro M4 13"',       '13-inch iPad Pro with M4 chip, Ultra Retina XDR tandem OLED display and Apple Pencil Pro support.',                 1099.00, 18.00, 32.00, 4,  'tablet,ipad,apple,m4'),
  -- ENDED auction item (testuser4 won)
  ('Test Winner Laptop',    'Perfect for testing UC4 — ended auction scenario.',                                                                   1000.00, 25.00, 20.00, 3,  'test,winner,laptop'),
  -- COMPLETED auction item (testuser4 already paid)
  ('Test Complete iPhone',  'User 4 already paid for this — completed auction scenario.',                                                           900.00, 15.00, 10.00, 2,  'test,complete,iphone');

-- ─── ACTIVE AUCTIONS ─────────────────────────────────────────────────────────
-- Each item_id is looked up by name so restarts never break the references
INSERT OR IGNORE INTO auctions (item_id, seller_id, auction_type, end_time, current_price, status) VALUES
  ((SELECT item_id FROM catalogue_items WHERE item_name='Laptop Dell XPS 15'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+30 days'), 1200.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='iPhone 15 Pro'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+7 days'),   999.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='Sony WH-1000XM5'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+3 days'),   350.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='Samsung 65" QLED TV'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+14 days'), 1500.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='Canon EOS R6 Mark II'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+10 days'), 2500.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='PlayStation 5'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+5 days'),   499.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='Apple Watch Ultra 2'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+8 days'),   799.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='MacBook Air M3'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+21 days'), 1299.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='Bose QuietComfort 45'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+6 days'),   279.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='LG C3 OLED 55"'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+12 days'), 1200.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='Nintendo Switch OLED'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+4 days'),   349.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='iPad Pro M4 13"'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+9 days'),  1099.00, 'ACTIVE');

-- ─── ENDED AUCTION (testuser4 is winner) ─────────────────────────────────────
INSERT OR IGNORE INTO auctions (item_id, seller_id, auction_type, end_time, current_price, status, highest_bidder_id) VALUES
  ((SELECT item_id FROM catalogue_items WHERE item_name='Test Winner Laptop'),
   (SELECT user_id FROM users WHERE username='seller1'),
   'FORWARD', '2026-02-28 10:00:00', 1250.00, 'ENDED',
   (SELECT user_id FROM users WHERE username='testuser4'));

-- ─── COMPLETED AUCTION (testuser4 paid) ──────────────────────────────────────
INSERT OR IGNORE INTO auctions (item_id, seller_id, auction_type, end_time, current_price, status, highest_bidder_id) VALUES
  ((SELECT item_id FROM catalogue_items WHERE item_name='Test Complete iPhone'),
   (SELECT user_id FROM users WHERE username='seller1'),
   'FORWARD', '2026-02-28 09:00:00', 1100.00, 'COMPLETED',
   (SELECT user_id FROM users WHERE username='testuser4'));

-- ─── BID HISTORY ─────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO bids (auction_id, bidder_id, bid_amount, bid_time)
SELECT a.auction_id, u.user_id, 1200.00, '2026-02-28 09:50:00'
FROM auctions a JOIN catalogue_items ci ON a.item_id=ci.item_id
JOIN users u ON u.username='testuser4'
WHERE ci.item_name='Test Winner Laptop' AND NOT EXISTS (SELECT 1 FROM bids WHERE auction_id=a.auction_id);

INSERT OR IGNORE INTO bids (auction_id, bidder_id, bid_amount, bid_time)
SELECT a.auction_id, u.user_id, 1250.00, '2026-02-28 09:58:00'
FROM auctions a JOIN catalogue_items ci ON a.item_id=ci.item_id
JOIN users u ON u.username='testuser4'
WHERE ci.item_name='Test Winner Laptop' AND NOT EXISTS (SELECT 1 FROM bids b WHERE b.auction_id=a.auction_id AND b.bid_amount=1250.00);

INSERT OR IGNORE INTO bids (auction_id, bidder_id, bid_amount, bid_time)
SELECT a.auction_id, u.user_id, 1100.00, '2026-02-28 08:58:00'
FROM auctions a JOIN catalogue_items ci ON a.item_id=ci.item_id
JOIN users u ON u.username='testuser4'
WHERE ci.item_name='Test Complete iPhone' AND NOT EXISTS (SELECT 1 FROM bids WHERE auction_id=a.auction_id);

-- ─── PAYMENT FOR COMPLETED AUCTION ───────────────────────────────────────────
INSERT OR IGNORE INTO payments (auction_id, user_id, total_amount, card_number, card_name, expedited_shipping, payment_status, payment_date)
SELECT a.auction_id, u.user_id, 1125.00, '4111111111111111', 'Winner User', 0, 'COMPLETED', '2026-02-28 09:10:00'
FROM auctions a JOIN catalogue_items ci ON a.item_id=ci.item_id
JOIN users u ON u.username='testuser4'
WHERE ci.item_name='Test Complete iPhone' AND NOT EXISTS (SELECT 1 FROM payments WHERE auction_id=a.auction_id);