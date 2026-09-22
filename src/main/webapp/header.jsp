<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="headerCartCount" value="0" />
<c:forEach var="cartItem" items="${sessionScope.cart}">
    <c:set var="headerCartCount" value="${headerCartCount + cartItem.quantity}" />
</c:forEach>

<c:if test="${not empty sessionScope.flashError}">
    <div class="container-fluid" style="padding-top:10px;">
        <div class="alert alert-danger">${sessionScope.flashError}</div>
    </div>
    <% session.removeAttribute("flashError"); %>
</c:if>
<!-- Top bar Start -->
<div class="top-bar">
    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-6">
                <i class="fa fa-envelope"></i>
                support@email.com
            </div>
            <div class="col-sm-6">
                <i class="fa fa-phone-alt"></i>
                +012-345-6789
            </div>
        </div>
    </div>
</div>
<!-- Top bar End -->

<!-- Nav Bar Start -->
<div class="nav">
    <div class="container-fluid">
        <nav class="navbar navbar-expand-md bg-dark navbar-dark">
            <a href="#" class="navbar-brand">MENU</a>
            <button type="button" class="navbar-toggler" data-toggle="collapse" data-target="#navbarCollapse">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse justify-content-between" id="navbarCollapse">
                <div class="navbar-nav mr-auto">
                    <a href="${pageContext.request.contextPath}/index.jsp" class="nav-item nav-link">Home</a>
                    <a href="${pageContext.request.contextPath}/products" class="nav-item nav-link">Products</a>
                    <a href="${pageContext.request.contextPath}/cart" class="nav-item nav-link">
                        Giỏ Hàng
                        <c:if test="${headerCartCount > 0}">
                            <span class="nav-cart-badge">${headerCartCount}</span>
                        </c:if>
                    </a>
                    <a href="${pageContext.request.contextPath}/checkout" class="nav-item nav-link">Checkout</a>
                    <a href="contact.html" class="nav-item nav-link">Contact Us</a>
                </div>
                <div class="navbar-nav ml-auto">
                    <div class="nav-item dropdown">
                        <c:choose>
                            <c:when test="${not empty sessionScope.user}">
                                <a href="#" class="nav-link dropdown-toggle" data-toggle="dropdown">
                                    <i class="fa fa-user"></i> Xin chào, ${sessionScope.user.fullName}
                                </a>
                                <div class="dropdown-menu dropdown-menu-right">
                                    <a href="${pageContext.request.contextPath}/profile" class="dropdown-item">Hồ sơ của tôi</a>
                                    <a href="${pageContext.request.contextPath}/orders" class="dropdown-item">
                                        <i class="fa fa-receipt"></i> Đơn Hàng Của Tôi
                                    </a>
                                    <c:if test="${sessionScope.user.admin}">
                                        <a href="${pageContext.request.contextPath}/admin" class="dropdown-item">
                                            <i class="fa fa-cog"></i> Trang Quản Trị
                                        </a>
                                    </c:if>
                                    <a href="${pageContext.request.contextPath}/logout" class="dropdown-item">Đăng xuất</a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <a href="#" class="nav-link dropdown-toggle" data-toggle="dropdown">Tài khoản</a>
                                <div class="dropdown-menu dropdown-menu-right">
                                    <a href="${pageContext.request.contextPath}/login" class="dropdown-item">Đăng nhập</a>
                                    <a href="${pageContext.request.contextPath}/register" class="dropdown-item">Đăng ký</a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </nav>
    </div>
</div>
<!-- Nav Bar End -->
