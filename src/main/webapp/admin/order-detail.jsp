<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Chi Tiết Đơn Hàng" scope="request"/>
<c:set var="activeMenu" value="orders" scope="request"/>
<%@ include file="_header.jsp" %>

<div class="admin-card">
    <div class="admin-header d-flex justify-content-between align-items-center">
        <h4 class="m-0">Đơn Hàng #${order.id}</h4>
        <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/admin/orders">
            <i class="fa fa-arrow-left"></i> Quay Lại Danh Sách
        </a>
    </div>

    <div class="row">
        <div class="col-lg-6">
            <table class="table table-bordered">
                <tr>
                    <th class="bg-light" style="width:180px;">Ngày Đặt</th>
                    <td><fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                </tr>
                <tr>
                    <th class="bg-light">Khách Hàng</th>
                    <td>${order.buyerFullName} (${order.buyerUsername})</td>
                </tr>
                <tr>
                    <th class="bg-light">Người Nhận</th>
                    <td>${order.receiverName}</td>
                </tr>
                <tr>
                    <th class="bg-light">Điện Thoại</th>
                    <td>${order.receiverPhone}</td>
                </tr>
                <tr>
                    <th class="bg-light">Địa Chỉ</th>
                    <td>${order.shippingAddress}</td>
                </tr>
                <c:if test="${not empty order.note}">
                    <tr>
                        <th class="bg-light">Ghi Chú</th>
                        <td>${order.note}</td>
                    </tr>
                </c:if>
            </table>
        </div>

        <div class="col-lg-6">
            <div class="admin-card" style="box-shadow:none; border:1px solid #eee;">
                <h5>Trạng Thái Đơn Hàng</h5>
                <p>
                    Hiện tại:
                    <c:choose>
                        <c:when test="${order.status == 'Hoàn thành'}"><span class="badge badge-success">${order.status}</span></c:when>
                        <c:when test="${order.status == 'Đã huỷ'}"><span class="badge badge-danger">${order.status}</span></c:when>
                        <c:when test="${order.status == 'Đang giao'}"><span class="badge badge-primary">${order.status}</span></c:when>
                        <c:otherwise><span class="badge badge-warning">${order.status}</span></c:otherwise>
                    </c:choose>
                </p>

                <form method="get" action="${pageContext.request.contextPath}/admin/orders">
                    <input type="hidden" name="action" value="updateStatus">
                    <input type="hidden" name="id" value="${order.id}">
                    <div class="form-group">
                        <label>Đổi sang trạng thái:</label>
                        <select name="status" class="form-control">
                            <c:forEach var="s" items="${statuses}">
                                <option value="${s}" ${order.status == s ? 'selected' : ''}>${s}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-primary"
                            onclick="return confirm('Đổi trạng thái đơn hàng #${order.id}?');">
                        <i class="fa fa-check"></i> Cập Nhật Trạng Thái
                    </button>
                </form>
                <small class="text-muted d-block mt-2">
                    * Đổi sang "Đã huỷ" sẽ tự động hoàn lại số lượng đã trừ kho của đơn này.
                </small>
            </div>
        </div>
    </div>

    <table class="table table-bordered mt-3">
        <thead class="thead-light">
            <tr>
                <th>Sản Phẩm</th>
                <th>Đơn Giá</th>
                <th>Số Lượng</th>
                <th>Thành Tiền</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="item" items="${order.items}">
                <tr>
                    <td>
                        <img src="${pageContext.request.contextPath}/${item.productImage}" alt="${item.productName}"
                             style="width:40px;height:40px;object-fit:cover;border-radius:4px;margin-right:8px;">
                        ${item.productName}
                    </td>
                    <td>${item.formattedUnitPrice}</td>
                    <td>${item.quantity}</td>
                    <td>${item.formattedSubtotal}</td>
                </tr>
            </c:forEach>
        </tbody>
        <tfoot>
            <tr>
                <th colspan="3" class="text-right">Tổng Cộng</th>
                <th>${order.formattedTotal}</th>
            </tr>
        </tfoot>
    </table>
</div>

<%@ include file="_footer.jsp" %>
