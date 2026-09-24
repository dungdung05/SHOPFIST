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

    <form method="post" action="${pageContext.request.contextPath}/admin/products" enctype="multipart/form-data">
        <input type="hidden" name="id" value="${product.id}">
        <input type="hidden" name="currentImage" value="${product.imageFileName}">

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
                <label>Ảnh Sản Phẩm</label>
                <c:if test="${not empty product.imageFileName}">
                    <div class="mb-2">
                        <img id="currentImagePreview" src="${pageContext.request.contextPath}/img/${product.imageFileName}"
                             alt="Ảnh hiện tại" style="width:80px;height:80px;object-fit:cover;border-radius:4px;border:1px solid #ddd;">
                        <small class="form-text text-muted">Ảnh hiện tại — chọn ảnh mới bên dưới nếu muốn đổi.</small>
                    </div>
                </c:if>
                <c:if test="${empty product.imageFileName}">
                    <img id="currentImagePreview" src="" alt="" style="display:none;width:80px;height:80px;object-fit:cover;border-radius:4px;border:1px solid #ddd;">
                </c:if>
                <input type="file" class="form-control-file" name="imageFile" accept="image/*" onchange="previewSelectedImage(this)">
                <small class="form-text text-muted">Bỏ trống nếu không muốn đổi ảnh.</small>
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

<script>
    // Xem trước ảnh vừa chọn ngay trên form, chưa cần lưu mới thấy.
    function previewSelectedImage(input) {
        if (!input.files || !input.files[0]) {
            return;
        }
        var preview = document.getElementById('currentImagePreview');
        preview.src = URL.createObjectURL(input.files[0]);
        preview.style.display = 'inline-block';
    }
</script>

<%@ include file="_footer.jsp" %>
