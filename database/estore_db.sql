-- ==========================================================
-- Script tạo cơ sở dữ liệu cho chức năng đăng ký/đăng nhập/hồ sơ
-- ==========================================================

CREATE DATABASE IF NOT EXISTS estore_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE estore_db;

CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(64)  NOT NULL,   -- mật khẩu đã băm SHA-256 (64 ký tự hex)
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    phone       VARCHAR(20),
    address     VARCHAR(255),
    is_admin    TINYINT(1) NOT NULL DEFAULT 0,   -- 1 = quản trị viên, được vào /admin
    is_locked   TINYINT(1) NOT NULL DEFAULT 0,   -- 1 = tài khoản bị khoá, không đăng nhập được
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Nếu bảng users đã tồn tại từ trước lúc chưa có cột is_admin (chạy lại script trên DB cũ) thì thêm vào an toàn
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_admin TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_locked TINYINT(1) NOT NULL DEFAULT 0;

-- Tài khoản mẫu để test đăng nhập nhanh (đồng thời là tài khoản quản trị)
-- username: admin / password: 123456
-- (hash SHA-256 của chuỗi "123456")
INSERT INTO users (username, password, full_name, email, phone, address, is_admin)
VALUES (
    'admin',
    '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
    'Quản Trị Viên',
    'admin@example.com',
    '0900000000',
    'Hà Nội, Việt Nam',
    1
)
ON DUPLICATE KEY UPDATE is_admin = 1;

-- ==========================================================
-- Bảng sản phẩm: dùng cho trang danh sách / tìm kiếm / chi tiết sản phẩm
-- (ProductDAO đọc trực tiếp từ bảng này thay cho dữ liệu tĩnh trước đây)
-- ==========================================================

CREATE TABLE IF NOT EXISTS products (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    price       DOUBLE NOT NULL,
    image       VARCHAR(255),
    category    VARCHAR(100),
    description TEXT,
    quantity    INT DEFAULT 0
);

INSERT INTO products (id, name, price, image, category, description, quantity) VALUES
(1, 'Áo Thun Cotton Unisex Form Rộng Basic', 99000, 'product-1.jpg', 'Áo', 'Áo thun cotton mềm, form rộng thời trang', 50),
(2, 'Áo Hoodie Nỉ Nam Cao Cấp', 299000, 'product-2.jpg', 'Áo', 'Hoodie giữ ấm, phong cách trẻ trung', 30),
(3, 'Giày Sneaker Nam Thời Trang Đế Êm', 389000, 'product-3.jpg', 'Giày', 'Giày sneaker đi học đi chơi', 40),
(4, 'Áo Khoác Gió Nam Chống Nước', 459000, 'product-4.jpg', 'Áo', 'Áo khoác gió thể thao', 25),
(5, 'Quần Jogger Nam Thể Thao', 259000, 'product-5.jpg', 'Quần', 'Quần jogger co giãn thoải mái', 60),
(6, 'Quần Jean Nam Slim Fit', 499000, 'product-6.jpg', 'Quần', 'Quần jean phong cách hiện đại', 35),
(7, 'Giày Chạy Bộ Nam Sport', 699000, 'product-7.jpg', 'Giày', 'Giày chạy bộ nhẹ và êm chân', 20),
(8, 'Áo Polo Nam Cao Cấp', 199000, 'product-8.jpg', 'Áo', 'Áo polo lịch sự, dễ phối đồ', 45),
(9, 'Balo Laptop Thời Trang', 259000, 'product-9.jpg', 'Phụ kiện', 'Balo chống nước đựng laptop', 15),
(10, 'Mũ Lưỡi Trai Unisex', 89000, 'product-10.jpg', 'Phụ kiện', 'Mũ thời trang cá tính', 70)
ON DUPLICATE KEY UPDATE
    name = VALUES(name), price = VALUES(price), image = VALUES(image),
    category = VALUES(category), description = VALUES(description), quantity = VALUES(quantity);

-- ==========================================================
-- Bảng giỏ hàng: lưu giỏ hàng xuống DB thay vì chỉ giữ trong session.
-- cart_key: "u<user_id>" nếu đã đăng nhập, "g<session_id>" nếu là khách (chưa đăng nhập).
-- Khi khách đăng nhập, giỏ hàng "g..." sẽ được gộp vào giỏ "u..." (xem CartServlet/LoginServlet).
-- ==========================================================

CREATE TABLE IF NOT EXISTS cart_items (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    cart_key     VARCHAR(150) NOT NULL,
    product_id   INT NOT NULL,
    variant_name VARCHAR(100) NOT NULL DEFAULT '',
    quantity     INT NOT NULL DEFAULT 1,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_cart_item (cart_key, product_id, variant_name),
    CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- ==========================================================
-- Đặt hàng: 1 đơn hàng (orders) có nhiều dòng sản phẩm (order_items).
-- order_items lưu lại tên/giá sản phẩm TẠI THỜI ĐIỂM đặt hàng (snapshot),
-- để sau này admin có đổi tên/giá sản phẩm thì đơn hàng cũ vẫn giữ đúng lịch sử.
-- ==========================================================

CREATE TABLE IF NOT EXISTS orders (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT NOT NULL,
    receiver_name    VARCHAR(150) NOT NULL,
    receiver_phone   VARCHAR(30) NOT NULL,
    shipping_address VARCHAR(255) NOT NULL,
    note             VARCHAR(255),
    total_amount     DECIMAL(12,2) NOT NULL,
    status           VARCHAR(30) NOT NULL DEFAULT 'Chờ xác nhận',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS order_items (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    order_id     INT NOT NULL,
    product_id   INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_image VARCHAR(255),
    unit_price   DECIMAL(12,2) NOT NULL,
    quantity     INT NOT NULL,
    CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- ==========================================================
-- Đánh giá / bình luận sản phẩm. Chỉ cho phép đánh giá nếu tài khoản đó đã
-- từng mua sản phẩm (kiểm tra qua order_items, xem ReviewDAO#hasPurchased).
-- Mỗi user chỉ đánh giá 1 lần / 1 sản phẩm (unique key bên dưới).
-- ==========================================================

CREATE TABLE IF NOT EXISTS reviews (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    user_id    INT NOT NULL,
    rating     TINYINT NOT NULL,   -- 1 đến 5 sao
    comment    VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_review_user_product (product_id, user_id),
    CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
