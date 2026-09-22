package com.estore.servlet;

import com.estore.dao.CartDAO;
import com.estore.dao.ProductDAO;
import com.estore.model.CartItem;
import com.estore.model.Product;
import com.estore.model.User;
import com.estore.model.Variant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Servlet quản lý giỏ hàng: xem giỏ hàng, thêm sản phẩm, cập nhật số lượng, xoá sản phẩm.
 * Giỏ hàng được lưu trong bảng `cart_items` (CartDAO), không phụ thuộc vào session:
 *  - Đã đăng nhập  -> cartKey = "u" + user.getId()  (giỏ hàng đi theo tài khoản)
 *  - Chưa đăng nhập -> cartKey = "g" + session.getId() (giỏ hàng tạm của khách)
 * Khi khách đăng nhập, giỏ hàng tạm sẽ được gộp vào giỏ của tài khoản (xem LoginServlet).
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("add".equals(action)) {
            if (!isLoggedIn(req)) {
                redirectToLogin(req, resp);
                return;
            }
            handleAdd(req);
            redirectBack(req, resp);
            return;
        }
        if ("remove".equals(action)) {
            handleRemove(req);
            redirectBack(req, resp);
            return;
        }
        if ("update".equals(action)) {
            handleUpdate(req);
            redirectBack(req, resp);
            return;
        }
        if ("clear".equals(action)) {
            cartDAO.clearCart(getCartKey(req));
            redirectBack(req, resp);
            return;
        }

        showCart(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("update".equals(action)) {
            handleUpdate(req);
        } else if ("remove".equals(action)) {
            handleRemove(req);
        } else {
            // Không có action = thêm vào giỏ (form "Thêm Vào Giỏ" / "Mua Ngay" ở trang chi tiết sản phẩm)
            if (!isLoggedIn(req)) {
                redirectToLogin(req, resp);
                return;
            }
            handleAdd(req);
        }

        // "Mua Ngay" đi thẳng tới giỏ hàng (chưa có trang checkout riêng)
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    /** Chưa đăng nhập mà bấm "Thêm vào giỏ" -> đưa sang trang login, đăng nhập xong quay lại đúng trang đang đứng. */
    private void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String referer = req.getHeader("Referer");
        String backTo = (referer != null && !referer.isEmpty()) ? referer : (req.getContextPath() + "/products");
        resp.sendRedirect(req.getContextPath() + "/login?redirect=" + java.net.URLEncoder.encode(backTo, "UTF-8"));
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

    // ---------------------------------------------------------------

    private void showCart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CartItem> cart = cartDAO.getItems(getCartKey(req));

        double total = 0;
        int count = 0;
        for (CartItem item : cart) {
            total += item.getSubtotal();
            count += item.getQuantity();
        }

        // Giữ nguyên "cart" trong session để header.jsp / cart.jsp (đang đọc sessionScope.cart)
        // không cần sửa lại, nhưng dữ liệu gốc luôn nằm ở DB (đọc lại mỗi request).
        req.getSession().setAttribute("cart", cart);
        req.setAttribute("cartTotalFormatted", formatCurrency(total));
        req.setAttribute("cartItemCount", count);

        req.getRequestDispatcher("cart.jsp").forward(req, resp);
    }

    private String formatCurrency(double value) {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        return nf.format(value) + "\u20ab";
    }

    /** cartKey: theo tài khoản nếu đã đăng nhập, theo session nếu là khách. */
    private String getCartKey(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return "u" + user.getId();
        }
        return "g" + session.getId();
    }

    private void handleAdd(HttpServletRequest req) {
        int productId = parseInt(req.getParameter("productId"), -1);
        Product product = productDAO.getById(productId);
        if (product == null) {
            return;
        }

        int quantity = parseInt(req.getParameter("quantity"), 1);
        if (quantity < 1) {
            quantity = 1;
        }

        String variantName = req.getParameter("variantName");
        if (variantName != null && !variantName.trim().isEmpty()) {
            boolean valid = false;
            for (Variant v : product.getVariants()) {
                if (v.getName().equals(variantName)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                variantName = "";
            }
        } else {
            variantName = "";
        }

        String cartKey = getCartKey(req);

        // Số lượng ĐANG có sẵn trong giỏ (nếu sản phẩm/phân loại này đã có trong giỏ từ trước)
        int alreadyInCart = 0;
        for (CartItem item : cartDAO.getItems(cartKey)) {
            if (item.getProductId() == productId && item.getVariantName().equals(variantName)) {
                alreadyInCart = item.getQuantity();
                break;
            }
        }

        int stock = product.getQuantity();
        if (alreadyInCart + quantity > stock) {
            int canAddMore = Math.max(0, stock - alreadyInCart);
            if (canAddMore <= 0) {
                req.getSession().setAttribute("flashError",
                        "\"" + product.getName() + "\" đã có đủ " + stock + " sản phẩm (toàn bộ số lượng còn trong kho) trong giỏ hàng của bạn.");
            } else {
                req.getSession().setAttribute("flashError",
                        "\"" + product.getName() + "\" chỉ còn " + stock + " sản phẩm trong kho. Bạn đã thêm được " + canAddMore + " sản phẩm nữa vào giỏ.");
                cartDAO.addItem(cartKey, productId, variantName, canAddMore);
            }
            req.getSession().setAttribute("cart", cartDAO.getItems(cartKey));
            return;
        }

        cartDAO.addItem(cartKey, productId, variantName, quantity);
        // Đồng bộ lại session ngay để header.jsp hiển thị đúng số lượng ở lần render tiếp theo.
        req.getSession().setAttribute("cart", cartDAO.getItems(cartKey));
    }

    private void handleUpdate(HttpServletRequest req) {
        String itemKey = req.getParameter("itemKey");
        int quantity = parseInt(req.getParameter("quantity"), 1);

        int[] productId = new int[1];
        String[] variantName = new String[1];
        if (!parseItemKey(itemKey, productId, variantName)) {
            return;
        }

        String cartKey = getCartKey(req);

        if (quantity > 0) {
            Product product = productDAO.getById(productId[0]);
            if (product != null && quantity > product.getQuantity()) {
                quantity = product.getQuantity();
                req.getSession().setAttribute("flashError",
                        "\"" + product.getName() + "\" chỉ còn " + product.getQuantity() + " sản phẩm trong kho.");
            }
        }

        cartDAO.updateQuantity(cartKey, productId[0], variantName[0], quantity);
        req.getSession().setAttribute("cart", cartDAO.getItems(cartKey));
    }

    private void handleRemove(HttpServletRequest req) {
        String itemKey = req.getParameter("itemKey");

        int[] productId = new int[1];
        String[] variantName = new String[1];
        if (!parseItemKey(itemKey, productId, variantName)) {
            return;
        }

        cartDAO.removeItem(getCartKey(req), productId[0], variantName[0]);
        req.getSession().setAttribute("cart", cartDAO.getItems(getCartKey(req)));
    }

    /** Tách "productId|variantName" (xem CartItem#getItemKey) thành 2 phần. */
    private boolean parseItemKey(String itemKey, int[] productIdOut, String[] variantNameOut) {
        if (itemKey == null || !itemKey.contains("|")) {
            return false;
        }
        int sep = itemKey.indexOf('|');
        try {
            productIdOut[0] = Integer.parseInt(itemKey.substring(0, sep));
        } catch (NumberFormatException e) {
            return false;
        }
        variantNameOut[0] = itemKey.substring(sep + 1);
        return true;
    }

    private void redirectBack(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String referer = req.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            resp.sendRedirect(referer);
        } else {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
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
