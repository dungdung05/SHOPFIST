package com.estore.servlet;

import com.estore.dao.UserDAO;
import com.estore.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Trang quản trị người dùng, chỉ dành cho tài khoản admin
 * (được kiểm tra bởi AdminAuthFilter ở /admin/*).
 *
 *   GET /admin/users                    -> danh sách người dùng
 *   GET /admin/users?action=view&id=X    -> xem thông tin chi tiết 1 khách hàng
 *   GET /admin/users?action=lock&id=X     -> khoá tài khoản (CHỈ áp dụng cho User, không áp dụng cho Admin)
 *   GET /admin/users?action=unlock&id=X   -> mở khoá tài khoản
 *
 * Không có chức năng cấp/thu hồi quyền admin hay xoá tài khoản qua trang này.
 * Tài khoản admin không thể bị khoá qua trang này.
 */
@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = trimToEmpty(req.getParameter("action"));
        int id = parseInt(req.getParameter("id"), -1);

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        switch (action) {
            case "view":
                handleView(req, resp, id);
                return;

            case "lock":
                handleLock(req, currentUser, id, true);
                listAndForward(req, resp);
                return;

            case "unlock":
                handleLock(req, currentUser, id, false);
                listAndForward(req, resp);
                return;

            default:
                listAndForward(req, resp);
        }
    }

    /** Xem thông tin chi tiết 1 khách hàng. */
    private void handleView(HttpServletRequest req, HttpServletResponse resp, int id)
            throws ServletException, IOException {
        User target = (id > 0) ? userDAO.findById(id) : null;
        if (target == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }
        req.setAttribute("viewUser", target);
        req.getRequestDispatcher("/admin/user-detail.jsp").forward(req, resp);
    }

    /**
     * Khoá/mở khoá tài khoản. Chặn: tự khoá chính mình, và khoá 1 tài khoản ĐANG LÀ ADMIN.
     */
    private void handleLock(HttpServletRequest req, User currentUser, int id, boolean lock) {
        if (id <= 0) {
            return;
        }
        if (currentUser != null && currentUser.getId() == id) {
            req.setAttribute("error", "Bạn không thể tự khoá tài khoản đang đăng nhập.");
            return;
        }
        User target = userDAO.findById(id);
        if (lock && target != null && target.isAdmin()) {
            req.setAttribute("error", "Không thể khoá tài khoản admin.");
            return;
        }
        userDAO.setLocked(id, lock);
    }

    private void listAndForward(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<User> users = userDAO.getAll();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/admin/users.jsp").forward(req, resp);
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
