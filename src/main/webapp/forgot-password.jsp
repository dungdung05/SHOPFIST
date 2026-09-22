<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <title>Quên Mật Khẩu - E Store</title>
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
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <c:choose>
                <%-- BƯỚC 1: xác minh Tên đăng nhập + Email --%>
                <c:when test="${step != 'reset'}">
                    <h3><i class="fa fa-key"></i> Quên Mật Khẩu</h3>
                    <p class="text-muted">Nhập Tên đăng nhập và Email đã đăng ký để xác minh tài khoản.</p>

                    <form action="${pageContext.request.contextPath}/forgot-password" method="post">
                        <input type="hidden" name="action" value="verify">
                        <div class="form-group">
                            <label>Tên đăng nhập</label>
                            <input type="text" name="username" class="form-control" value="${username}" required autofocus>
                        </div>
                        <div class="form-group">
                            <label>Email</label>
                            <input type="email" name="email" class="form-control" value="${email}" required>
                        </div>
                        <button type="submit" class="btn btn-primary btn-block">Xác Minh</button>
                    </form>
                </c:when>

                <%-- BƯỚC 2: đặt mật khẩu mới --%>
                <c:otherwise>
                    <h3><i class="fa fa-lock"></i> Đặt Mật Khẩu Mới</h3>
                    <p class="text-muted">Xác minh thành công. Nhập mật khẩu mới cho tài khoản của bạn.</p>

                    <form action="${pageContext.request.contextPath}/forgot-password" method="post">
                        <input type="hidden" name="action" value="reset">
                        <div class="form-group">
                            <label>Mật khẩu mới</label>
                            <input type="password" name="newPassword" class="form-control" required minlength="6" autofocus>
                        </div>
                        <div class="form-group">
                            <label>Xác nhận mật khẩu mới</label>
                            <input type="password" name="confirmNewPassword" class="form-control" required minlength="6">
                        </div>
                        <button type="submit" class="btn btn-primary btn-block">Đặt Lại Mật Khẩu</button>
                    </form>
                </c:otherwise>
            </c:choose>

            <p class="mt-3 text-center">
                <a href="${pageContext.request.contextPath}/login">Quay lại Đăng nhập</a>
            </p>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
</body>
</html>
