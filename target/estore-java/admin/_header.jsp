<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <title>${empty pageTitle ? 'E Store Admin' : pageTitle} - E Store Admin</title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background: #f4f6f9; margin: 0; }
        .admin-topbar {
            background: #1f2937; color: #fff; padding: 12px 24px;
            display: flex; justify-content: space-between; align-items: center;
            position: sticky; top: 0; z-index: 10;
        }
        .admin-topbar a { color: #fff; text-decoration: none; }
        .admin-topbar a:hover { color: #ffc107; }
        .admin-layout { display: flex; min-height: calc(100vh - 50px); }
        .admin-sidebar {
            width: 220px; background: #111827; color: #cbd5e1; flex-shrink: 0;
            padding: 18px 0;
        }
        .admin-sidebar a {
            display: block; padding: 12px 22px; color: #cbd5e1; text-decoration: none;
            border-left: 3px solid transparent;
        }
        .admin-sidebar a:hover { background: #1f2937; color: #fff; }
        .admin-sidebar a.active {
            background: #1f2937; color: #ffc107; border-left-color: #ffc107; font-weight: 600;
        }
        .admin-content { flex: 1; padding: 26px; }
        .admin-card {
            background: #fff; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,.08); padding: 22px;
        }
        .admin-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
        table img { width: 50px; height: 50px; object-fit: cover; border-radius: 4px; }
        .stat-box {
            background: #fff; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,.08);
            padding: 20px; text-align: center;
        }
        .stat-box .stat-number { font-size: 30px; font-weight: 700; color: #1f2937; }
        .stat-box .stat-label { color: #6b7280; margin-top: 4px; }
    </style>
</head>
<body>

<div class="admin-topbar">
    <div><i class="fa fa-store"></i> E Store <b>Admin</b></div>
    <div>
        Xin chào, ${sessionScope.user.fullName}
        &nbsp;|&nbsp;
        <a href="${pageContext.request.contextPath}/index.jsp"><i class="fa fa-home"></i> Về trang chủ</a>
        &nbsp;|&nbsp;
        <a href="${pageContext.request.contextPath}/logout"><i class="fa fa-sign-out-alt"></i> Đăng xuất</a>
    </div>
</div>

<div class="admin-layout">
    <div class="admin-sidebar">
        <a href="${pageContext.request.contextPath}/admin" class="${activeMenu eq 'dashboard' ? 'active' : ''}">
            <i class="fa fa-chart-line fa-fw"></i> Tổng Quan
        </a>
        <a href="${pageContext.request.contextPath}/admin/products" class="${activeMenu eq 'products' ? 'active' : ''}">
            <i class="fa fa-box fa-fw"></i> Sản Phẩm
        </a>
        <a href="${pageContext.request.contextPath}/admin/orders" class="${activeMenu eq 'orders' ? 'active' : ''}">
            <i class="fa fa-receipt fa-fw"></i> Đơn Hàng
        </a>
        <a href="${pageContext.request.contextPath}/admin/users" class="${activeMenu eq 'users' ? 'active' : ''}">
            <i class="fa fa-users fa-fw"></i> Người Dùng
        </a>
    </div>

    <div class="admin-content">
