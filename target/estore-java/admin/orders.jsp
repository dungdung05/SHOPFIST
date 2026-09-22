<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Đơn Hàng" scope="request"/>
<c:set var="activeMenu" value="orders" scope="request"/>
<%@ include file="_header.jsp" %>

<div class="admin-card">
    <div class="admin-header">
        <h4 class="m-0">Danh Sách Đơn Hàng</h4>
    </div>

    <div class="mb-3">
        <a class="btn btn-sm ${empty currentStatusFilter ? 'btn-dark' : 'btn-outline-dark'}"
           href="${pageContext.request.contextPath}/admin/orders">Tất Cả</a>
        <c:forEach var="s" items="${statuses}">
            <a class="btn btn-sm ${currentStatusFilter eq s ? 'btn-dark' : 'btn-outline-dark'}"
               href="${pageContext.request.contextPath}/admin/orders?status=${s}">${s}</a>
        </c:forEach>
    </div>

    <table class="table table-bordered table-hover align-middle">
        <thead class="thead-light">
            <tr>
                <th style="width:70px;">Mã Đơn</th>
                <th>Khách Hàng</th>
                <th>Người Nhận</th>
                <th>Ngày Đặt</th>
                <th>Tổng Tiền</th>
                <th style="width:130px;">Trạng Thái</th>
                <th style="width:110px;"></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="o" items="${orders}">
                <tr>
                    <td>#${o.id}</td>
                    <td>${o.buyerUsername}<br><small class="text-muted">${o.buyerFullName}</small></td>
                    <td>${o.receiverName}<br><small class="text-muted">${o.receiverPhone}</small></td>
                    <td><fmt:formatDate value="${o.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                    <td>${o.formattedTotal}</td>
                    <td>
                        <c:choose>
                            <c:when test="${o.status == 'Hoàn thành'}"><span class="badge badge-success">${o.status}</span></c:when>
                            <c:when test="${o.status == 'Đã huỷ'}"><span class="badge badge-danger">${o.status}</span></c:when>
                            <c:when test="${o.status == 'Đang giao'}"><span class="badge badge-primary">${o.status}</span></c:when>
                            <c:otherwise><span class="badge badge-warning">${o.status}</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/admin/orders?action=view&id=${o.id}">
                            <i class="fa fa-eye"></i> Xem
                        </a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty orders}">
                <tr><td colspan="7" class="text-center text-muted">Không có đơn hàng nào.</td></tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="_footer.jsp" %>
