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
 * Trang hồ sơ cá nhân: xem thông tin, cập nhật thông tin, đổi mật khẩu.
 * Được bảo vệ bởi AuthFilter (chỉ vào được khi đã đăng nhập).
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");

        String action = req.getParameter("action");

        if ("updateInfo".equals(action)) {
            req.setAttribute("activeTab", "info");
            String fullName = req.getParameter("fullName");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String address = req.getParameter("address");

            if (fullName == null || fullName.trim().isEmpty() || email == null || email.trim().isEmpty()) {
                req.setAttribute("error", "Họ tên và email không được để trống.");
            } else {
                currentUser.setFullName(fullName.trim());
                currentUser.setEmail(email.trim());
                currentUser.setPhone(phone == null ? "" : phone.trim());
                currentUser.setAddress(address == null ? "" : address.trim());

                boolean ok = userDAO.updateProfile(currentUser);
                if (ok) {
                    session.setAttribute("user", currentUser); // cập nhật lại session
                    req.setAttribute("success", "Cập nhật thông tin hồ sơ thành công.");
                } else {
                    req.setAttribute("error", "Cập nhật thất bại, vui lòng thử lại.");
                }
            }
        } else if ("changePassword".equals(action)) {
            req.setAttribute("activeTab", "password"); // luôn ở lại tab "Đổi mật khẩu" dù lỗi hay thành công

            String currentPassword = req.getParameter("currentPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmNewPassword = req.getParameter("confirmNewPassword");

            if (!PasswordUtil.matches(currentPassword, currentUser.getPassword())) {
                req.setAttribute("error", "Mật khẩu hiện tại không đúng.");
            } else if (newPassword == null || newPassword.length() < 6) {
                req.setAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            } else if (!newPassword.equals(confirmNewPassword)) {
                req.setAttribute("error", "Xác nhận mật khẩu mới không khớp.");
            } else {
                boolean ok = userDAO.updatePassword(currentUser.getId(), PasswordUtil.hash(newPassword));
                if (ok) {
                    // Đổi mật khẩu xong -> huỷ session hiện tại, bắt đăng nhập lại bằng mật khẩu mới.
                    session.invalidate();
                    resp.sendRedirect(req.getContextPath() + "/login?passwordChanged=1");
                    return;
                } else {
                    req.setAttribute("error", "Đổi mật khẩu thất bại, vui lòng thử lại.");
                }
            }
        }

        req.getRequestDispatcher("profile.jsp").forward(req, resp);
    }
}
