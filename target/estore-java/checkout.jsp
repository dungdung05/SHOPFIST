<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <title>Đặt Hàng - E Store</title>
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
                <span>Trang Chủ</span><i class="fa fa-angle-right"></i>
                <span>Giỏ Hàng</span><i class="fa fa-angle-right"></i>
                <span>Đặt Hàng</span>
            </div>
        </div>

        <div class="cart-section">
            <div class="container-fluid">
                <h1 class="cart-heading">Thông Tin Đặt Hàng</h1>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <div class="row">
                    <div class="col-lg-7">
                        <form method="post" action="${pageContext.request.contextPath}/checkout">
                            <div class="form-group">
                                <label>Họ Tên Người Nhận</label>
                                <input type="text" name="receiverName" class="form-control"
                                       value="${not empty receiverName ? receiverName : sessionScope.user.fullName}" required>
                            </div>
                            <div class="form-group">
                                <label>Số Điện Thoại</label>
                                <input type="text" name="receiverPhone" class="form-control"
                                       value="${not empty receiverPhone ? receiverPhone : sessionScope.user.phone}" required>
                            </div>
                            <div class="form-group">
                                <label>Địa Chỉ Nhận Hàng</label>
                                <textarea name="shippingAddress" class="form-control" rows="2" required>${not empty shippingAddress ? shippingAddress : sessionScope.user.address}</textarea>
                            </div>
                            <div class="form-group">
                                <label>Ghi Chú (tuỳ chọn)</label>
                                <textarea name="note" class="form-control" rows="2">${note}</textarea>
                            </div>
                            <button type="submit" class="pd-btn pd-btn-solid">
                                <i class="fa fa-check"></i> Xác Nhận Đặt Hàng
                            </button>
                            <a class="pd-btn pd-btn-outline" href="${pageContext.request.contextPath}/cart">
                                <i class="fa fa-arrow-left"></i> Quay Lại Giỏ Hàng
                            </a>
                        </form>
                    </div>

                    <div class="col-lg-5">
                        <div class="cart-table-wrap">
                            <table class="cart-table">
                                <thead>
                                    <tr>
                                        <th class="cart-col-product">Sản Phẩm</th>
                                        <th>SL</th>
                                        <th>Tiền</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${cart}">
                                        <tr>
                                            <td class="cart-col-product">
                                                <div class="cart-product">
                                                    <img src="${item.image}" alt="${item.name}">
                                                    <div>
                                                        <span>${item.name}</span>
                                                        <c:if test="${not empty item.variantName}">
                                                            <p class="cart-variant">Phân loại: ${item.variantName}</p>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>${item.quantity}</td>
                                            <td class="cart-subtotal">${item.formattedSubtotal}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="cart-summary">
                            <span></span>
                            <div class="cart-total-box">
                                <span>Tổng Cộng:</span>
                                <b>${cartTotalFormatted}</b>
                            </div>
                        </div>
                    </div>
                </div>
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
