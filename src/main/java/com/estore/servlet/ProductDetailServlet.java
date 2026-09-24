package com.estore.servlet;

import com.estore.dao.ProductDAO;
import com.estore.dao.ReviewDAO;
import com.estore.model.Product;
import com.estore.model.Review;
import com.estore.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Hiển thị trang chi tiết sản phẩm khi người dùng bấm vào một sản phẩm
 * ở trang chủ (index.jsp): /product-detail?id=1
 */
@WebServlet("/product-detail")
public class ProductDetailServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = 1;
        String idParam = req.getParameter("id");
        if (idParam != null) {
            try {
                id = Integer.parseInt(idParam.trim());
            } catch (NumberFormatException ignored) {
                // giữ id mặc định = 1 nếu tham số không hợp lệ
            }
        }

        Product product = productDAO.getById(id);
        if (product == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        List<Product> related = productDAO.getRelated(product.getId(), 5);

        // ==== Đánh giá / bình luận ====
        List<Review> reviews = reviewDAO.getReviewsByProduct(id);
        double avgRating = reviewDAO.getAverageRating(id);
        int reviewCount = reviews.size();

        User user = (User) req.getSession().getAttribute("user");
        boolean canReview = false;
        String reviewBlockReason = null;

        if (user == null) {
            reviewBlockReason = "login";
        } else if (!reviewDAO.hasPurchased(user.getId(), id)) {
            reviewBlockReason = "not-purchased";
        } else if (reviewDAO.hasReviewed(user.getId(), id)) {
            reviewBlockReason = "already-reviewed";
        } else {
            canReview = true;
        }

        req.setAttribute("product", product);
        req.setAttribute("relatedProducts", related);
        req.setAttribute("reviews", reviews);
        req.setAttribute("avgRating", avgRating);
        req.setAttribute("reviewCount", reviewCount);
        req.setAttribute("canReview", canReview);
        req.setAttribute("reviewBlockReason", reviewBlockReason);
        req.getRequestDispatcher("product-detail.jsp").forward(req, resp);
    }
}
