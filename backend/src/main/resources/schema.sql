-- ================================================================
-- SmartStock Enterprise - Supabase PostgreSQL Schema
-- Run this in Supabase SQL Editor to create all required tables
-- ================================================================

-- Products
CREATE TABLE IF NOT EXISTS products (
  id            SERIAL PRIMARY KEY,
  name          VARCHAR(255) NOT NULL,
  weight        VARCHAR(50),
  price         DECIMAL(10,2) NOT NULL,
  original_price DECIMAL(10,2),
  stock         INTEGER NOT NULL DEFAULT 0,
  category      VARCHAR(100) NOT NULL,
  image_url     TEXT,
  is_flash_sale BOOLEAN DEFAULT false,
  version       BIGINT DEFAULT 0
);

-- Cart Locks (atomic 7-minute locks for low-stock items)
CREATE TABLE IF NOT EXISTS cart_locks (
  id           SERIAL PRIMARY KEY,
  phone        VARCHAR(15) NOT NULL,
  product_id   INTEGER NOT NULL REFERENCES products(id),
  qty          INTEGER NOT NULL DEFAULT 1,
  lock_expires TIMESTAMP NOT NULL,
  status       VARCHAR(20) NOT NULL DEFAULT 'active',
  created_at   TIMESTAMP DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_cart_locks_phone   ON cart_locks(phone);
CREATE INDEX IF NOT EXISTS idx_cart_locks_product ON cart_locks(product_id);
CREATE INDEX IF NOT EXISTS idx_cart_locks_status  ON cart_locks(status);
CREATE INDEX IF NOT EXISTS idx_cart_locks_expires ON cart_locks(lock_expires);

-- Orders
CREATE TABLE IF NOT EXISTS orders (
  id                 SERIAL PRIMARY KEY,
  phone              VARCHAR(15) NOT NULL,
  items              JSONB NOT NULL,
  total              DECIMAL(10,2) NOT NULL,
  status             VARCHAR(20) NOT NULL DEFAULT 'pending',
  razorpay_order_id  VARCHAR(100),
  razorpay_payment_id VARCHAR(100),
  delivery_address   TEXT,
  delivery_slot      VARCHAR(100),
  created_at         TIMESTAMP DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_orders_phone  ON orders(phone);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);

-- Inventory Audit Trail
CREATE TABLE IF NOT EXISTS inventory_audit (
  id          SERIAL PRIMARY KEY,
  admin_phone VARCHAR(15),
  product_id  INTEGER NOT NULL,
  qty_change  INTEGER NOT NULL,
  action      VARCHAR(50) NOT NULL,
  notes       TEXT,
  timestamp   TIMESTAMP DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_audit_product ON inventory_audit(product_id);

-- Users (registered via OTP)
CREATE TABLE IF NOT EXISTS users (
  id         SERIAL PRIMARY KEY,
  phone      VARCHAR(15) NOT NULL UNIQUE,
  role       VARCHAR(20) NOT NULL DEFAULT 'USER',
  created_at TIMESTAMP DEFAULT NOW(),
  last_login TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);

-- OTP Codes (for phone authentication)
CREATE TABLE IF NOT EXISTS otp_codes (
  id         SERIAL PRIMARY KEY,
  phone      VARCHAR(15) NOT NULL,
  code       VARCHAR(6) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  used       BOOLEAN DEFAULT false,
  created_at TIMESTAMP DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_otp_phone ON otp_codes(phone);

-- ================================================================
-- SEED: Insert sample products
-- ================================================================
INSERT INTO products (name, weight, price, original_price, stock, category, image_url, is_flash_sale, version) VALUES
('Kellogg''s Corn Flakes', '500g', 189, 220, 22, 'Breakfast Cereals', 'https://m.media-amazon.com/images/I/81cYjr7yo0L._SL1500_.jpg', false, 0),
('Quaker Oats Classic', '1kg', 249, 290, 7, 'Oats & Muesli', 'https://m.media-amazon.com/images/I/71d0wtpbxJL._SL1500_.jpg', false, 0),
('Muesli Fruit & Nut', '500g', 319, 370, 4, 'Oats & Muesli', 'https://m.media-amazon.com/images/I/81SRIDaDC3L.jpg', true, 0),
('Maggi Hot & Sweet Ketchup', '900g', 145, 170, 30, 'Tomato Sauce', 'https://m.media-amazon.com/images/I/61vRgADDsYL._SL1500_.jpg', false, 0),
('Kissan Mixed Fruit Jam', '500g', 165, 192, 18, 'Honey & Spreads', 'https://m.media-amazon.com/images/I/51s7o0usoCL.jpg', false, 0),
('Dabur Honey Pure', '500g', 220, 260, 9, 'Honey & Spreads', 'https://m.media-amazon.com/images/I/71LAn7mX1GL._SL1500_.jpg', false, 0),
('MyFitness Peanut Butter Crunchy', '1kg', 399, 480, 3, 'Peanut Butter', 'https://m.media-amazon.com/images/I/71p-Y-p-8eL._SL1500_.jpg', true, 0),
('Alpino Chocolate Peanut Butter', '1kg', 449, 520, 6, 'Peanut Butter', 'https://m.media-amazon.com/images/I/71nJtF2T4YL.jpg', false, 0),
('Tata Tea Premium', '500g', 259, 290, 35, 'Tea & Coffee Powder', 'https://m.media-amazon.com/images/I/714gtsczZwL._SL1500_.jpg', false, 0),
('Nescafe Classic Coffee', '200g', 429, 499, 12, 'Tea & Coffee Powder', 'https://m.media-amazon.com/images/I/71kSJv+gUIL._SL1500_.jpg', false, 0),
('MTR Instant Khichdi Mix', '300g', 79, 95, 8, 'Batter & Mixes', 'https://m.media-amazon.com/images/I/81x0f1V-W4L.jpg', false, 0),
('Everest Chicken Masala', '100g', 55, 65, 25, 'Masala Powders', 'https://m.media-amazon.com/images/I/81I-R9n78ML._SL1500_.jpg', false, 0),
('MDH Garam Masala', '100g', 65, 80, 20, 'Masala Powders', 'https://m.media-amazon.com/images/I/61MyqJ4PkEL.jpg', false, 0),
('Premium Cashews Whole', '500g', 549, 650, 5, 'Dry Fruits', 'https://m.media-amazon.com/images/I/711pYeieACL._SL1500_.jpg', false, 0),
('Almonds California Raw', '500g', 499, 580, 11, 'Dry Fruits', 'https://m.media-amazon.com/images/I/81bXeu5KTOL._SL1500_.jpg', false, 0),
('Medjool Dates Premium', '500g', 389, 450, 7, 'Dates & Mixed Seeds', 'https://m.media-amazon.com/images/I/71eFCPT0lIL._SL1500_.jpg', false, 0),
('Sunflower Seeds Roasted', '250g', 99, 120, 0, 'Dates & Mixed Seeds', 'https://m.media-amazon.com/images/I/71u92fsh1uL.jpg', false, 0),
('Green Cardamom Whole', '100g', 225, 270, 14, 'Whole Spices', 'https://m.media-amazon.com/images/I/61KqfGQ8nEL._SL1500_.jpg', false, 0),
('Tata Rock Salt Sendha', '1kg', 65, 80, 40, 'Salt, Sugar & Jaggery', 'https://m.media-amazon.com/images/I/510-o9RtOVL._SL1000_.jpg', false, 0),
('Aashirvaad Whole Wheat Atta', '5kg', 299, 340, 22, 'Atta', 'https://m.media-amazon.com/images/I/91ytlLbe1oL._SL1500_.jpg', false, 0),
('Fortune Sunflower Oil', '2L', 289, 340, 14, 'Cooking Oil', 'https://m.media-amazon.com/images/I/81mWTR+nfdL._SL1500_.jpg', false, 0),
('Organic Sona Masuri Rice', '5kg', 399, 460, 8, 'Rice', 'https://m.media-amazon.com/images/I/71e0tntLp1L._SL1500_.jpg', false, 0),
('Amul Pure Ghee', '1L',  599, 680, 8, 'Ghee', 'https://m.media-amazon.com/images/I/51SeHKW4jHL._SL1500_.jpg', false, 0),
('Chana Dal Yellow Split', '1kg', 129, 155, 32, 'Dals & Pulses', 'https://m.media-amazon.com/images/I/71RXCg+DvuL._SL1000_.jpg', false, 0),
('Moong Dal Washed', '1kg', 149, 175, 28, 'Dals & Pulses', 'https://m.media-amazon.com/images/I/81Mst76D5oL.jpg', false, 0),
('Fresh Chicken Curry Cut', '1kg', 249, 290, 8, 'Chicken', 'https://m.media-amazon.com/images/I/718yG6d+eNL._SL1500_.jpg', false, 0),
('Rohu Fish Fresh', '1kg', 199, 249, 4, 'Fish & Seafood', 'https://m.media-amazon.com/images/I/61-yeci9imL._SL1500_.jpg', true, 0),
('Farm Fresh Eggs', '12pcs', 89, 105, 20, 'Eggs', 'https://m.media-amazon.com/images/I/8176oVxtf0L._SL1500_.jpg', false, 0),
('Rajdhani Besan Fine', '500g', 75, 90, 33, 'Besan, Sooji & Maida', 'https://m.media-amazon.com/images/I/71B96gbMKwL.jpg', false, 0),
('Breakfast Combo Pack', 'Assorted', 799, 950, 3, 'Breakfast Cereals', 'https://m.media-amazon.com/images/I/71Y15-pOfDL.jpg', true, 0),
('Poha Thick Flattened Rice', '500g', 65, 80, 26, 'Rice', 'https://m.media-amazon.com/images/I/81MlvT-o5tL.jpg', false, 0),
('Organic Jaggery Powder', '500g', 110, 135, 13, 'Salt, Sugar & Jaggery', 'https://m.media-amazon.com/images/I/71Yy8gW9TML.jpg', false, 0)
ON CONFLICT DO NOTHING;
