package com.estore.servlet;

import com.estore.dao.ProductDAO;
import com.estore.model.Product;

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

        req.setAttribute("product", product);
        req.setAttribute("relatedProducts", related);
        req.getRequestDispatcher("product-detail.jsp").forward(req, resp);
    }
}
