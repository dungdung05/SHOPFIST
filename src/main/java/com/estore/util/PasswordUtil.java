package com.estore.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Băm mật khẩu bằng SHA-256 trước khi lưu vào CSDL.
 * (Đơn giản, phù hợp cho đồ án học tập. Với hệ thống thực tế nên dùng BCrypt.)
 */
public class PasswordUtil {

    public static String hash(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawPassword.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Lỗi băm mật khẩu", e);
        }
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        return hash(rawPassword).equals(hashedPassword);
    }
}
