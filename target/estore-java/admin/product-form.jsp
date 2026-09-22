<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="isEdit" value="${not empty product.id and product.id > 0}"/>
<c:set var="pageTitle" value="${isEdit ? 'Sửa Sản Phẩm' : 'Thêm Sản Phẩm'}" scope="request"/>
<c:set var="activeMenu" value="products" scope="request"/>
<%@ include file="_header.jsp" %>

<div class="admin-card" style="max-width:650px;">
    <h4 class="mb-3">
        <c:choose>
            <c:when test="${isEdit}">Sửa Sản Phẩm #${product.id}</c:when>
            <c:otherwise>Thêm Sản Phẩm Mới</c:otherwise>
        </c:choose>
    </h4>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/admin/products">
        <input type="hidden" name="id" value="${product.id}">

        <div class="form-group">
            <label>Tên Sản Phẩm <span class="text-danger">*</span></label>
            <input type="text" class="form-control" name="name" value="${product.name}" required>
        </div>

        <div class="form-row">
            <div class="form-group col-md-6">
                <label>Giá (đ) <span class="text-danger">*</span></label>
                <input type="number" step="1000" min="0" class="form-control" name="price" value="${product.price}" required>
            </div>
            <div class="form-group col-md-6">
                <label>Số Lượng Tồn Kho</label>
                <input type="number" min="0" class="form-control" name="quantity" value="${product.quantity}">
            </div>
        </div>

        <div class="form-row">
            <div class="form-group col-md-6">
                <label>Danh Mục</label>
                <input type="text" class="form-control" name="category" value="${product.category}"
                       placeholder="VD: Áo, Quần, Giày, Phụ kiện" list="categoryOptions">
                <datalist id="categoryOptions">
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat}">
                    </c:forEach>
                </datalist>
            </div>
            <div class="form-group col-md-6">
                <label>Tên File Ảnh</label>
                <input type="text" class="form-control" name="image" value="${product.imageFileName}"
                       placeholder="VD: product-1.jpg">
                <small class="form-text text-muted">Ảnh cần đặt sẵn trong thư mục <code>webapp/img/</code>.</small>
            </div>
        </div>

        <div class="form-group">
            <label>Mô Tả</label>
            <textarea class="form-control" name="description" rows="3">${product.description}</textarea>
        </div>

        <button type="submit" class="btn btn-primary"><i class="fa fa-save"></i> Lưu Sản Phẩm</button>
        <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-secondary">Huỷ</a>
    </form>
</div>

<%@ include file="_footer.jsp" %>
