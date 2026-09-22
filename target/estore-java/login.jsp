<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <title>Đăng nhập - E Store</title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
    <style>
        .auth-wrapper {
            max-width: 450px;
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
            <h3><i class="fa fa-sign-in-alt"></i> Đăng nhập</h3>

            <c:if test="${not empty sessionScope.registerSuccess}">
                <div class="alert alert-success">Đăng ký thành công! Vui lòng đăng nhập.</div>
                <% session.removeAttribute("registerSuccess"); %>
            </c:if>

            <c:if test="${param.passwordChanged == '1'}">
                <div class="alert alert-success">Đổi mật khẩu thành công! Vui lòng đăng nhập lại bằng mật khẩu mới.</div>
            </c:if>

            <c:if test="${param.locked == '1'}">
                <div class="alert alert-danger">Tài khoản của bạn đã bị khoá. Vui lòng liên hệ quản trị viên.</div>
            </c:if>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <input type="hidden" name="redirect" value="${param.redirect}">
                <div class="form-group">
                    <label>Tên đăng nhập</label>
                    <input type="text" name="username" class="form-control" value="${username}" required autofocus>
                </div>
                <div class="form-group">
                    <label>Mật khẩu</label>
                    <input type="password" name="password" class="form-control" required>
                </div>
                <p class="text-right mb-3">
                    <a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a>
                </p>
                <button type="submit" class="btn btn-primary btn-block">Đăng nhập</button>
            </form>

            <p class="mt-3 text-center">
                Chưa có tài khoản?
                <a href="${pageContext.request.contextPath}/register">Đăng ký ngay</a>
            </p>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
</body>
</html>
