<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <title>Hồ sơ của tôi - E Store</title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
    <style>
        .profile-card {
            max-width: 750px;
            margin: 50px auto;
            background: #fff;
            border: 1px solid #eee;
            border-radius: 4px;
            box-shadow: 0 0 15px rgba(0,0,0,.05);
        }
        .profile-header {
            background: #343a40;
            color: #fff;
            padding: 25px 35px;
            border-radius: 4px 4px 0 0;
        }
        .profile-body { padding: 30px 35px; }
        .btn-primary { background-color: #4e8bff; border-color: #4e8bff; }
        .nav-tabs .nav-link.active { font-weight: 700; }
    </style>
</head>
<body>
    <%@ include file="header.jsp" %>

    <div class="container">
        <div class="profile-card">
            <div class="profile-header">
                <h4><i class="fa fa-user-circle"></i> Hồ sơ của tôi</h4>
                <small>Xin chào, ${sessionScope.user.fullName} (@${sessionScope.user.username})</small>
            </div>
            <div class="profile-body">

                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <ul class="nav nav-tabs" id="profileTab" role="tablist">
                    <li class="nav-item">
                        <a class="nav-link ${empty activeTab || activeTab == 'info' ? 'active' : ''}" data-toggle="tab" href="#info">Thông tin cá nhân</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${activeTab == 'password' ? 'active' : ''}" data-toggle="tab" href="#password">Đổi mật khẩu</a>
                    </li>
                </ul>

                <div class="tab-content pt-4">
                    <!-- Tab thông tin -->
                    <div class="tab-pane fade ${empty activeTab || activeTab == 'info' ? 'show active' : ''}" id="info">
                        <form action="${pageContext.request.contextPath}/profile" method="post">
                            <input type="hidden" name="action" value="updateInfo">
                            <div class="form-group">
                                <label>Tên đăng nhập</label>
                                <input type="text" class="form-control" value="${sessionScope.user.username}" disabled>
                            </div>
                            <div class="form-group">
                                <label>Họ và tên *</label>
                                <input type="text" name="fullName" class="form-control"
                                       value="${sessionScope.user.fullName}" required>
                            </div>
                            <div class="form-group">
                                <label>Email *</label>
                                <input type="email" name="email" class="form-control"
                                       value="${sessionScope.user.email}" required>
                            </div>
                            <div class="form-group">
                                <label>Số điện thoại</label>
                                <input type="text" name="phone" class="form-control"
                                       value="${sessionScope.user.phone}">
                            </div>
                            <div class="form-group">
                                <label>Địa chỉ</label>
                                <textarea name="address" class="form-control" rows="2">${sessionScope.user.address}</textarea>
                            </div>
                            <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                        </form>
                    </div>

                    <!-- Tab đổi mật khẩu -->
                    <div class="tab-pane fade ${activeTab == 'password' ? 'show active' : ''}" id="password">
                        <form action="${pageContext.request.contextPath}/profile" method="post">
                            <input type="hidden" name="action" value="changePassword">
                            <div class="form-group">
                                <label>Mật khẩu hiện tại *</label>
                                <input type="password" name="currentPassword" class="form-control" required>
                            </div>
                            <div class="form-group">
                                <label>Mật khẩu mới * (≥ 6 ký tự)</label>
                                <input type="password" name="newPassword" class="form-control" required>
                            </div>
                            <div class="form-group">
                                <label>Xác nhận mật khẩu mới *</label>
                                <input type="password" name="confirmNewPassword" class="form-control" required>
                            </div>
                            <button type="submit" class="btn btn-primary">Đổi mật khẩu</button>
                        </form>
                    </div>
                </div>

                <hr>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger">
                    <i class="fa fa-sign-out-alt"></i> Đăng xuất
                </a>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
</body>
</html>
