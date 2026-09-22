package com.estore.servlet;

import com.estore.dao.UserDAO;
import com.estore.model.User;
import com.estore.util.PasswordUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = trim(req.getParameter("username"));
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String fullName = trim(req.getParameter("fullName"));
        String email = trim(req.getParameter("email"));
        String phone = trim(req.getParameter("phone"));
        String address = trim(req.getParameter("address"));

        String error = validate(username, password, confirmPassword, fullName, email);

        if (error == null) {
            if (userDAO.isUsernameTaken(username)) {
                error = "Tên đăng nhập đã tồn tại, vui lòng chọn tên khác.";
            } else if (userDAO.isEmailTaken(email)) {
                error = "Email này đã được sử dụng để đăng ký tài khoản khác.";
            }
        }

        if (error != null) {
            req.setAttribute("error", error);
            // giữ lại dữ liệu người dùng đã nhập để không phải gõ lại
            req.setAttribute("username", username);
            req.setAttribute("fullName", fullName);
            req.setAttribute("email", email);
            req.setAttribute("phone", phone);
            req.setAttribute("address", address);
            RequestDispatcher rd = req.getRequestDispatcher("register.jsp");
            rd.forward(req, resp);
            return;
        }

        User user = new User(username, PasswordUtil.hash(password), fullName, email, phone, address);
        boolean ok = userDAO.insertUser(user);

        if (ok) {
            req.getSession().setAttribute("registerSuccess", true);
            resp.sendRedirect(req.getContextPath() + "/login");
        } else {
            req.setAttribute("error", "Đăng ký thất bại, vui lòng thử lại.");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
        }
    }

    private String validate(String username, String password, String confirmPassword, String fullName, String email) {
        if (isEmpty(username) || isEmpty(password) || isEmpty(fullName) || isEmpty(email)) {
            return "Vui lòng điền đầy đủ các trường bắt buộc (*).";
        }
        if (username.length() < 4) {
            return "Tên đăng nhập phải có ít nhất 4 ký tự.";
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return "Tên đăng nhập chỉ được chứa chữ, số và dấu gạch dưới.";
        }
        if (password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự.";
        }
        if (!password.equals(confirmPassword)) {
            return "Mật khẩu xác nhận không khớp.";
        }
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            return "Email không đúng định dạng.";
        }
        return null;
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
