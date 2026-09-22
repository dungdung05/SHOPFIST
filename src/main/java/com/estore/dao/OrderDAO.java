package com.estore.dao;

import com.estore.model.CartItem;
import com.estore.model.Order;
import com.estore.model.OrderItem;
import com.estore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Đọc/ghi đơn hàng (bảng orders + order_items).
 * Một đơn hàng được tạo ra từ giỏ hàng hiện tại của người dùng (xem CheckoutServlet).
 */
public class OrderDAO {

    /** Giá trị trả về của createOrder() khi 1 sản phẩm trong giỏ không còn đủ hàng vào đúng lúc chốt đơn. */
    public static final int STOCK_INSUFFICIENT = -2;

    /**
     * Tạo 1 đơn hàng mới từ danh sách sản phẩm trong giỏ, kèm thông tin người nhận.
     * Chạy trong 1 transaction: hoặc lưu được cả đơn + toàn bộ dòng sản phẩm, hoặc không lưu gì cả.
     *
     * @return id của đơn hàng vừa tạo, hoặc -1 nếu thất bại.
     */
    public int createOrder(int userId, String receiverName, String receiverPhone,
                            String shippingAddress, String note, List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return -1;
        }

        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }

        String insertOrderSql = "INSERT INTO orders "
                + "(user_id, receiver_name, receiver_phone, shipping_address, note, total_amount, status) "
                + "VALUES (?,?,?,?,?,?,'Chờ xác nhận')";
        String insertItemSql = "INSERT INTO order_items "
                + "(order_id, product_id, product_name, product_image, unit_price, quantity) "
                + "VALUES (?,?,?,?,?,?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int orderId;
            try (PreparedStatement ps = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setString(2, receiverName);
                ps.setString(3, receiverPhone);
                ps.setString(4, shippingAddress);
                ps.setString(5, note);
                ps.setDouble(6, total);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conn.rollback();
                        return -1;
                    }
                    orderId = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(insertItemSql)) {
                for (CartItem item : cartItems) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, item.getProductId());
                    ps.setString(3, item.getName());
                    ps.setString(4, item.getImage());
                    ps.setDouble(5, item.getUnitPrice());
                    ps.setInt(6, item.getQuantity());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // Trừ kho ngay trong transaction này. Điều kiện "quantity >= ?" đảm bảo không bao giờ
            // trừ xuống âm — nếu giữa lúc thêm vào giỏ và lúc bấm đặt hàng có người khác mua mất
            // hàng, dòng UPDATE sẽ không sửa được gì (rowcount = 0) và cả đơn hàng sẽ bị huỷ bỏ.
            String decrementStockSql = "UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
            try (PreparedStatement ps = conn.prepareStatement(decrementStockSql)) {
                for (CartItem item : cartItems) {
                    ps.setInt(1, item.getQuantity());
                    ps.setInt(2, item.getProductId());
                    ps.setInt(3, item.getQuantity());
                    ps.addBatch();
                }
                int[] results = ps.executeBatch();
                for (int updatedRows : results) {
                    if (updatedRows == 0) {
                        conn.rollback();
                        return STOCK_INSUFFICIENT;
                    }
                }
            }

            conn.commit();
            return orderId;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /** Danh sách đơn hàng của 1 người dùng, mới nhất trước, KHÔNG kèm chi tiết sản phẩm (dùng cho trang danh sách). */
    public List<Order> getOrdersByUser(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orders;
    }

    /**
     * Lấy chi tiết 1 đơn hàng (kèm danh sách sản phẩm), CHỈ trả về nếu đơn đó thuộc đúng userId
     * truyền vào — tránh 1 user xem được đơn hàng của người khác chỉ bằng cách đổi số id trên URL.
     */
    public Order getOrderDetail(int orderId, int userId) {
        String sql = "SELECT * FROM orders WHERE id = ? AND user_id = ?";
        Order order = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = mapOrder(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (order == null) {
            return null;
        }

        String itemSql = "SELECT * FROM order_items WHERE order_id = ? ORDER BY id ASC";
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(itemSql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem oi = new OrderItem();
                    oi.setId(rs.getInt("id"));
                    oi.setProductId(rs.getInt("product_id"));
                    oi.setProductName(rs.getString("product_name"));
                    oi.setProductImage(rs.getString("product_image"));
                    oi.setUnitPrice(rs.getDouble("unit_price"));
                    oi.setQuantity(rs.getInt("quantity"));
                    items.add(oi);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        order.setItems(items);
        return order;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setUserId(rs.getInt("user_id"));
        o.setReceiverName(rs.getString("receiver_name"));
        o.setReceiverPhone(rs.getString("receiver_phone"));
        o.setShippingAddress(rs.getString("shipping_address"));
        o.setNote(rs.getString("note"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setStatus(rs.getString("status"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        return o;
    }

    private List<OrderItem> loadItems(Connection conn, int orderId) throws SQLException {
        String itemSql = "SELECT * FROM order_items WHERE order_id = ? ORDER BY id ASC";
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem oi = new OrderItem();
                    oi.setId(rs.getInt("id"));
                    oi.setProductId(rs.getInt("product_id"));
                    oi.setProductName(rs.getString("product_name"));
                    oi.setProductImage(rs.getString("product_image"));
                    oi.setUnitPrice(rs.getDouble("unit_price"));
                    oi.setQuantity(rs.getInt("quantity"));
                    items.add(oi);
                }
            }
        }
        return items;
    }

    // ================= Dành cho trang quản trị (Admin) =================

    /**
     * Toàn bộ đơn hàng của TẤT CẢ khách hàng (dùng cho /admin/orders), mới nhất trước.
     * Ghép thêm username/họ tên người mua qua JOIN sang bảng users.
     * @param statusFilter truyền null hoặc "" để lấy tất cả trạng thái.
     */
    public List<Order> getAllOrders(String statusFilter) {
        List<Order> orders = new ArrayList<>();
        boolean hasFilter = statusFilter != null && !statusFilter.trim().isEmpty();
        String sql = "SELECT o.*, u.username AS buyer_username, u.full_name AS buyer_full_name "
                + "FROM orders o JOIN users u ON u.id = o.user_id "
                + (hasFilter ? "WHERE o.status = ? " : "")
                + "ORDER BY o.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (hasFilter) {
                ps.setString(1, statusFilter);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = mapOrder(rs);
                    o.setBuyerUsername(rs.getString("buyer_username"));
                    o.setBuyerFullName(rs.getString("buyer_full_name"));
                    orders.add(o);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orders;
    }

    /** Chi tiết 1 đơn hàng BẤT KỲ (dành cho admin xem, không giới hạn theo user_id như bản của khách). */
    public Order getOrderDetailForAdmin(int orderId) {
        String sql = "SELECT o.*, u.username AS buyer_username, u.full_name AS buyer_full_name "
                + "FROM orders o JOIN users u ON u.id = o.user_id WHERE o.id = ?";
        Order order = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = mapOrder(rs);
                    order.setBuyerUsername(rs.getString("buyer_username"));
                    order.setBuyerFullName(rs.getString("buyer_full_name"));
                }
            }
            if (order != null) {
                order.setItems(loadItems(conn, orderId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return order;
    }

    /**
     * Đổi trạng thái đơn hàng. Nếu chuyển SANG "Đã huỷ" từ 1 trạng thái khác, tự động
     * hoàn lại số lượng đã trừ kho lúc đặt hàng — chạy trong 1 transaction cho an toàn.
     */
    public boolean updateStatus(int orderId, String newStatus) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String currentStatus = null;
            try (PreparedStatement ps = conn.prepareStatement("SELECT status FROM orders WHERE id = ?")) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    currentStatus = rs.getString("status");
                }
            }

            boolean isCancellingNow = "Đã huỷ".equals(newStatus) && !"Đã huỷ".equals(currentStatus);
            if (isCancellingNow) {
                List<OrderItem> items = loadItems(conn, orderId);
                String restoreSql = "UPDATE products SET quantity = quantity + ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(restoreSql)) {
                    for (OrderItem item : items) {
                        ps.setInt(1, item.getQuantity());
                        ps.setInt(2, item.getProductId());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE orders SET status = ? WHERE id = ?")) {
                ps.setString(1, newStatus);
                ps.setInt(2, orderId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }
}
