# EStore - Chức năng Đăng ký / Đăng nhập / Hồ sơ / Đăng xuất (Java Web)

Project này bổ sung chức năng **Đăng ký, Đăng nhập, Hồ sơ cá nhân, Đăng xuất**
bằng Java (Servlet + JSP + MySQL) vào giao diện có sẵn của template **EStore**.

## 1. Công nghệ sử dụng
- Java Servlet 4.0 + JSP + JSTL
- MySQL (JDBC)
- Maven (quản lý thư viện, build WAR)
- Giao diện Bootstrap 4 (tái sử dụng từ template gốc)

## 2. Cấu trúc project
```
estore-java/
├── pom.xml
├── database/
│   └── estore_db.sql          <-- script tạo CSDL + bảng users
└── src/main/
    ├── java/com/estore/
    │   ├── model/User.java
    │   ├── dao/UserDAO.java
    │   ├── util/DBConnection.java   <-- SỬA thông tin kết nối MySQL ở đây
    │   ├── util/PasswordUtil.java
    │   ├── filter/AuthFilter.java   <-- chặn /profile nếu chưa đăng nhập
    │   └── servlet/
    │       ├── RegisterServlet.java  (/register)
    │       ├── LoginServlet.java     (/login)
    │       ├── LogoutServlet.java    (/logout)
    │       └── ProfileServlet.java   (/profile)
    └── webapp/
        ├── header.jsp     <-- menu dùng chung, tự đổi Login/Register <-> Hồ sơ/Đăng xuất
        ├── index.jsp      <-- trang chủ (chuyển từ index.html gốc)
        ├── login.jsp
        ├── register.jsp
        ├── profile.jsp
        ├── css/ img/ js/ lib/   <-- tài nguyên gốc của template
        └── WEB-INF/web.xml
```

## 3. Cài đặt & chạy

### Bước 1: Tạo cơ sở dữ liệu
Mở MySQL Workbench / terminal, chạy file `database/estore_db.sql`:
```
mysql -u root -p < database/estore_db.sql
```
File này tự tạo database `estore_db`, bảng `users`, và 1 tài khoản mẫu:
- **Tên đăng nhập:** admin
- **Mật khẩu:** 123456

### Bước 2: Cấu hình kết nối
Mở file `src/main/java/com/estore/util/DBConnection.java`, sửa lại:
```java
private static final String URL = "jdbc:mysql://localhost:3306/estore_db?...";
private static final String USER = "root";
private static final String PASSWORD = "";  // mật khẩu MySQL của bạn
```

### Bước 3: Build & chạy
**Cách 1 - Dùng Maven (nhanh nhất, không cần cài Tomcat riêng):**
```
cd estore-java
mvn tomcat7:run
```
Sau đó mở trình duyệt: http://localhost:8080/

**Cách 2 - Deploy WAR lên Tomcat có sẵn:**
```
mvn clean package
```
File `target/estore-java.war` sinh ra, copy vào thư mục `webapps` của Tomcat rồi khởi động Tomcat. Truy cập:
`http://localhost:8080/estore-java/`

**Cách 3 - Import vào Eclipse/IntelliJ:**
- File > Import > Existing Maven Project > chọn thư mục `estore-java`
- Add project vào server Tomcat (đã cài JDK 11+ và Tomcat 9)
- Run on Server

## 4. Các chức năng đã cài đặt

| Chức năng | URL | Servlet | Ghi chú |
|---|---|---|---|
| Đăng ký | `/register` | `RegisterServlet` | Kiểm tra trùng username/email, validate dữ liệu, băm mật khẩu SHA-256 |
| Đăng nhập | `/login` | `LoginServlet` | Tạo `HttpSession`, lưu đối tượng `User` vào session |
| Hồ sơ | `/profile` | `ProfileServlet` | Xem/cập nhật thông tin, đổi mật khẩu. Được bảo vệ bởi `AuthFilter` |
| Đăng xuất | `/logout` | `LogoutServlet` | Gọi `session.invalidate()` |

Menu "Tài khoản" trên header (`header.jsp`) tự động đổi:
- Chưa đăng nhập → hiện "Đăng nhập" / "Đăng ký"
- Đã đăng nhập → hiện "Xin chào, {Họ tên}" → "Hồ sơ của tôi" / "Đăng xuất"

## 5. Lưu ý mở rộng
- Mật khẩu hiện băm bằng SHA-256 (đơn giản, đủ cho đồ án). Nếu triển khai thực tế nên đổi sang **BCrypt**.
- Các trang `product-list.html`, `cart.html`, `checkout.html`, `contact.html` vẫn giữ nguyên dạng tĩnh (`.html`); nếu muốn menu tài khoản cũng hiển thị động trên các trang đó, hãy đổi đuôi các file này thành `.jsp` và thêm dòng:
  ```jsp
  <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  ```
  ở đầu file, rồi thay khối `<!-- Top bar Start --> ... <!-- Nav Bar End -->` bằng `<%@ include file="header.jsp" %>` giống như đã làm với `index.jsp`.
