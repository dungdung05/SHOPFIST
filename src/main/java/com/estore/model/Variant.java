package com.estore.model;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Một lựa chọn (phân loại) của sản phẩm, ví dụ: "Hộp 300gr", "Màu Đen", "Size M"...
 * Mỗi lựa chọn có thể có giá và giá gốc riêng.
 */
public class Variant implements Serializable {

    private String name;
    private double price;
    private double originalPrice;

    public Variant(String name, double price, double originalPrice) {
        this.name = name;
        this.price = price;
        this.originalPrice = originalPrice;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public String getFormattedPrice() {
        return format(price);
    }

    public String getFormattedOriginalPrice() {
        return format(originalPrice);
    }

    private String format(double value) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(value) + "\u20ab"; // ₫
    }
}
