<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Tổng Quan" scope="request"/>
<c:set var="activeMenu" value="dashboard" scope="request"/>
<%@ include file="_header.jsp" %>

<div class="row mb-4">
    <div class="col-md-3">
        <div class="stat-box">
            <div class="stat-number">${productCount}</div>
            <div class="stat-label"><i class="fa fa-box"></i> Tổng Số Sản Phẩm</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-box">
            <div class="stat-number">${userCount}</div>
            <div class="stat-label"><i class="fa fa-users"></i> Tổng Số Người Dùng</div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="stat-box">
            <div class="stat-number">${adminCount}</div>
            <div class="stat-label"><i class="fa fa-user-shield"></i> Tài Khoản Admin</div>
        </div>
    </div>
    <div class="col-md-3">
        <a href="${pageContext.request.contextPath}/admin/orders?status=Chờ xác nhận" style="text-decoration:none;">
            <div class="stat-box">
                <div class="stat-number" style="color:#dc3545;">${pendingOrderCount}</div>
                <div class="stat-label"><i class="fa fa-receipt"></i> Đơn Hàng Chờ Xác Nhận</div>
            </div>
        </a>
    </div>
</div>

<div class="admin-card">
    <div class="admin-header">
        <h4 class="m-0"><i class="fa fa-exclamation-triangle text-warning"></i> Sản Phẩm Sắp Hết Hàng (≤ 10)</h4>
        <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/admin/products">
            Xem Tất Cả Sản Phẩm
        </a>
    </div>

    <c:choose>
        <c:when test="${empty lowStockProducts}">
            <p class="text-muted mb-0">Không có sản phẩm nào sắp hết hàng.</p>
        </c:when>
        <c:otherwise>
            <table class="table table-bordered table-hover">
                <thead class="thead-light">
                    <tr>
                        <th style="width:60px;">ID</th>
                        <th>Tên Sản Phẩm</th>
                        <th style="width:120px;">Danh Mục</th>
                        <th style="width:100px;">Tồn Kho</th>
                        <th style="width:100px;">Hành Động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${lowStockProducts}">
                        <tr class="${p.quantity == 0 ? 'table-danger' : 'table-warning'}">
                            <td>${p.id}</td>
                            <td>${p.name}</td>
                            <td>${p.category}</td>
                            <td>${p.quantity}</td>
                            <td>
                                <a class="btn btn-sm btn-outline-secondary"
                                   href="${pageContext.request.contextPath}/admin/products?action=edit&id=${p.id}">
                                    <i class="fa fa-edit"></i> Sửa
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="_footer.jsp" %>
