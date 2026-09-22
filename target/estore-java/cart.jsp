<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <title>Giỏ Hàng - E Store</title>
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
                <span>Trang Chủ</span><i class="fa fa-angle-right"></i><span>Giỏ Hàng</span>
            </div>
        </div>

        <div class="cart-section">
            <div class="container-fluid">
                <h1 class="cart-heading">Giỏ Hàng Của Bạn</h1>

                <c:choose>
                    <c:when test="${empty sessionScope.cart}">
                        <div class="cart-empty">
                            <i class="fa fa-shopping-cart"></i>
                            <p>Giỏ hàng của bạn đang trống.</p>
                            <a class="pd-btn pd-btn-solid" href="${pageContext.request.contextPath}/index.jsp">
                                Tiếp Tục Mua Sắm
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="cart-table-wrap">
                            <table class="cart-table">
                                <thead>
                                    <tr>
                                        <th style="width:36px;">
                                            <input type="checkbox" id="cartSelectAll" checked>
                                        </th>
                                        <th class="cart-col-product">Sản Phẩm</th>
                                        <th>Đơn Giá</th>
                                        <th>Số Lượng</th>
                                        <th>Số Tiền</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${sessionScope.cart}">
                                        <tr>
                                            <td>
                                                <input type="checkbox" class="cart-select"
                                                       data-item-key="${item.itemKey}"
                                                       data-subtotal="${item.subtotal}" checked>
                                            </td>
                                            <td class="cart-col-product">
                                                <div class="cart-product">
                                                    <img src="${item.image}" alt="${item.name}">
                                                    <div>
                                                        <a href="${pageContext.request.contextPath}/product-detail?id=${item.productId}">${item.name}</a>
                                                        <c:if test="${not empty item.variantName}">
                                                            <p class="cart-variant">Phân loại: ${item.variantName}</p>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>${item.formattedUnitPrice}</td>
                                            <td>
                                                <form method="post" action="${pageContext.request.contextPath}/cart" class="cart-qty-form">
                                                    <input type="hidden" name="action" value="update">
                                                    <input type="hidden" name="itemKey" value="${item.itemKey}">
                                                    <div class="pd-qty">
                                                        <button type="submit" name="quantity" value="${item.quantity - 1}">-</button>
                                                        <input type="text" value="${item.quantity}" readonly>
                                                        <button type="submit" name="quantity" value="${item.quantity + 1}">+</button>
                                                    </div>
                                                </form>
                                            </td>
                                            <td class="cart-subtotal">${item.formattedSubtotal}</td>
                                            <td>
                                                <form method="post" action="${pageContext.request.contextPath}/cart">
                                                    <input type="hidden" name="action" value="remove">
                                                    <input type="hidden" name="itemKey" value="${item.itemKey}">
                                                    <button type="submit" class="cart-remove-btn" title="Xoá">
                                                        <i class="fa fa-trash-alt"></i>
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <div class="cart-summary">
                            <a class="pd-btn pd-btn-outline" href="${pageContext.request.contextPath}/index.jsp">
                                <i class="fa fa-arrow-left"></i> Tiếp Tục Mua Sắm
                            </a>
                            <div class="cart-total-box">
                                <span>Tổng Cộng (<span id="cartSelectedCount">0</span> mục đã chọn):</span>
                                <b id="cartSelectedTotal">0₫</b>
                            </div>
                            <button type="button" id="cartCheckoutBtn" class="pd-btn pd-btn-solid">
                                <i class="fa fa-check"></i> Đặt Hàng
                            </button>
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

        <script>
            // Bấm +/- gửi luôn form cập nhật số lượng (submit theo nút được bấm)
            document.querySelectorAll('.cart-qty-form input[readonly]').forEach(function (input) {
                input.addEventListener('focus', function () { this.blur(); });
            });

            // ==== Tích chọn sản phẩm để thanh toán (giống Shopee/Lazada) ====
            (function () {
                var selectAll = document.getElementById('cartSelectAll');
                var checkboxes = document.querySelectorAll('.cart-select');
                var totalEl = document.getElementById('cartSelectedTotal');
                var countEl = document.getElementById('cartSelectedCount');
                var checkoutBtn = document.getElementById('cartCheckoutBtn');

                function formatMoney(value) {
                    return new Intl.NumberFormat('vi-VN').format(Math.round(value)) + '₫';
                }

                function recalc() {
                    var total = 0, count = 0;
                    checkboxes.forEach(function (cb) {
                        if (cb.checked) {
                            total += parseFloat(cb.getAttribute('data-subtotal')) || 0;
                            count++;
                        }
                    });
                    if (totalEl) totalEl.textContent = formatMoney(total);
                    if (countEl) countEl.textContent = count;
                    if (checkoutBtn) checkoutBtn.disabled = (count === 0);
                    if (selectAll) selectAll.checked = (count === checkboxes.length && count > 0);
                }

                checkboxes.forEach(function (cb) {
                    cb.addEventListener('change', recalc);
                });

                if (selectAll) {
                    selectAll.addEventListener('change', function () {
                        checkboxes.forEach(function (cb) { cb.checked = selectAll.checked; });
                        recalc();
                    });
                }

                if (checkoutBtn) {
                    checkoutBtn.addEventListener('click', function () {
                        var params = [];
                        checkboxes.forEach(function (cb) {
                            if (cb.checked) {
                                params.push('itemKeys=' + encodeURIComponent(cb.getAttribute('data-item-key')));
                            }
                        });
                        if (params.length === 0) {
                            alert('Vui lòng chọn ít nhất 1 sản phẩm để thanh toán.');
                            return;
                        }
                        window.location.href = '${pageContext.request.contextPath}/checkout?' + params.join('&');
                    });
                }

                recalc();
            })();
        </script>
    </body>
</html>
