package com.estore.servlet;

import com.estore.dao.CartDAO;
import com.estore.dao.UserDAO;
import com.estore.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Nếu đã đăng nhập rồi thì vào thẳng trang hồ sơ
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }
        req.getRequestDispatcher("login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || password == null || username.trim().isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
        }

        User user = userDAO.login(username.trim(), password);

        if (user != null && user.isLocked()) {
            req.setAttribute("error", "Tài khoản của bạn đã bị khoá. Vui lòng liên hệ quản trị viên.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
        }

        if (user != null) {
            HttpSession oldSession = req.getSession(false);
            String guestCartKey = (oldSession != null) ? "g" + oldSession.getId() : null;

            HttpSession session = req.getSession(true); // tạo session mới sau khi đăng nhập thành công
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60); // 30 phút

            // Gộp giỏ hàng tạm (lúc còn là khách) vào giỏ hàng của tài khoản vừa đăng nhập.
            cartDAO.mergeCart(guestCartKey, "u" + user.getId());

            String redirect = req.getParameter("redirect");
            if (redirect != null && !redirect.isEmpty()) {
                resp.sendRedirect(redirect);
            } else {
                resp.sendRedirect(req.getContextPath() + "/profile");
            }
        } else {
            req.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }
}
