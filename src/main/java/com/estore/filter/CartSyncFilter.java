package com.estore.filter;

import com.estore.dao.CartDAO;
import com.estore.dao.UserDAO;
import com.estore.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Đồng bộ giỏ hàng từ DB (bảng cart_items, qua CartDAO) vào session ở MỌI trang,
 * để header.jsp (đọc sessionScope.cart để hiển thị badge số lượng) luôn đúng
 * ngay cả khi người dùng không đứng ở trang /cart.
 * CartServlet vẫn là nơi duy nhất ghi xuống DB; filter này chỉ đọc lại và
 * cập nhật session cho khớp, không tạo dữ liệu mới.
 */
@WebFilter("/*")
public class CartSyncFilter implements Filter {

    private final CartDAO cartDAO = new CartDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        if (isStaticResource(req)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("user");

        // Nếu admin vừa khoá tài khoản này trong lúc họ vẫn đang đăng nhập -> đá ra ngay lập tức.
        if (user != null) {
            User fresh = userDAO.findById(user.getId());
            if (fresh == null || fresh.isLocked()) {
                session.invalidate();
                ((HttpServletResponse) response).sendRedirect(
                        req.getContextPath() + "/login?locked=1");
                return;
            }
        }

        String cartKey = (user != null) ? "u" + user.getId() : "g" + session.getId();

        session.setAttribute("cart", cartDAO.getItems(cartKey));

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    /** Bỏ qua css/js/ảnh/font... để không phải query DB cho từng file tĩnh. */
    private boolean isStaticResource(HttpServletRequest req) {
        String uri = req.getRequestURI();
        return uri.contains("/css/") || uri.contains("/js/") || uri.contains("/img/")
                || uri.contains("/lib/") || uri.endsWith(".css") || uri.endsWith(".js")
                || uri.endsWith(".jpg") || uri.endsWith(".jpeg") || uri.endsWith(".png")
                || uri.endsWith(".gif") || uri.endsWith(".svg") || uri.endsWith(".ico")
                || uri.endsWith(".woff") || uri.endsWith(".woff2") || uri.endsWith(".ttf")
                || uri.endsWith(".eot");
    }
}
