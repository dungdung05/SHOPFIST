<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Người Dùng" scope="request"/>
<c:set var="activeMenu" value="users" scope="request"/>
<%@ include file="_header.jsp" %>

<div class="admin-card">
    <div class="admin-header">
        <h4 class="m-0">Danh Sách Người Dùng</h4>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <table class="table table-bordered table-hover align-middle">
        <thead class="thead-light">
            <tr>
                <th style="width:60px;">ID</th>
                <th>Tên Đăng Nhập</th>
                <th>Họ Tên</th>
                <th>Email</th>
                <th>Điện Thoại</th>
                <th style="width:90px;">Quyền</th>
                <th style="width:110px;">Trạng Thái</th>
                <th style="width:220px;">Hành Động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="u" items="${users}">
                <tr>
                    <td>${u.id}</td>
                    <td>${u.username}</td>
                    <td>${u.fullName}</td>
                    <td>${u.email}</td>
                    <td>${u.phone}</td>
                    <td>
                        <c:choose>
                            <c:when test="${u.admin}"><span class="badge badge-warning">Admin</span></c:when>
                            <c:otherwise><span class="badge badge-secondary">User</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${u.locked}"><span class="badge badge-danger">Đã khoá</span></c:when>
                            <c:otherwise><span class="badge badge-success">Hoạt động</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a class="btn btn-sm btn-outline-primary" title="Xem thông tin"
                           href="${pageContext.request.contextPath}/admin/users?action=view&id=${u.id}">
                            <i class="fa fa-eye"></i> Xem
                        </a>

                        <c:if test="${not u.admin}">
                            <c:choose>
                                <c:when test="${u.locked}">
                                    <a class="btn btn-sm btn-outline-success" title="Mở khoá tài khoản"
                                       href="${pageContext.request.contextPath}/admin/users?action=unlock&id=${u.id}">
                                        <i class="fa fa-unlock"></i> Mở Khoá
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <a class="btn btn-sm btn-outline-danger" title="Khoá tài khoản"
                                       href="${pageContext.request.contextPath}/admin/users?action=lock&id=${u.id}"
                                       onclick="return confirm('Khoá tài khoản \'${u.username}\'?');">
                                        <i class="fa fa-lock"></i> Khoá
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty users}">
                <tr><td colspan="8" class="text-center text-muted">Chưa có người dùng nào.</td></tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="_footer.jsp" %>
