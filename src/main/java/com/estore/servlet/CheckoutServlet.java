package com.estore.servlet;

import com.estore.dao.CartDAO;
import com.estore.dao.OrderDAO;
import com.estore.model.CartItem;
import com.estore.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Trang đặt hàng: CHỈ đặt những sản phẩm người dùng đã tích chọn ở trang giỏ hàng
 * (xem cart.jsp), không phải toàn bộ giỏ hàng. Sản phẩm không tích vẫn nằm nguyên trong giỏ.
 * Yêu cầu đăng nhập (được chặn sẵn bởi AuthFilter ở /checkout).
 *
 *   GET  /checkout?itemKeys=...&itemKeys=...  -> hiện form nhập thông tin nhận hàng,
 *                                                  chỉ với các sản phẩm có itemKey nằm trong danh sách
 *   POST /checkout                             -> tạo đơn hàng từ đúng các sản phẩm đã chọn ở bước GET
 */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private final CartDAO cartDAO = new CartDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        String cartKey = "u" + user.getId();
        List<CartItem> fullCart = cartDAO.getItems(cartKey);

        String[] itemKeysParam = req.getParameterValues("itemKeys");
        if (itemKeysParam == null || itemKeysParam.length == 0) {
            req.getSession().setAttribute("flashError", "Vui lòng chọn ít nhất 1 sản phẩm để thanh toán.");
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        Set<String> selectedKeys = new HashSet<>(Arrays.asList(itemKeysParam));
        List<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : fullCart) {
            if (selectedKeys.contains(item.getItemKey())) {
                selectedItems.add(item);
            }
        }

        if (selectedItems.isEmpty()) {
            // Giỏ hàng đã thay đổi (sản phẩm bị xoá ở tab khác...) từ lúc tích chọn tới giờ.
            req.getSession().setAttribute("flashError", "Sản phẩm bạn chọn không còn trong giỏ hàng, vui lòng chọn lại.");
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        // Ghi nhớ đúng các sản phẩm đã chọn để bước POST (submit form địa chỉ) dùng lại,
        // vì form địa chỉ không gửi lại danh sách checkbox.
        req.getSession().setAttribute("checkoutItemKeys", new ArrayList<>(selectedKeys));

        req.setAttribute("cart", selectedItems);
        req.setAttribute("cartTotalFormatted", formatCurrency(sumTotal(selectedItems)));
        req.getRequestDispatcher("checkout.jsp").forward(req, resp);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        String cartKey = "u" + user.getId();

        List<String> checkoutItemKeys = (List<String>) session.getAttribute("checkoutItemKeys");
        if (checkoutItemKeys == null || checkoutItemKeys.isEmpty()) {
            // Vào thẳng POST /checkout mà chưa qua bước chọn sản phẩm (vd: bấm Back rồi F5)
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        Set<String> selectedKeys = new HashSet<>(checkoutItemKeys);
        List<CartItem> fullCart = cartDAO.getItems(cartKey);
        List<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : fullCart) {
            if (selectedKeys.contains(item.getItemKey())) {
                selectedItems.add(item);
            }
        }

        if (selectedItems.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        String receiverName = trim(req.getParameter("receiverName"));
        String receiverPhone = trim(req.getParameter("receiverPhone"));
        String shippingAddress = trim(req.getParameter("shippingAddress"));
        String note = trim(req.getParameter("note"));

        if (receiverName.isEmpty() || receiverPhone.isEmpty() || shippingAddress.isEmpty()) {
            showCheckoutError(req, resp, selectedItems,
                    "Vui lòng điền đầy đủ Họ tên, Số điện thoại và Địa chỉ nhận hàng.",
                    receiverName, receiverPhone, shippingAddress, note);
            return;
        }

        int orderId = orderDAO.createOrder(user.getId(), receiverName, receiverPhone, shippingAddress, note, selectedItems);

        if (orderId == OrderDAO.STOCK_INSUFFICIENT) {
            showCheckoutError(req, resp, selectedItems,
                    "Rất tiếc, có sản phẩm trong danh sách vừa hết hàng hoặc không còn đủ số lượng. Vui lòng quay lại giỏ hàng và cập nhật số lượng.",
                    receiverName, receiverPhone, shippingAddress, note);
            return;
        }

        if (orderId <= 0) {
            showCheckoutError(req, resp, selectedItems, "Đặt hàng thất bại, vui lòng thử lại.",
                    receiverName, receiverPhone, shippingAddress, note);
            return;
        }

        // Đặt hàng xong -> chỉ xoá ĐÚNG những sản phẩm vừa đặt khỏi giỏ, giữ nguyên phần còn lại chưa tích.
        for (CartItem item : selectedItems) {
            cartDAO.removeItem(cartKey, item.getProductId(), item.getVariantName());
        }
        session.removeAttribute("checkoutItemKeys");
        session.setAttribute("cart", cartDAO.getItems(cartKey));

        resp.sendRedirect(req.getContextPath() + "/orders?id=" + orderId + "&placed=1");
    }

    private void showCheckoutError(HttpServletRequest req, HttpServletResponse resp, List<CartItem> items,
                                    String error, String receiverName, String receiverPhone,
                                    String shippingAddress, String note) throws ServletException, IOException {
        req.setAttribute("error", error);
        req.setAttribute("cart", items);
        req.setAttribute("cartTotalFormatted", formatCurrency(sumTotal(items)));
        req.setAttribute("receiverName", receiverName);
        req.setAttribute("receiverPhone", receiverPhone);
        req.setAttribute("shippingAddress", shippingAddress);
        req.setAttribute("note", note);
        req.getRequestDispatcher("checkout.jsp").forward(req, resp);
    }

    private double sumTotal(List<CartItem> items) {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String formatCurrency(double value) {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        return nf.format(value) + "\u20ab";
    }
}
