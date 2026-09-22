package com.estore.servlet;

import com.estore.dao.OrderDAO;
import com.estore.model.Order;
import com.estore.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * "Đơn hàng của tôi" — yêu cầu đăng nhập (được chặn sẵn bởi AuthFilter ở /orders).
 *
 *   GET /orders                     -> danh sách đơn hàng của người đang đăng nhập
 *   GET /orders?id=X                 -> chi tiết 1 đơn hàng (chỉ xem được đơn của chính mình)
 *   GET /orders?action=cancel&id=X    -> huỷ đơn hàng (chỉ khi đơn còn "Chờ xác nhận" và đúng là đơn của mình)
 */
@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    /** Chỉ huỷ được khi đơn còn ở trạng thái này — đã "Đang giao" trở đi thì không tự huỷ được nữa. */
    private static final String CANCELLABLE_STATUS = "Chờ xác nhận";

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");

        if ("cancel".equals(req.getParameter("action"))) {
            handleCancel(req, resp, user);
            return;
        }

        String idParam = req.getParameter("id");
        if (idParam != null) {
            int id = parseInt(idParam, -1);
            Order order = (id > 0) ? orderDAO.getOrderDetail(id, user.getId()) : null;
            if (order == null) {
                resp.sendRedirect(req.getContextPath() + "/orders");
                return;
            }
            req.setAttribute("order", order);
            req.setAttribute("cancellableStatus", CANCELLABLE_STATUS);
            req.getRequestDispatcher("order-detail.jsp").forward(req, resp);
            return;
        }

        List<Order> orders = orderDAO.getOrdersByUser(user.getId());
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("orders.jsp").forward(req, resp);
    }

    /** Huỷ đơn hàng: chỉ cho phép nếu đơn thuộc đúng người đang đăng nhập VÀ còn ở trạng thái "Chờ xác nhận". */
    private void handleCancel(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        int id = parseInt(req.getParameter("id"), -1);

        // getOrderDetail(id, user.getId()) đã tự kiểm tra đơn có đúng thuộc user này không —
        // nếu không phải đơn của mình (hoặc gõ id bừa), trả về null, không cho huỷ.
        Order order = (id > 0) ? orderDAO.getOrderDetail(id, user.getId()) : null;

        if (order != null && CANCELLABLE_STATUS.equals(order.getStatus())) {
            // Dùng lại đúng hàm updateStatus() của OrderDAO -> tự động hoàn lại số lượng vào kho.
            orderDAO.updateStatus(id, "Đã huỷ");
        }

        resp.sendRedirect(req.getContextPath() + "/orders?id=" + id);
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return fallback;
        }
    }
}
