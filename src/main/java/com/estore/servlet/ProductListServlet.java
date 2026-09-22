package com.estore.servlet;

import com.estore.dao.ProductDAO;
import com.estore.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Trang danh sách sản phẩm: tìm kiếm theo từ khoá, lọc theo danh mục / khoảng giá,
 * và sắp xếp (giá, đánh giá, đã bán, tên).
 * URL ví dụ: /products?q=ao&category=Men+%26+Women+Clothes&minPrice=50000&maxPrice=300000&sort=price_asc
 */
@WebServlet("/products")
public class ProductListServlet extends HttpServlet {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = trimToEmpty(req.getParameter("q"));
        String category = trimToEmpty(req.getParameter("category"));
        String sort = trimToEmpty(req.getParameter("sort"));
        Double minPrice = parseDouble(req.getParameter("minPrice"));
        Double maxPrice = parseDouble(req.getParameter("maxPrice"));

        List<Product> products = productDAO.getAll();
        List<Product> result = new ArrayList<>();

        String normalizedKeyword = normalize(keyword);

        for (Product p : products) {
            if (!keyword.isEmpty()
                    && !normalize(p.getName()).contains(normalizedKeyword)
                    && !normalize(p.getDescription()).contains(normalizedKeyword)) {
                continue;
            }
            if (!category.isEmpty() && !category.equals(p.getCategory())) {
                continue;
            }
            if (minPrice != null && p.getMaxPrice() < minPrice) {
                continue;
            }
            if (maxPrice != null && p.getPrice() > maxPrice) {
                continue;
            }
            result.add(p);
        }

        applySort(result, sort);

        req.setAttribute("products", result);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedCategory", category);
        req.setAttribute("selectedSort", sort.isEmpty() ? "default" : sort);
        req.setAttribute("minPrice", req.getParameter("minPrice"));
        req.setAttribute("maxPrice", req.getParameter("maxPrice"));
        req.setAttribute("categories", productDAO.getCategories());
        req.setAttribute("resultCount", result.size());

        req.getRequestDispatcher("product-list.jsp").forward(req, resp);
    }

    private void applySort(List<Product> products, String sort) {
        if (sort == null) {
            return;
        }
        switch (sort) {
            case "price_asc":
                products.sort(Comparator.comparingDouble(Product::getPrice));
                break;
            case "price_desc":
                products.sort(Comparator.comparingDouble(Product::getPrice).reversed());
                break;
            case "rating_desc":
                products.sort(Comparator.comparingDouble(Product::getRating).reversed());
                break;
            case "sold_desc":
                products.sort(Comparator.comparingInt(Product::getSoldCount).reversed());
                break;
            case "name_asc":
                products.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
                break;
            default:
                // giữ nguyên thứ tự mặc định (theo id)
                break;
        }
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }
        String noAccent = Normalizer.normalize(text, Normalizer.Form.NFD);
        noAccent = DIACRITICS.matcher(noAccent).replaceAll("");
        return noAccent.toLowerCase().replace('đ', 'd').replace('Đ', 'd');
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
















        
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
