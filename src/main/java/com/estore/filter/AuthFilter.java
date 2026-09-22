package com.estore.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Chặn truy cập /profile, /checkout, /orders nếu chưa đăng nhập -> chuyển hướng về trang đăng nhập,
 * đăng nhập xong tự quay lại đúng trang đang định vào.
 */
@WebFilter(urlPatterns = {"/profile", "/checkout", "/orders"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("user") != null);

        if (loggedIn) {
            chain.doFilter(request, response);
        } else {
            String target = req.getContextPath() + req.getServletPath();
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" + java.net.URLEncoder.encode(target, "UTF-8"));
        }
    }

    @Override
    public void destroy() {
    }
}
