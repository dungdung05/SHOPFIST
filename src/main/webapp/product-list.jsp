<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <title>Tìm Kiếm Sản Phẩm - E Store</title>
        <meta content="width=device-width, initial-scale=1.0" name="viewport">

        <link href="img/favicon.ico" rel="icon">
        <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,400|Source+Code+Pro:700,900&display=swap" rel="stylesheet">
        <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
        <link href="css/style.css" rel="stylesheet">
        <link href="css/product-detail.css" rel="stylesheet">
        <link href="css/product-list.css" rel="stylesheet">
    </head>

    <body>
        <%@ include file="header.jsp" %>

        <div class="pd-breadcrumb">
            <div class="container-fluid">
                <span>Trang Chủ</span><i class="fa fa-angle-right"></i><span>Sản Phẩm</span>
                <c:if test="${not empty keyword}">
                    <i class="fa fa-angle-right"></i><span>Kết quả cho "${keyword}"</span>
                </c:if>
            </div>
        </div>

        <!-- Search Bar Start -->
        <div class="pl-search-bar">
            <div class="container-fluid">
                <form method="get" action="${pageContext.request.contextPath}/products" class="pl-search-form">
                    <input type="hidden" name="category" value="${selectedCategory}">
                    <input type="hidden" name="sort" value="${selectedSort}">
                    <input type="hidden" name="minPrice" value="${minPrice}">
                    <input type="hidden" name="maxPrice" value="${maxPrice}">
                    <input type="text" name="q" value="${keyword}" placeholder="Tìm kiếm sản phẩm...">
                    <button type="submit"><i class="fa fa-search"></i></button>
                </form>
            </div>
        </div>
        <!-- Search Bar End -->

        <div class="pl-section">
            <div class="container-fluid">
                <div class="row">

                    <!-- Filter Sidebar -->
                    <div class="col-lg-3">
                        <form method="get" action="${pageContext.request.contextPath}/products" class="pl-filter-form">
                            <input type="hidden" name="q" value="${keyword}">
                            <input type="hidden" name="sort" value="${selectedSort}">

                            <div class="pl-filter-box">
                                <h3>Danh Mục</h3>
                                <ul class="pl-filter-list">
                                    <li>
                                        <label>
                                            <input type="radio" name="category" value=""
                                                   ${empty selectedCategory ? 'checked' : ''} onchange="this.form.submit()">
                                            Tất Cả
                                        </label>
                                    </li>
                                    <c:forEach var="cat" items="${categories}">
                                        <li>
                                            <label>
                                                <input type="radio" name="category" value="${cat}"
                                                       ${selectedCategory eq cat ? 'checked' : ''} onchange="this.form.submit()">
                                                ${cat}
                                            </label>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>

                            <div class="pl-filter-box">
                                <h3>Khoảng Giá</h3>
                                <div class="pl-price-inputs">
                                    <input type="number" name="minPrice" placeholder="Từ" value="${minPrice}" min="0">
                                    <span>-</span>
                                    <input type="number" name="maxPrice" placeholder="Đến" value="${maxPrice}" min="0">
                                </div>
                                <button type="submit" class="pd-btn pd-btn-solid pl-filter-apply">Áp Dụng</button>
                            </div>

                            <a class="pl-clear-filter" href="${pageContext.request.contextPath}/products">
                                <i class="fa fa-times-circle"></i> Xoá Tất Cả Bộ Lọc
                            </a>
                        </form>
                    </div>

                    <!-- Results -->
                    <div class="col-lg-9">
                        <div class="pl-result-bar">
                            <span class="pl-result-count">
                                Tìm thấy <b>${resultCount}</b> sản phẩm
                                <c:if test="${not empty keyword}"> cho từ khoá "<b>${keyword}</b>"</c:if>
                            </span>

                            <form method="get" action="${pageContext.request.contextPath}/products" class="pl-sort-form">
                                <input type="hidden" name="q" value="${keyword}">
                                <input type="hidden" name="category" value="${selectedCategory}">
                                <input type="hidden" name="minPrice" value="${minPrice}">
                                <input type="hidden" name="maxPrice" value="${maxPrice}">
                                <label for="pl-sort-select">Sắp Xếp:</label>
                                <select id="pl-sort-select" name="sort" onchange="this.form.submit()">
                                    <option value="default" ${selectedSort eq 'default' ? 'selected' : ''}>Liên Quan</option>
                                    <option value="price_asc" ${selectedSort eq 'price_asc' ? 'selected' : ''}>Giá: Thấp Đến Cao</option>
                                    <option value="price_desc" ${selectedSort eq 'price_desc' ? 'selected' : ''}>Giá: Cao Đến Thấp</option>
                                    <option value="sold_desc" ${selectedSort eq 'sold_desc' ? 'selected' : ''}>Bán Chạy Nhất</option>
                                    <option value="rating_desc" ${selectedSort eq 'rating_desc' ? 'selected' : ''}>Đánh Giá Cao Nhất</option>
                                    <option value="name_asc" ${selectedSort eq 'name_asc' ? 'selected' : ''}>Tên: A - Z</option>
                                </select>
                            </form>
                        </div>

                        <c:choose>
                            <c:when test="${empty products}">
                                <div class="pl-empty">
                                    <i class="fa fa-search"></i>
                                    <p>Không tìm thấy sản phẩm phù hợp.</p>
                                    <a class="pd-btn pd-btn-outline" href="${pageContext.request.contextPath}/products">
                                        Xoá Bộ Lọc
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="row">
                                    <c:forEach var="p" items="${products}">
                                        <div class="col-lg-4 col-md-6 pl-item-col">
                                            <div class="product-item">
                                                <div class="product-title">
                                                    <a href="${pageContext.request.contextPath}/product-detail?id=${p.id}">${p.name}</a>
                                                    <div class="ratting">
                                                        <c:forEach begin="1" end="5" varStatus="s">
                                                            <i class="fa fa-star ${s.index <= p.rating ? '' : 'pd-star-empty'}"></i>
                                                        </c:forEach>
                                                    </div>
                                                </div>
                                                <div class="product-image">
                                                    <a href="${pageContext.request.contextPath}/product-detail?id=${p.id}">
                                                        <img src="${p.thumbnail}" alt="${p.name}">
                                                    </a>
                                                    <div class="product-action">
                                                        <a href="${pageContext.request.contextPath}/cart?action=add&amp;productId=${p.id}&amp;quantity=1"
                                                           title="Thêm vào giỏ hàng">
                                                            <i class="fa fa-cart-plus"></i>
                                                        </a>
                                                        <a href="${pageContext.request.contextPath}/product-detail?id=${p.id}" title="Xem chi tiết">
                                                            <i class="fa fa-search"></i>
                                                        </a>
                                                    </div>
                                                </div>
                                                <div class="product-price">
                                                    <h3>${p.formattedPrice}</h3>
                                                    <a class="btn" href="${pageContext.request.contextPath}/product-detail?id=${p.id}">
                                                        <i class="fa fa-eye"></i>Xem
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
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
