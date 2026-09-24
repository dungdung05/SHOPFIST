package com.estore.dao;

import com.estore.model.Review;
import com.estore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Đọc/ghi đánh giá sản phẩm (bảng reviews). */
public class ReviewDAO {

    /**
     * Kiểm tra 1 user đã từng mua sản phẩm này chưa (dò trong order_items, bỏ qua đơn đã huỷ).
     * Chỉ cho phép đánh giá nếu true — tránh người chưa mua vào đánh giá bừa.
     */
    public boolean hasPurchased(int userId, int productId) {
        String sql = "SELECT COUNT(*) FROM order_items oi "
                + "JOIN orders o ON o.id = oi.order_id "
                + "WHERE o.user_id = ? AND oi.product_id = ? AND o.status <> 'Đã huỷ'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Kiểm tra user này đã đánh giá sản phẩm này chưa (mỗi người chỉ đánh giá 1 lần / sản phẩm). */
    public boolean hasReviewed(int userId, int productId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Thêm 1 đánh giá mới. */
    public boolean addReview(int userId, int productId, int rating, String comment) {
        String sql = "INSERT INTO reviews (product_id, user_id, rating, comment) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, userId);
            ps.setInt(3, rating);
            ps.setString(4, comment);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Toàn bộ đánh giá của 1 sản phẩm, mới nhất trước, kèm tên người đánh giá (JOIN sang users). */
    public List<Review> getReviewsByProduct(int productId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name AS reviewer_name FROM reviews r "
                + "JOIN users u ON u.id = r.user_id "
                + "WHERE r.product_id = ? ORDER BY r.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review();
                    r.setId(rs.getInt("id"));
                    r.setProductId(rs.getInt("product_id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setRating(rs.getInt("rating"));
                    r.setComment(rs.getString("comment"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setReviewerName(rs.getString("reviewer_name"));
                    reviews.add(r);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reviews;
    }

    /** Điểm trung bình (làm tròn 1 chữ số thập phân) của 1 sản phẩm; trả về 0 nếu chưa có đánh giá nào. */
    public double getAverageRating(int productId) {
        String sql = "SELECT AVG(rating) FROM reviews WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble(1);
                    return Math.round(avg * 10) / 10.0;
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Tổng số đánh giá của 1 sản phẩm. */
    public int getReviewCount(int productId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
