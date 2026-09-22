<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Thông Tin Khách Hàng" scope="request"/>
<c:set var="activeMenu" value="users" scope="request"/>
<%@ include file="_header.jsp" %>

<div class="admin-card">
    <div class="admin-header d-flex justify-content-between align-items-center">
        <h4 class="m-0">Thông Tin Khách Hàng</h4>
        <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/admin/users">
            <i class="fa fa-arrow-left"></i> Quay Lại Danh Sách
        </a>
    </div>

    <table class="table table-bordered">
        <tr>
            <th style="width:220px;" class="bg-light">ID</th>
            <td>${viewUser.id}</td>
        </tr>
        <tr>
            <th class="bg-light">Tên Đăng Nhập</th>
            <td>${viewUser.username}</td>
        </tr>
        <tr>
            <th class="bg-light">Họ Tên</th>
            <td>${viewUser.fullName}</td>
        </tr>
        <tr>
            <th class="bg-light">Email</th>
            <td>${viewUser.email}</td>
        </tr>
        <tr>
            <th class="bg-light">Điện Thoại</th>
            <td>${viewUser.phone}</td>
        </tr>
        <tr>
            <th class="bg-light">Địa Chỉ</th>
            <td>${viewUser.address}</td>
        </tr>
        <tr>
            <th class="bg-light">Ngày Tạo Tài Khoản</th>
            <td><fmt:formatDate value="${viewUser.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
        </tr>
        <tr>
            <th class="bg-light">Quyền</th>
            <td>
                <c:choose>
                    <c:when test="${viewUser.admin}"><span class="badge badge-warning">Admin</span></c:when>
                    <c:otherwise><span class="badge badge-secondary">User</span></c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th class="bg-light">Trạng Thái</th>
            <td>
                <c:choose>
                    <c:when test="${viewUser.locked}"><span class="badge badge-danger">Đã khoá</span></c:when>
                    <c:otherwise><span class="badge badge-success">Hoạt động</span></c:otherwise>
                </c:choose>
            </td>
        </tr>
    </table>

    <div class="mt-3">
        <c:if test="${not viewUser.admin}">
            <c:choose>
                <c:when test="${viewUser.locked}">
                    <a class="btn btn-outline-success" href="${pageContext.request.contextPath}/admin/users?action=unlock&id=${viewUser.id}">
                        <i class="fa fa-unlock"></i> Mở Khoá Tài Khoản
                    </a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-outline-danger" href="${pageContext.request.contextPath}/admin/users?action=lock&id=${viewUser.id}"
                       onclick="return confirm('Khoá tài khoản \'${viewUser.username}\'?');">
                        <i class="fa fa-lock"></i> Khoá Tài Khoản
                    </a>
                </c:otherwise>
            </c:choose>
        </c:if>
    </div>
</div>

<%@ include file="_footer.jsp" %>
