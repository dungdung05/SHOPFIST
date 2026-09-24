package com.estore.servlet;

import com.estore.dao.ReviewDAO;
import com.estore.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Gửi đánh giá/bình luận cho 1 sản phẩm.
 *   POST /review  (productId, rating, comment)
 * Điều kiện bắt buộc: đã đăng nhập, đã từng MUA sản phẩm này, và CHƯA đánh giá sản phẩm này trước đó.
 */
@WebServlet("/review")
public class ReviewServlet extends HttpServlet {

    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int productId = parseInt(req.getParameter("productId"), -1);
        String backTo = req.getContextPath() + "/product-detail?id=" + productId + "#reviews";

        if (productId <= 0) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect="
                    + java.net.URLEncoder.encode(backTo, "UTF-8"));
            return;
        }

        if (!reviewDAO.hasPurchased(user.getId(), productId)) {
            session.setAttribute("flashError", "Bạn cần mua sản phẩm này trước khi đánh giá.");
            resp.sendRedirect(backTo);
            return;
        }

        if (reviewDAO.hasReviewed(user.getId(), productId)) {
            session.setAttribute("flashError", "Bạn đã đánh giá sản phẩm này rồi.");
            resp.sendRedirect(backTo);
            return;
        }

        int rating = parseInt(req.getParameter("rating"), 0);
        String comment = trim(req.getParameter("comment"));

        if (rating < 1 || rating > 5 || comment.isEmpty()) {
            session.setAttribute("flashError", "Vui lòng chọn số sao (1-5) và nhập nội dung đánh giá.");
            resp.sendRedirect(backTo);
            return;
        }

        reviewDAO.addReview(user.getId(), productId, rating, comment);
        resp.sendRedirect(backTo);
    }

    private String trim(String value) {
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
