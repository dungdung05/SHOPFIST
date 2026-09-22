package com.estore.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lớp tiện ích để lấy kết nối tới cơ sở dữ liệu MySQL.
 * Chỉnh lại URL, USER, PASSWORD cho đúng với môi trường của bạn.
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/estore_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // đổi thành mật khẩu MySQL của bạn

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không tìm thấy MySQL JDBC Driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
