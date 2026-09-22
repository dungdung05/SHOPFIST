<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <title>${product.name} - E Store</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">

        <!-- Favicon -->
        <link href="img/favicon.ico" rel="icon">

        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,400|Source+Code+Pro:700,900&display=swap" rel="stylesheet">

        <!-- CSS Libraries -->
        <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="lib/slick/slick.css" rel="stylesheet">
        <link href="lib/slick/slick-theme.css" rel="stylesheet">

        <!-- Template Stylesheet -->
        <link href="css/style.css" rel="stylesheet">
        <link href="css/product-detail.css" rel="stylesheet">
    </head>

    <body>
        <%@ include file="header.jsp" %>

        <c:if test="${empty product}">
            <div class="container-fluid" style="padding:60px 15px;text-align:center;">
                <h2>Không tìm thấy sản phẩm.</h2>
                <a class="btn" href="${pageContext.request.contextPath}/index.jsp">Về Trang Chủ</a>
            </div>
        </c:if>

        <c:if test="${not empty product}">
        <!-- Breadcrumb Start -->
        <div class="pd-breadcrumb">
            <div class="container-fluid">
                <c:forEach var="crumb" items="${product.breadcrumb}" varStatus="st">
                    <span>${crumb}</span>
                    <c:if test="${!st.last}"><i class="fa fa-angle-right"></i></c:if>
                </c:forEach>
            </div>
        </div>
        <!-- Breadcrumb End -->

        <!-- Product Detail Start -->
        <div class="pd-section">
            <div class="container-fluid">
                <div class="row">

                    <!-- Gallery -->
                    <div class="col-lg-5">
                        <div class="pd-gallery">
                            <div class="pd-main-image">
                                <img id="pdMainImage" src="${product.thumbnail}" alt="${product.name}">
                            </div>
                            <div class="pd-thumbs">
                                <c:forEach var="img" items="${product.images}" varStatus="st">
                                    <div class="pd-thumb ${st.first ? 'active' : ''}" onclick="pdSwapImage(this, '${img}')">
                                        <img src="${img}" alt="Thumbnail">
                                    </div>
                                </c:forEach>
                            </div>
                            <div class="pd-share">
                                <span>Chia Sẻ:</span>
                                <a href="#"><i class="fab fa-facebook-f"></i></a>
                                <a href="#"><i class="fab fa-twitter"></i></a>
                                <a href="#"><i class="fab fa-pinterest-p"></i></a>
                                <a href="#" class="pd-like"><i class="fa fa-heart"></i> Đã Thích (481)</a>
                            </div>
                        </div>
                    </div>

                    <!-- Info -->
                    <div class="col-lg-7">
                        <div class="pd-info">
                            <c:if test="${not empty product.badge}">
                                <span class="pd-badge">${product.badge}</span>
                            </c:if>
                            <h1 class="pd-title">${product.name}</h1>

                            <div class="pd-meta">
                                <span class="pd-rating">
                                    <b>${product.rating}</b>
                                    <c:forEach begin="1" end="5" varStatus="s">
                                        <i class="fa fa-star ${s.index <= product.rating ? '' : 'pd-star-empty'}"></i>
                                    </c:forEach>
                                </span>
                                <span class="pd-sep">|</span>
                                <span><b>${product.reviewCount}</b> Đánh Giá</span>
                                <span class="pd-sep">|</span>
                                <span>Đã Bán <b>${product.soldCount}</b></span>
                            </div>

                            <div class="pd-price-box">
                                <c:choose>
                                    <c:when test="${product.hasPriceRange}">
                                        <span class="pd-price">${product.formattedPrice} - ${product.formattedMaxPrice}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="pd-price">${product.formattedPrice}</span>
                                    </c:otherwise>
                                </c:choose>
                                <span class="pd-price-original">${product.formattedOriginalPrice} - ${product.formattedMaxOriginalPrice}</span>
                                <span class="pd-discount">-${product.discountPercent}%</span>
                            </div>

                            <div class="pd-row">
                                <div class="pd-row-label">Vận Chuyển</div>
                                <div class="pd-row-content">
                                    <span class="pd-shipping-tag"><i class="fa fa-bolt"></i> Hoả Tốc</span>
                                    ${product.shippingInfo}
                                </div>
                            </div>

                            <div class="pd-row">
                                <div class="pd-row-label">An Tâm Mua Sắm</div>
                                <div class="pd-row-content">
                                    <i class="fa fa-shield-alt pd-shield"></i>
                                    Trả Hàng Miễn Phí 15 Ngày &middot; Bảo Vệ Người Tiêu Dùng E Store
                                </div>
                            </div>

                            <form method="post" action="${pageContext.request.contextPath}/cart" id="pdAddToCartForm">
                                <input type="hidden" name="productId" value="${product.id}">
                                <input type="hidden" name="variantName" id="pdVariantName"
                                       value="${not empty product.variants ? product.variants[0].name : ''}">

                                <c:if test="${not empty product.variants}">
                                <div class="pd-row">
                                    <div class="pd-row-label">Phân Loại</div>
                                    <div class="pd-row-content pd-variants">
                                        <c:forEach var="v" items="${product.variants}" varStatus="st">
                                            <button type="button"
                                                    class="pd-variant-btn ${st.first ? 'active' : ''}"
                                                    data-name="${v.name}"
                                                    data-price="${v.formattedPrice}"
                                                    onclick="pdSelectVariant(this)">
                                                ${v.name}
                                            </button>
                                        </c:forEach>
                                    </div>
                                </div>
                                </c:if>

                                <div class="pd-row">
                                    <div class="pd-row-label">Số Lượng</div>
                                    <div class="pd-row-content">
                                        <div class="pd-qty">
                                            <button type="button" onclick="pdChangeQty(-1)">-</button>
                                            <input type="text" name="quantity" id="pdQty" value="1" readonly>
                                            <button type="button" onclick="pdChangeQty(1)">+</button>
                                        </div>
                                        <span class="pd-stock">Còn Hàng</span>
                                    </div>
                                </div>

                                <div class="pd-actions">
                                    <button type="submit" name="action" value="add" class="pd-btn pd-btn-outline">
                                        <i class="fa fa-cart-plus"></i> Thêm Vào Giỏ Hàng
                                    </button>
                                    <button type="submit" name="action" value="buynow" class="pd-btn pd-btn-solid">
                                        Mua Ngay
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Product Detail End -->

        <!-- Shop Info Start -->
        <div class="pd-shop">
            <div class="container-fluid">
                <div class="row align-items-center">
                    <div class="col-md-4">
                        <div class="pd-shop-brief">
                            <img src="${product.shopAvatar}" alt="Shop Avatar">
                            <div>
                                <h3>${product.shopName}</h3>
                                <p>Hoạt Động 2 Phút Trước</p>
                                <a href="${pageContext.request.contextPath}/login" class="pd-btn pd-btn-outline pd-btn-sm">
                                    <i class="fa fa-comment-dots"></i> Chat Ngay
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-8">
                        <div class="pd-shop-stats">
                            <div><span>${product.shopReviews}</span>Đánh Giá</div>
                            <div><span>${product.shopResponseRate}</span>Tỉ Lệ Phản Hồi</div>
                            <div><span>${product.shopJoinDate}</span>Tham Gia</div>
                            <div><span>${product.shopProductCount}</span>Sản Phẩm</div>
                            <div><span>${product.shopFollowers}</span>Người Theo Dõi</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Shop Info End -->

        <!-- Description Start -->
        <div class="pd-description">
            <div class="container-fluid">
                <h2>Chi Tiết Sản Phẩm</h2>
                <p>${product.description}</p>
                <ul>
                    <c:forEach var="h" items="${product.highlights}">
                        <li><i class="fa fa-check-circle"></i> ${h}</li>
                    </c:forEach>
                </ul>
            </div>
        </div>
        <!-- Description End -->

        <!-- Related Products Start -->
        <c:if test="${not empty relatedProducts}">
        <div class="recent-product product">
            <div class="container-fluid">
                <div class="section-header">
                    <h1>Sản Phẩm Liên Quan</h1>
                </div>
                <div class="row align-items-center">
                    <c:forEach var="rp" items="${relatedProducts}">
                        <div class="col-lg-3 col-md-6">
                            <div class="product-item">
                                <div class="product-title">
                                    <a href="${pageContext.request.contextPath}/product-detail?id=${rp.id}">${rp.name}</a>
                                    <div class="ratting">
                                        <c:forEach begin="1" end="5" varStatus="s">
                                            <i class="fa fa-star ${s.index <= rp.rating ? '' : 'pd-star-empty'}"></i>
                                        </c:forEach>
                                    </div>
                                </div>
                                <div class="product-image">
                                    <a href="${pageContext.request.contextPath}/product-detail?id=${rp.id}">
                                        <img src="${rp.thumbnail}" alt="Product Image">
                                    </a>
                                </div>
                                <div class="product-price">
                                    <h3>${rp.formattedPrice}</h3>
                                    <a class="btn" href="${pageContext.request.contextPath}/product-detail?id=${rp.id}">
                                        <i class="fa fa-eye"></i>Xem
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
        </c:if>
        <!-- Related Products End -->
        </c:if>

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

        <!-- JavaScript Libraries -->
        <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.bundle.min.js"></script>
        <script src="lib/easing/easing.min.js"></script>
        <script src="lib/slick/slick.min.js"></script>
        <script src="js/main.js"></script>

        <script>
            function pdSwapImage(thumbEl, src) {
                document.getElementById('pdMainImage').src = src;
                document.querySelectorAll('.pd-thumb').forEach(function (el) {
                    el.classList.remove('active');
                });
                thumbEl.classList.add('active');
            }

            function pdSelectVariant(btn) {
                document.querySelectorAll('.pd-variant-btn').forEach(function (el) {
                    el.classList.remove('active');
                });
                btn.classList.add('active');
                var priceBox = document.querySelector('.pd-price-box .pd-price');
                if (priceBox && btn.dataset.price) {
                    priceBox.textContent = btn.dataset.price;
                }
                var variantInput = document.getElementById('pdVariantName');
                if (variantInput && btn.dataset.name) {
                    variantInput.value = btn.dataset.name;
                }
            }

            function pdChangeQty(delta) {
                var input = document.getElementById('pdQty');
                var current = parseInt(input.value, 10) || 1;
                var next = current + delta;
                if (next < 1) next = 1;
                input.value = next;
            }
        </script>
    </body>
</html>
