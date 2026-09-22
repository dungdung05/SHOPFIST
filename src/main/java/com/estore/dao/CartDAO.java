package com.estore.dao;

import com.estore.model.CartItem;
import com.estore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Đọc/ghi giỏ hàng trong bảng `cart_items`.
 *
 * cartKey xác định "chủ" giỏ hàng:
 *  - "u" + user.getId()   -> giỏ hàng của người dùng đã đăng nhập
 *  - "g" + session.getId() -> giỏ hàng tạm của khách (chưa đăng nhập)
 *
 * Giá & tên/ảnh sản phẩm luôn được lấy MỚI NHẤT từ bảng `products` mỗi lần đọc,
 * để tránh hiển thị giá cũ nếu admin đã sửa giá sau khi khách thêm vào giỏ.
 */
public class CartDAO {

    private static final String SELECT_ITEMS =
            "SELECT ci.product_id, ci.variant_name, ci.quantity, "
          + "       p.name, p.image, p.price "
          + "FROM cart_items ci "
          + "JOIN products p ON p.id = ci.product_id "
          + "WHERE ci.cart_key = ? "
          + "ORDER BY ci.id ASC";

    /** Lấy toàn bộ giỏ hàng của 1 cartKey, đã join sang thông tin sản phẩm mới nhất. */
    public List<CartItem> getItems(String cartKey) {
        List<CartItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ITEMS)) {
            ps.setString(1, cartKey);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    String variantName = rs.getString("variant_name");
                    int quantity = rs.getInt("quantity");
                    String name = rs.getString("name");
                    String image = rs.getString("image");
                    double price = rs.getDouble("price");

                    String imagePath = (image == null || image.trim().isEmpty())
                            ? "img/logo.png" : "img/" + image;

                    items.add(new CartItem(productId, name, imagePath, variantName, price, quantity));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return items;
    }

    /** Đếm tổng số lượng sản phẩm trong giỏ (dùng cho badge trên header). */
    public int countItems(String cartKey) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE cart_key = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cartKey);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Thêm sản phẩm vào giỏ; nếu đã có (cùng cartKey + productId + variant) thì cộng dồn số lượng. */
    public void addItem(String cartKey, int productId, String variantName, int quantity) {
        String sql = "INSERT INTO cart_items (cart_key, product_id, variant_name, quantity) "
                + "VALUES (?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cartKey);
            ps.setInt(2, productId);
            ps.setString(3, variantName == null ? "" : variantName);
            ps.setInt(4, quantity);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Cập nhật số lượng của 1 dòng trong giỏ; nếu số lượng &lt;= 0 thì xoá luôn dòng đó. */
    public void updateQuantity(String cartKey, int productId, String variantName, int quantity) {
        if (quantity <= 0) {
            removeItem(cartKey, productId, variantName);
            return;
        }
        String sql = "UPDATE cart_items SET quantity = ? "
                + "WHERE cart_key = ? AND product_id = ? AND variant_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setString(2, cartKey);
            ps.setInt(3, productId);
            ps.setString(4, variantName == null ? "" : variantName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Xoá 1 dòng khỏi giỏ hàng. */
    public void removeItem(String cartKey, int productId, String variantName) {
        String sql = "DELETE FROM cart_items WHERE cart_key = ? AND product_id = ? AND variant_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cartKey);
            ps.setInt(2, productId);
            ps.setString(3, variantName == null ? "" : variantName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Xoá sạch giỏ hàng của 1 cartKey. */
    public void clearCart(String cartKey) {
        String sql = "DELETE FROM cart_items WHERE cart_key = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cartKey);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gộp giỏ hàng khách (guestKey) vào giỏ hàng của user vừa đăng nhập (userKey),
     * cộng dồn số lượng nếu trùng sản phẩm/phân loại, rồi xoá giỏ khách.
     * Gọi ngay sau khi đăng nhập thành công.
     */
    public void mergeCart(String guestKey, String userKey) {
        if (guestKey == null || guestKey.equals(userKey)) {
            return;
        }
        List<CartItem> guestItems = getItems(guestKey);
        if (guestItems.isEmpty()) {
            return;
        }
        for (CartItem item : guestItems) {
            addItem(userKey, item.getProductId(), item.getVariantName(), item.getQuantity());
        }
        clearCart(guestKey);
    }
}
