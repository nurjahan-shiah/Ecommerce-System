-- ─── CLEANUP STALE DATA (removes duplicates from old restarts) ───────────────
DELETE FROM session_selection;
DELETE FROM auctions WHERE status = 'ACTIVE';
DELETE FROM catalogue_items WHERE item_name IN (
  'Laptop Dell XPS 15','iPhone 15 Pro','Sony WH-1000XM5',
  'Samsung 65" QLED TV','Canon EOS R6 Mark II','PlayStation 5',
  'Apple Watch Ultra 2','MacBook Air M3','Bose QuietComfort 45',
  'LG C3 OLED 55"','Nintendo Switch OLED','iPad Pro M4 13"',
  'Samsung Galaxy S24 Ultra','Google Pixel 8 Pro','OnePlus 12',
  'Test Winner Laptop','Test Complete iPhone'
);

-- ─── USERS ───────────────────────────────────────────────────────────────────
-- All passwords are bcrypt of "password123"
INSERT OR IGNORE INTO users (username, password, role, email, first_name, last_name, street_number, street_name, city, country, postal_code)
VALUES
  ('testuser1', '$2a$10$Crr5saarQ.rrZPkU2upRPOGr0H5GbZefTKthXCqrhGosS3i9WH4v6', 'BUYER',  'testuser1@gmail.com', 'John',   'Doe',    '123', 'Main St',    'Toronto', 'Canada', 'M5V 2T6'),
  ('testuser2', '$2a$10$Crr5saarQ.rrZPkU2upRPOGr0H5GbZefTKthXCqrhGosS3i9WH4v6', 'BUYER',  'testuser2@gmail.com', 'Jane',   'Smith',  '456', 'Oak Ave',    'Toronto', 'Canada', 'M4W 1A5'),
  ('testuser3', '$2a$10$Crr5saarQ.rrZPkU2upRPOGr0H5GbZefTKthXCqrhGosS3i9WH4v6', 'BUYER',  'testuser3@gmail.com', 'Mike',   'Brown',  '789', 'King St',    'Toronto', 'Canada', 'M5H 1A1'),
  ('testuser4', '$2a$10$Crr5saarQ.rrZPkU2upRPOGr0H5GbZefTKthXCqrhGosS3i9WH4v6', 'BUYER',  'testuser4@gmail.com', 'Winner', 'User',   '999', 'Victory St', 'Toronto', 'Canada', 'M1W 1W1'),
  ('seller1',   '$2a$10$Crr5saarQ.rrZPkU2upRPOGr0H5GbZefTKthXCqrhGosS3i9WH4v6', 'SELLER', 'seller1@gmail.com',   'Sam',    'Seller', '10',  'Seller St',  'Toronto', 'Canada', 'M1M 1M1'),
  ('seller2',   '$2a$10$Crr5saarQ.rrZPkU2upRPOGr0H5GbZefTKthXCqrhGosS3i9WH4v6', 'SELLER', 'seller2@gmail.com',   'Sara',   'Shop',   '20',  'Market Rd',  'Toronto', 'Canada', 'M2M 2M2');

-- ─── CATALOGUE ITEMS ─────────────────────────────────────────────────────────
INSERT OR IGNORE INTO catalogue_items (item_name, description, starting_price, shipping_price, expedited_shipping_price, shipping_days, keywords) VALUES
  ('Laptop Dell XPS 15',       'High-performance 15" laptop with Intel Core i7, 16GB RAM, 512GB SSD.',                                              1200.00, 25.00, 45.00, 5,  'laptop,computer,dell,xps'),
  ('iPhone 15 Pro',            'Latest Apple flagship with A17 Pro chip, titanium frame, 48MP camera and USB-C.',                                    999.00, 15.00, 25.00, 3,  'smartphone,iphone,apple'),
  ('Samsung Galaxy S24 Ultra', 'Samsung flagship with Snapdragon 8 Gen 3, 200MP camera, built-in S-Pen, 6.8" Dynamic AMOLED display.',               1199.00, 15.00, 28.00, 3,  'smartphone,samsung,android,galaxy'),
  ('Google Pixel 8 Pro',       'Pure Android experience with Google Tensor G3 chip, 50MP camera, 7 years of OS updates.',                             799.00, 12.00, 22.00, 4,  'smartphone,google,pixel,android'),
  ('OnePlus 12',               'Flagship killer with Snapdragon 8 Gen 3, Hasselblad-tuned 50MP camera, 100W SUPERVOOC charging.',                     699.00, 10.00, 20.00, 4,  'smartphone,oneplus,android'),
  ('Sony WH-1000XM5',         'Industry-leading noise-cancelling wireless headphones with 30hr battery and multipoint connection.',                   350.00, 10.00, 18.00, 7,  'headphones,audio,sony,wireless'),
  ('Samsung 65" QLED TV',     '65-inch 4K QLED Smart TV with Quantum HDR, 120Hz refresh rate and built-in Alexa.',                                  1500.00, 0.00,  80.00, 10, 'tv,television,samsung,qled,4k'),
  ('Canon EOS R6 Mark II',    'Full-frame mirrorless camera with 40fps burst, in-body stabilization and 6K RAW video.',                              2500.00, 20.00, 40.00, 5,  'camera,canon,mirrorless,photography'),
  ('PlayStation 5',            'Sony PS5 console with DualSense controller, 825GB SSD and ray-tracing support.',                                      499.00, 15.00, 30.00, 4,  'gaming,playstation,ps5,sony,console'),
  ('Apple Watch Ultra 2',      '49mm titanium smartwatch with precision dual-frequency GPS, 60hr battery and depth gauge.',                            799.00, 12.00, 22.00, 3,  'smartwatch,apple,wearable'),
  ('MacBook Air M3',           '15" MacBook Air with Apple M3 chip, 18hr battery, 8GB RAM and Liquid Retina display.',                               1299.00, 20.00, 38.00, 4,  'laptop,macbook,apple,m3,computer'),
  ('LG C3 OLED 55"',          '55-inch OLED evo TV with α9 AI Processor 4K, Dolby Vision IQ and NVIDIA G-Sync compatible.',                         1200.00, 0.00,  75.00, 10, 'tv,oled,lg,television,4k'),
  ('Nintendo Switch OLED',     'Nintendo Switch with vibrant 7-inch OLED screen, 64GB storage and enhanced audio.',                                    349.00, 12.00, 22.00, 5,  'gaming,nintendo,switch,console,portable'),
  ('iPad Pro M4 13"',          '13-inch iPad Pro with M4 chip, Ultra Retina XDR OLED display and Apple Pencil Pro support.',                          1099.00, 18.00, 32.00, 4,  'tablet,ipad,apple,m4'),
  -- ENDED auction item (testuser4 won)
  ('Test Winner Laptop',       'Perfect for testing UC4 — ended auction scenario.',                                                                    1000.00, 25.00, 20.00, 3,  'test,winner,laptop'),
  -- COMPLETED auction item (testuser4 already paid — UC6 receipt demo)
  ('Test Complete iPhone',     'User 4 already paid for this — UC6 receipt demo.',                                                                      900.00, 15.00, 10.00, 2,  'test,complete,iphone');

-- ─── ACTIVE AUCTIONS (staggered end times for testing) ───────────────────────
INSERT OR IGNORE INTO auctions (item_id, seller_id, auction_type, end_time, current_price, status) VALUES
  ((SELECT item_id FROM catalogue_items WHERE item_name='Laptop Dell XPS 15'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+1 minute'),   1200.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='iPhone 15 Pro'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+4 minutes'),   999.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='Samsung Galaxy S24 Ultra'),
   (SELECT user_id FROM users WHERE username='seller2'), 'FORWARD', datetime('now','+7 minutes'),  1199.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='Google Pixel 8 Pro'),
   (SELECT user_id FROM users WHERE username='seller2'), 'FORWARD', datetime('now','+12 minutes'),  799.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='OnePlus 12'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+18 minutes'),  699.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='Sony WH-1000XM5'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+25 minutes'),  350.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='Samsung 65" QLED TV'),
   (SELECT user_id FROM users WHERE username='seller2'), 'FORWARD', datetime('now','+35 minutes'), 1500.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='Canon EOS R6 Mark II'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+45 minutes'), 2500.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='PlayStation 5'),
   (SELECT user_id FROM users WHERE username='seller2'), 'FORWARD', datetime('now','+55 minutes'),  499.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='Apple Watch Ultra 2'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+70 minutes'),  799.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='MacBook Air M3'),
   (SELECT user_id FROM users WHERE username='seller2'), 'FORWARD', datetime('now','+85 minutes'), 1299.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='LG C3 OLED 55"'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+100 minutes'),1200.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='Nintendo Switch OLED'),
   (SELECT user_id FROM users WHERE username='seller2'), 'FORWARD', datetime('now','+2 hours'),    349.00, 'ACTIVE'),

  ((SELECT item_id FROM catalogue_items WHERE item_name='iPad Pro M4 13"'),
   (SELECT user_id FROM users WHERE username='seller1'), 'FORWARD', datetime('now','+3 hours'),   1099.00, 'ACTIVE');

-- ─── ENDED AUCTION (testuser4 is winner, can pay) ────────────────────────────
INSERT OR IGNORE INTO auctions (item_id, seller_id, auction_type, end_time, current_price, status, highest_bidder_id) VALUES
  ((SELECT item_id FROM catalogue_items WHERE item_name='Test Winner Laptop'),
   (SELECT user_id FROM users WHERE username='seller1'),
   'FORWARD', '2026-02-28 10:00:00', 1250.00, 'ENDED',
   (SELECT user_id FROM users WHERE username='testuser4'));

-- ─── COMPLETED AUCTION (testuser4 paid — UC6 receipt visible) ────────────────
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

-- ─── PAYMENT FOR COMPLETED AUCTION (UC6 receipt demo) ────────────────────────
INSERT OR IGNORE INTO payments (auction_id, user_id, total_amount, card_number, card_name, expedited_shipping, payment_status, payment_date)
SELECT a.auction_id, u.user_id, 1125.00, '4111111111111111', 'Winner User', 0, 'COMPLETED', '2026-02-28 09:10:00'
FROM auctions a JOIN catalogue_items ci ON a.item_id=ci.item_id
JOIN users u ON u.username='testuser4'
WHERE ci.item_name='Test Complete iPhone' AND NOT EXISTS (SELECT 1 FROM payments WHERE auction_id=a.auction_id);
