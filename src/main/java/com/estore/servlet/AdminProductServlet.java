package com.estore.servlet;

import com.estore.dao.ProductDAO;
import com.estore.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

/**
 * Trang quản trị sản phẩm (mini admin), chỉ dành cho tài khoản có is_admin = 1
 * (được kiểm tra bởi AdminAuthFilter ở /admin/*).
 *
 *   GET  /admin/products                -> danh sách sản phẩm
 *   GET  /admin/products?action=new     -> form thêm sản phẩm mới
 *   GET  /admin/products?action=edit&id=X -> form sửa sản phẩm
 *   GET  /admin/products?action=delete&id=X -> xoá sản phẩm
 *   POST /admin/products (action=save)  -> lưu (thêm mới hoặc cập nhật), CÓ THỂ kèm file ảnh tải lên
 *
 * LƯU Ý: ảnh tải lên được lưu thẳng vào thư mục webapp/img/ ĐANG CHẠY trên Tomcat
 * (getRealPath) — tức là thư mục đã giải nén từ file .war. Sau khi tải ảnh lên,
 * nếu build lại (mvn clean package) và deploy đè WAR mới, thư mục đó bị thay mới hoàn
 * toàn -> ẢNH VỪA TẢI SẼ MẤT. Muốn giữ ảnh vĩnh viễn, sau khi tải lên cần copy tay
 * ảnh đó từ thư mục Tomcat (webapps/estore-java/img/) về lại src/main/webapp/img/
 * trong project trước khi build lại lần sau.
 */
@WebServlet("/admin/products")
@MultipartConfig(
        maxFileSize = 1024 * 1024 * 5,      // 5MB / 1 ảnh
        maxRequestSize = 1024 * 1024 * 10    // 10MB / cả request
)
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
        String category = trimToEmpty(req.getParameter("category"));
        String description = trimToEmpty(req.getParameter("description"));
        int quantity = parseInt(req.getParameter("quantity"), 0);

        // Ảnh cũ đang có (ẩn trong form) — dùng làm mặc định nếu admin không chọn ảnh mới.
        String currentImage = trimToEmpty(req.getParameter("currentImage"));
        String image = currentImage;

        String uploadError = null;
        try {
            String uploadedFileName = handleImageUpload(req);
            if (uploadedFileName != null) {
                image = uploadedFileName;
            }
        } catch (IllegalArgumentException e) {
            uploadError = e.getMessage();
        }

        if (name.isEmpty() || price < 0 || uploadError != null) {
            String error = uploadError != null ? uploadError : "Vui lòng nhập đầy đủ Tên sản phẩm và Giá hợp lệ.";
            req.setAttribute("error", error);
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

    /**
     * Nếu admin có chọn file ảnh mới (input type="file" name="imageFile"), lưu file đó
     * vào thư mục webapp/img/ với tên mới (tránh trùng tên), trả về tên file để lưu vào DB.
     * Trả về null nếu admin không chọn file nào (giữ nguyên ảnh cũ).
     */
    private String handleImageUpload(HttpServletRequest req) throws IOException, ServletException {
        Part filePart = req.getPart("imageFile");
        if (filePart == null || filePart.getSize() == 0) {
            return null; // không chọn ảnh mới
        }

        String contentType = filePart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File tải lên phải là hình ảnh (jpg, png, webp...).");
        }

        String originalName = extractFileName(filePart);
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalName.substring(dotIndex); // gồm cả dấu chấm, vd ".jpg"
        }

        // Đặt tên file mới ngẫu nhiên để không bao giờ bị trùng/đè lên ảnh có sẵn.
        String newFileName = "upload-" + UUID.randomUUID() + extension;

        String imgDirPath = getServletContext().getRealPath("/img/");
        File imgDir = new File(imgDirPath);
        if (!imgDir.exists()) {
            imgDir.mkdirs();
        }

        File destFile = new File(imgDir, newFileName);
        try (InputStream in = filePart.getInputStream();
             OutputStream out = Files.newOutputStream(destFile.toPath())) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return newFileName;
    }

    private String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String token : contentDisposition.split(";")) {
            token = token.trim();
            if (token.startsWith("filename")) {
                String fileName = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                int slash = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
                return (slash >= 0) ? fileName.substring(slash + 1) : fileName;
            }
        }
        return "unnamed";
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
