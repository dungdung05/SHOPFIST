package com.estore.servlet;

import com.estore.dao.UserDAO;
import com.estore.model.User;
import com.estore.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Quên mật khẩu — không cần cấu hình gửi email (project chưa có SMTP), xác minh danh tính
 * bằng cách khớp "Tên đăng nhập" + "Email" đã đăng ký, khớp đúng thì cho đặt mật khẩu mới.
 *
 *   GET  /forgot-password                 -> bước 1: form nhập username + email
 *   POST /forgot-password (action=verify)  -> kiểm tra khớp, nếu đúng chuyển sang bước 2
 *   POST /forgot-password (action=reset)   -> bước 2: đặt mật khẩu mới
 */
@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("step", "verify");
        req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("reset".equals(action)) {
            handleReset(req, resp);
        } else {
            handleVerify(req, resp);
        }
    }

    /** Bước 1: kiểm tra username + email có khớp 1 tài khoản trong DB không. */
    private void handleVerify(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = trim(req.getParameter("username"));
        String email = trim(req.getParameter("email"));

        User user = userDAO.findByUsername(username);

        if (user == null || user.getEmail() == null || !user.getEmail().equalsIgnoreCase(email)) {
            req.setAttribute("error", "Tên đăng nhập và Email không khớp với tài khoản nào. Vui lòng kiểm tra lại.");
            req.setAttribute("step", "verify");
            req.setAttribute("username", username);
            req.setAttribute("email", email);
            req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
            return;
        }

        // Khớp -> lưu tạm id tài khoản trong session để bước 2 biết đang đặt lại mật khẩu cho ai.
        HttpSession session = req.getSession();
        session.setAttribute("resetUserId", user.getId());

        req.setAttribute("step", "reset");
        req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
    }

    /** Bước 2: đặt mật khẩu mới cho tài khoản đã xác minh ở bước 1. */
    private void handleReset(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Object resetUserIdObj = session.getAttribute("resetUserId");

        if (resetUserIdObj == null) {
            // Chưa xác minh (vào thẳng bước 2 bằng cách gõ URL, hoặc session hết hạn) -> quay lại bước 1.
            req.setAttribute("step", "verify");
            req.setAttribute("error", "Phiên xác minh đã hết hạn, vui lòng thử lại.");
            req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
            return;
        }

        int userId = (int) resetUserIdObj;
        String newPassword = req.getParameter("newPassword");
        String confirmNewPassword = req.getParameter("confirmNewPassword");

        if (newPassword == null || newPassword.length() < 6) {
            req.setAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            req.setAttribute("step", "reset");
            req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            req.setAttribute("error", "Xác nhận mật khẩu không khớp.");
            req.setAttribute("step", "reset");
            req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
            return;
        }

        userDAO.updatePassword(userId, PasswordUtil.hash(newPassword));
        session.removeAttribute("resetUserId");

        resp.sendRedirect(req.getContextPath() + "/login?passwordChanged=1");
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
