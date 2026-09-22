<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <title>Đăng ký - E Store</title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
    <style>
        .auth-wrapper {
            max-width: 600px;
            margin: 60px auto;
            padding: 35px;
            background: #fff;
            border: 1px solid #eee;
            border-radius: 4px;
            box-shadow: 0 0 15px rgba(0,0,0,.05);
        }
        .auth-wrapper h3 { margin-bottom: 25px; font-weight: 700; }
        .btn-primary { background-color: #4e8bff; border-color: #4e8bff; }
    </style>
</head>
<body>
    <%@ include file="header.jsp" %>

    <div class="container">
        <div class="auth-wrapper">
            <h3><i class="fa fa-user-plus"></i> Tạo tài khoản mới</h3>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="post">
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label>Tên đăng nhập *</label>
                        <input type="text" name="username" class="form-control" value="${username}" required>
                    </div>
                    <div class="form-group col-md-6">
                        <label>Họ và tên *</label>
                        <input type="text" name="fullName" class="form-control" value="${fullName}" required>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label>Mật khẩu * (≥ 6 ký tự)</label>
                        <input type="password" name="password" class="form-control" required>
                    </div>
                    <div class="form-group col-md-6">
                        <label>Xác nhận mật khẩu *</label>
                        <input type="password" name="confirmPassword" class="form-control" required>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label>Email *</label>
                        <input type="email" name="email" class="form-control" value="${email}" required>
                    </div>
                    <div class="form-group col-md-6">
                        <label>Số điện thoại</label>
                        <input type="text" name="phone" class="form-control" value="${phone}">
                    </div>
                </div>
                <div class="form-group">
                    <label>Địa chỉ</label>
                    <textarea name="address" class="form-control" rows="2">${address}</textarea>
                </div>
                <button type="submit" class="btn btn-primary btn-block">Đăng ký</button>
            </form>

            <p class="mt-3 text-center">
                Đã có tài khoản?
                <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
            </p>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
</body>
</html>
