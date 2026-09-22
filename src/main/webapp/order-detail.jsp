<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <title>Chi Tiết Đơn Hàng #${order.id} - E Store</title>
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
                <a href="${pageContext.request.contextPath}/orders">Đơn Hàng Của Tôi</a><i class="fa fa-angle-right"></i>
                <span>#${order.id}</span>
            </div>
        </div>

        <div class="cart-section">
            <div class="container-fluid">
                <c:if test="${param.placed == '1'}">
                    <div class="alert alert-success">Đặt hàng thành công! Cảm ơn bạn đã mua sắm tại E Store.</div>
                </c:if>

                <h1 class="cart-heading">Đơn Hàng #${order.id}</h1>

                <div class="row">
                    <div class="col-lg-5">
                        <table class="table table-bordered">
                            <tr>
                                <th class="bg-light" style="width:160px;">Ngày Đặt</th>
                                <td><fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                            </tr>
                            <tr>
                                <th class="bg-light">Trạng Thái</th>
                                <td>
                                    <c:choose>
                                        <c:when test="${order.status == 'Hoàn thành'}"><span class="badge badge-success">${order.status}</span></c:when>
                                        <c:when test="${order.status == 'Đã huỷ'}"><span class="badge badge-danger">${order.status}</span></c:when>
                                        <c:when test="${order.status == 'Đang giao'}"><span class="badge badge-primary">${order.status}</span></c:when>
                                        <c:otherwise><span class="badge badge-warning">${order.status}</span></c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                            <tr>
                                <th class="bg-light">Người Nhận</th>
                                <td>${order.receiverName}</td>
                            </tr>
                            <tr>
                                <th class="bg-light">Điện Thoại</th>
                                <td>${order.receiverPhone}</td>
                            </tr>
                            <tr>
                                <th class="bg-light">Địa Chỉ</th>
                                <td>${order.shippingAddress}</td>
                            </tr>
                            <c:if test="${not empty order.note}">
                                <tr>
                                    <th class="bg-light">Ghi Chú</th>
                                    <td>${order.note}</td>
                                </tr>
                            </c:if>
                        </table>
                    </div>
                </div>

                <div class="cart-table-wrap">
                    <table class="cart-table">
                        <thead>
                            <tr>
                                <th class="cart-col-product">Sản Phẩm</th>
                                <th>Đơn Giá</th>
                                <th>Số Lượng</th>
                                <th>Thành Tiền</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${order.items}">
                                <tr>
                                    <td class="cart-col-product">
                                        <div class="cart-product">
                                            <img src="${item.productImage}" alt="${item.productName}">
                                            <div><span>${item.productName}</span></div>
                                        </div>
                                    </td>
                                    <td>${item.formattedUnitPrice}</td>
                                    <td>${item.quantity}</td>
                                    <td class="cart-subtotal">${item.formattedSubtotal}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="cart-summary">
                    <a class="pd-btn pd-btn-outline" href="${pageContext.request.contextPath}/orders">
                        <i class="fa fa-arrow-left"></i> Quay Lại Danh Sách Đơn Hàng
                    </a>
                    <c:if test="${order.status == cancellableStatus}">
                        <a class="pd-btn pd-btn-outline" style="color:#dc3545;border-color:#dc3545;"
                           href="${pageContext.request.contextPath}/orders?action=cancel&id=${order.id}"
                           onclick="return confirm('Huỷ đơn hàng #${order.id}? Hành động này không thể hoàn tác.');">
                            <i class="fa fa-times"></i> Huỷ Đơn Hàng
                        </a>
                    </c:if>
                    <div class="cart-total-box">
                        <span>Tổng Cộng:</span>
                        <b>${order.formattedTotal}</b>
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
