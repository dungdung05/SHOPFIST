package com.estore.model;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

/** 1 dòng sản phẩm trong 1 đơn hàng — lưu lại tên/giá tại thời điểm đặt hàng. */
public class OrderItem implements Serializable {

    private int id;
    private int productId;
    private String productName;
    private String productImage;
    private double unitPrice;
    private int quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    public String getFormattedUnitPrice() {
        return format(unitPrice);
    }

    public String getFormattedSubtotal() {
        return format(getSubtotal());
    }

    private String format(double value) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(value) + "\u20ab";
    }
}
