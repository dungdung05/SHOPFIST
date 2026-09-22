<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Sản Phẩm" scope="request"/>
<c:set var="activeMenu" value="products" scope="request"/>
<%@ include file="_header.jsp" %>

<div class="admin-card">
    <div class="admin-header">
        <h4 class="m-0">Danh Sách Sản Phẩm</h4>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/products?action=new">
            <i class="fa fa-plus"></i> Thêm Sản Phẩm
        </a>
    </div>

    <c:if test="${not empty param.deleted}">
        <div class="alert alert-success">Đã xoá sản phẩm.</div>
    </c:if>

    <table class="table table-bordered table-hover align-middle">
        <thead class="thead-light">
            <tr>
                <th style="width:60px;">ID</th>
                <th style="width:70px;">Ảnh</th>
                <th>Tên Sản Phẩm</th>
                <th style="width:110px;">Giá</th>
                <th style="width:120px;">Danh Mục</th>
                <th style="width:80px;">Tồn Kho</th>
                <th style="width:140px;">Hành Động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="p" items="${products}">
                <tr>
                    <td>${p.id}</td>
                    <td><img src="${pageContext.request.contextPath}/${p.thumbnail}" alt="${p.name}"
                             onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/img/logo.png';"></td>
                    <td>${p.name}</td>
                    <td>${p.formattedPrice}</td>
                    <td>${p.category}</td>
                    <td>${p.quantity}</td>
                    <td>
                        <a class="btn btn-sm btn-outline-secondary" title="Sửa"
                           href="${pageContext.request.contextPath}/admin/products?action=edit&id=${p.id}">
                            <i class="fa fa-edit"></i>
                        </a>
                        <a class="btn btn-sm btn-outline-danger" title="Xoá"
                           href="${pageContext.request.contextPath}/admin/products?action=delete&id=${p.id}"
                           onclick="return confirm('Xoá sản phẩm \'${p.name}\'?');">
                            <i class="fa fa-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty products}">
                <tr><td colspan="7" class="text-center text-muted">Chưa có sản phẩm nào.</td></tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="_footer.jsp" %>
