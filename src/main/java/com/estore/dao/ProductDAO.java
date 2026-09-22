package com.estore.dao;

import com.estore.model.Product;
import com.estore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Đọc dữ liệu sản phẩm từ bảng `products` trong MySQL (thay cho ProductCatalog
 * dữ liệu tĩnh trước đây). Cấu trúc bảng:
 *
 *   products(id, name, price, image, category, description, quantity)
 *
 * Các trường không có trong DB (rating, reviewCount, soldCount, variants,
 * thông tin shop...) được gán giá trị mặc định để các trang JSP hiện tại
 * (product-list.jsp, product-detail.jsp) vẫn hiển thị được mà không cần sửa.
 */
public class ProductDAO {

    private static final String SELECT_ALL = "SELECT id, name, price, image, category, description, quantity FROM products";

    /** Lấy toàn bộ sản phẩm trong DB. */
    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();
        String sql = SELECT_ALL + " ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    /** Lấy 1 sản phẩm theo id, trả về null nếu không tồn tại. */
    public Product getById(int id) {
        String sql = SELECT_ALL + " WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /** Lấy {@code limit} sản phẩm cho khối "Featured Product" ở trang chủ (mặc định: id tăng dần). */
    public List<Product> getFeatured(int limit) {
        List<Product> result = new ArrayList<>();
        String sql = SELECT_ALL + " ORDER BY id ASC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /** Lấy {@code limit} sản phẩm mới nhất (id giảm dần) cho khối "Recent Product" ở trang chủ. */
    public List<Product> getRecent(int limit) {
        List<Product> result = new ArrayList<>();
        String sql = SELECT_ALL + " ORDER BY id DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /** Danh sách các danh mục (không trùng lặp) đang có sản phẩm trong DB, theo thứ tự id tăng dần. */
    public List<String> getCategories() {
        LinkedHashSet<String> categories = new LinkedHashSet<>();
        String sql = "SELECT DISTINCT category FROM products WHERE category IS NOT NULL ORDER BY category ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(categories);
    }

    /**
     * Trả về tối đa {@code limit} sản phẩm khác (không tính sản phẩm hiện tại),
     * dùng cho khối "Sản phẩm liên quan" ở cuối trang chi tiết.
     */
    public List<Product> getRelated(int excludeId, int limit) {
        List<Product> result = new ArrayList<>();
        String sql = SELECT_ALL + " WHERE id <> ? ORDER BY id ASC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, excludeId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /** Thêm sản phẩm mới, trả về id vừa tạo (hoặc -1 nếu thất bại). */
    public int insert(Product p) {
        String sql = "INSERT INTO products (name, price, image, category, description, quantity) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setDouble(2, p.getPrice());
            ps.setString(3, p.getImageFileName());
            ps.setString(4, p.getCategory());
            ps.setString(5, p.getDescription());
            ps.setInt(6, p.getQuantity());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    /** Cập nhật thông tin sản phẩm theo id. */
    public boolean update(Product p) {
        String sql = "UPDATE products SET name=?, price=?, image=?, category=?, description=?, quantity=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setDouble(2, p.getPrice());
            ps.setString(3, p.getImageFileName());
            ps.setString(4, p.getCategory());
            ps.setString(5, p.getDescription());
            ps.setInt(6, p.getQuantity());
            ps.setInt(7, p.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Xoá sản phẩm theo id. */
    public boolean delete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Đếm tổng số sản phẩm, dùng cho dashboard admin. */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Danh sách sản phẩm có tồn kho <= threshold, dùng để cảnh báo sắp hết hàng. */
    public List<Product> getLowStock(int threshold) {
        List<Product> result = new ArrayList<>();
        String sql = SELECT_ALL + " WHERE quantity <= ? ORDER BY quantity ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    // ---------------------------------------------------------------

    private Product mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        double price = rs.getDouble("price");
        String image = rs.getString("image");
        String category = rs.getString("category");
        String description = rs.getString("description");
        int quantity = rs.getInt("quantity");

        String imagePath = (image == null || image.trim().isEmpty()) ? "img/logo.png" : "img/" + image;

        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setCategory(category);
        p.setBreadcrumb(Arrays.asList("Trang Chủ", category == null ? "" : category, name));
        p.setImages(new ArrayList<>(Arrays.asList(imagePath)));
        p.setImageFileName(image);
        p.setDescription(description);
        p.setQuantity(quantity);
        p.setHighlights(new ArrayList<>());
        p.setShippingInfo("Giao Hoả Tốc 4 Giờ (Đặt hàng trước 18:00) - Miễn phí vận chuyển đơn từ 150.000đ");

        // Chưa có bảng đánh giá/lượt bán riêng -> để mặc định 0, không hiển thị sai lệch dữ liệu
        p.setRating(0);
        p.setReviewCount(0);
        p.setSoldCount(0);

        // Chưa có bảng biến thể (variant) -> để danh sách rỗng; giá hiển thị = giá gốc trong DB
        p.setVariants(new ArrayList<>());
        p.setPrice(price);
        p.setMaxPrice(price);
        p.setOriginalPrice(price);
        p.setMaxOriginalPrice(price);
        p.setDiscountPercent(0);

        p.setShopName("E Store Chính Hãng");
        p.setShopAvatar("img/logo.png");
        p.setShopReviews(0);
        p.setShopResponseRate("--");
        p.setShopJoinDate("--");
        p.setShopProductCount(0);
        p.setShopFollowers(0);

        return p;
    }
}
