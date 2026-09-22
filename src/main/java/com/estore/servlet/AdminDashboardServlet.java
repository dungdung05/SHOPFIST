package com.estore.servlet;

import com.estore.dao.OrderDAO;
import com.estore.dao.ProductDAO;
import com.estore.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Trang tổng quan (dashboard) của khu quản trị: /admin
 * Hiển thị vài số liệu nhanh: tổng số sản phẩm, tổng số người dùng,
 * số quản trị viên, danh sách sản phẩm sắp hết hàng.
 */
@WebServlet("/admin")
public class AdminDashboardServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final UserDAO userDAO = new UserDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("productCount", productDAO.countAll());
        req.setAttribute("userCount", userDAO.countAll());
        req.setAttribute("adminCount", userDAO.countAdmins());
        req.setAttribute("lowStockProducts", productDAO.getLowStock(10));
        req.setAttribute("pendingOrderCount", orderDAO.getAllOrders("Chờ xác nhận").size());

        req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
    }
}
