<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <title>Đơn Hàng Của Tôi - E Store</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">

        <link href="img/favicon.ico" rel="icon">
        <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,400|Source+Code+Pro:700,900&display=swap" rel="stylesheet">
        <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="css/style.css" rel="stylesheet">
        <link href="css/product-detail.css" rel="stylesheet">
        <link href="css/cart.css" rel="stylesheet">
    </head>

    <body>
        <%@ include file="header.jsp" %>

        <div class="pd-breadcrumb">
            <div class="container-fluid">
                <span>Trang Chủ</span><i class="fa fa-angle-right"></i><span>Đơn Hàng Của Tôi</span>
            </div>
        </div>

        <div class="cart-section">
            <div class="container-fluid">
                <h1 class="cart-heading">Đơn Hàng Của Tôi</h1>

                <c:choose>
                    <c:when test="${empty orders}">
                        <div class="cart-empty">
                            <i class="fa fa-receipt"></i>
                            <p>Bạn chưa có đơn hàng nào.</p>
                            <a class="pd-btn pd-btn-solid" href="${pageContext.request.contextPath}/products">
                                Mua Sắm Ngay
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="cart-table-wrap">
                            <table class="cart-table">
                                <thead>
                                    <tr>
                                        <th>Mã Đơn</th>
                                        <th>Ngày Đặt</th>
                                        <th>Tổng Tiền</th>
                                        <th>Trạng Thái</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="o" items="${orders}">
                                        <tr>
                                            <td>#${o.id}</td>
                                            <td><fmt:formatDate value="${o.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                            <td class="cart-subtotal">${o.formattedTotal}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${o.status == 'Hoàn thành'}"><span class="badge badge-success">${o.status}</span></c:when>
                                                    <c:when test="${o.status == 'Đã huỷ'}"><span class="badge badge-danger">${o.status}</span></c:when>
                                                    <c:when test="${o.status == 'Đang giao'}"><span class="badge badge-primary">${o.status}</span></c:when>
                                                    <c:otherwise><span class="badge badge-warning">${o.status}</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <a class="pd-btn pd-btn-outline" href="${pageContext.request.contextPath}/orders?id=${o.id}">
                                                    Xem Chi Tiết
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Footer Start -->
        <div class="footer">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-3 col-md-6">
                        <div class="footer-widget">
                            <h2>Get in Touch</h2>
                            <div class="contact-info">
                                <p><i class="fa fa-map-marker"></i>123 E Store, Los Angeles, USA</p>
                                <p><i class="fa fa-envelope"></i>email@example.com</p>
                                <p><i class="fa fa-phone"></i>+123-456-7890</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-3 col-md-6">
                        <div class="footer-widget">
                            <h2>Follow Us</h2>
                            <div class="contact-info">
                                <div class="social">
                                    <a href=""><i class="fab fa-twitter"></i></a>
                                    <a href=""><i class="fab fa-facebook-f"></i></a>
                                    <a href=""><i class="fab fa-linkedin-in"></i></a>
                                    <a href=""><i class="fab fa-instagram"></i></a>
                                    <a href=""><i class="fab fa-youtube"></i></a>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-3 col-md-6">
                        <div class="footer-widget">
                            <h2>Company Info</h2>
                            <ul>
                                <li><a href="#">About Us</a></li>
                                <li><a href="#">Privacy Policy</a></li>
                                <li><a href="#">Terms & Condition</a></li>
                            </ul>
                        </div>
                    </div>
                    <div class="col-lg-3 col-md-6">
                        <div class="footer-widget">
                            <h2>Purchase Info</h2>
                            <ul>
                                <li><a href="#">Payment Policy</a></li>
                                <li><a href="#">Shipping Policy</a></li>
                                <li><a href="#">Return Policy</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Footer End -->

        <a href="#" class="back-to-top"><i class="fa fa-chevron-up"></i></a>

        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.bundle.min.js"></script>
        <script src="lib/easing/easing.min.js"></script>
        <script src="js/main.js"></script>
    </body>
</html>
