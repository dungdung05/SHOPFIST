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
 * Trang quản trị sản phẩm (mini admin), chỉ dành cho tài khoản có is_admin = 1
 * (được kiểm tra bởi AdminAuthFilter ở /admin/*).
 *
 *   GET  /admin/products                -> danh sách sản phẩm
 *   GET  /admin/products?action=new     -> form thêm sản phẩm mới
 *   GET  /admin/products?action=edit&id=X -> form sửa sản phẩm
 *   GET  /admin/products?action=delete&id=X -> xoá sản phẩm
 *   POST /admin/products (action=save)  -> lưu (thêm mới hoặc cập nhật)
 */
@WebServlet("/admin/products")
public class AdminProductServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = trimToEmpty(req.getParameter("action"));

        switch (action) {
            case "new":
                req.setAttribute("product", new Product());
                req.setAttribute("categories", productDAO.getCategories());
                req.getRequestDispatcher("/admin/product-form.jsp").forward(req, resp);
                return;

            case "edit":
                int editId = parseInt(req.getParameter("id"), -1);
                Product product = productDAO.getById(editId);
                if (product == null) {
                    resp.sendRedirect(req.getContextPath() + "/admin/products");
                    return;
                }
                req.setAttribute("product", product);
                req.setAttribute("categories", productDAO.getCategories());
                req.getRequestDispatcher("/admin/product-form.jsp").forward(req, resp);
                return;

            case "delete":
                int deleteId = parseInt(req.getParameter("id"), -1);
                if (deleteId > 0) {
                    productDAO.delete(deleteId);
                }
                resp.sendRedirect(req.getContextPath() + "/admin/products?deleted=1");
                return;

            default:
                List<Product> products = productDAO.getAll();
                req.setAttribute("products", products);
                req.getRequestDispatcher("/admin/products.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = parseInt(req.getParameter("id"), -1);
        String name = trimToEmpty(req.getParameter("name"));
        double price = parseDouble(req.getParameter("price"), 0);
        String image = trimToEmpty(req.getParameter("image"));
        String category = trimToEmpty(req.getParameter("category"));
        String description = trimToEmpty(req.getParameter("description"));
        int quantity = parseInt(req.getParameter("quantity"), 0);

        if (name.isEmpty() || price < 0) {
            req.setAttribute("error", "Vui lòng nhập đầy đủ Tên sản phẩm và Giá hợp lệ.");
            Product p = new Product();
            p.setId(id);
            p.setName(name);
            p.setPrice(price);
            p.setImageFileName(image);
            p.setCategory(category);
            p.setDescription(description);
            p.setQuantity(quantity);
            req.setAttribute("product", p);
            req.setAttribute("categories", productDAO.getCategories());
            req.getRequestDispatcher("/admin/product-form.jsp").forward(req, resp);
            return;
        }

        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setPrice(price);
        p.setImageFileName(image);
        p.setCategory(category);
        p.setDescription(description);
        p.setQuantity(quantity);

        if (id > 0) {
            productDAO.update(p);
        } else {
            productDAO.insert(p);
        }

        resp.sendRedirect(req.getContextPath() + "/admin/products");
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

    private double parseDouble(String value, double fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
