package com.estore.servlet;

import com.estore.dao.OrderDAO;
import com.estore.model.Order;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Quản lý đơn hàng phía Admin, chỉ dành cho tài khoản admin (AdminAuthFilter chặn ở /admin/*).
 *
 *   GET /admin/orders                          -> danh sách TẤT CẢ đơn hàng, có thể lọc theo trạng thái (?status=...)
 *   GET /admin/orders?action=view&id=X          -> xem chi tiết 1 đơn
 *   GET /admin/orders?action=updateStatus&id=X&status=... -> đổi trạng thái đơn hàng
 */
@WebServlet("/admin/orders")
public class AdminOrderServlet extends HttpServlet {

    /** Các trạng thái hợp lệ, theo đúng thứ tự vòng đời 1 đơn hàng. */
    public static final String[] STATUSES = {"Chờ xác nhận", "Đang giao", "Hoàn thành", "Đã huỷ"};

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = trimToEmpty(req.getParameter("action"));
        int id = parseInt(req.getParameter("id"), -1);

        switch (action) {
            case "view":
                handleView(req, resp, id);
                return;

            case "updateStatus":
                handleUpdateStatus(req, id);
                resp.sendRedirect(req.getContextPath() + "/admin/orders?id=" + id + "&action=view");
                return;

            default:
                listAndForward(req, resp);
        }
    }

    private void handleView(HttpServletRequest req, HttpServletResponse resp, int id)
            throws ServletException, IOException {
        Order order = (id > 0) ? orderDAO.getOrderDetailForAdmin(id) : null;
        if (order == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
            return;
        }
        req.setAttribute("order", order);
        req.setAttribute("statuses", STATUSES);
        req.getRequestDispatcher("/admin/order-detail.jsp").forward(req, resp);
    }

    private void handleUpdateStatus(HttpServletRequest req, int id) {
        if (id <= 0) {
            return;
        }
        String newStatus = req.getParameter("status");
        if (newStatus == null || !isValidStatus(newStatus)) {
            return;
        }
        orderDAO.updateStatus(id, newStatus);
    }

    private boolean isValidStatus(String status) {
        for (String s : STATUSES) {
            if (s.equals(status)) {
                return true;
            }
        }
        return false;
    }

    private void listAndForward(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String statusFilter = req.getParameter("status");
        List<Order> orders = orderDAO.getAllOrders(statusFilter);
        req.setAttribute("orders", orders);
        req.setAttribute("statuses", STATUSES);
        req.setAttribute("currentStatusFilter", statusFilter);
        req.getRequestDispatcher("/admin/orders.jsp").forward(req, resp);
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private int parseInt(String value, int fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
