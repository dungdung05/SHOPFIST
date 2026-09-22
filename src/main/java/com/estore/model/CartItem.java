package com.estore.model;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Một dòng sản phẩm trong giỏ hàng (lưu trong session).
 */
public class CartItem implements Serializable {

    private int productId;
    private String name;
    private String image;
    private String variantName;   // có thể rỗng nếu sản phẩm không có phân loại
    private double unitPrice;
    private int quantity;

    public CartItem(int productId, String name, String image, String variantName, double unitPrice, int quantity) {
        this.productId = productId;
        this.name = name;
        this.image = image;
        this.variantName = variantName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /**
     * Khoá duy nhất để gộp số lượng khi thêm cùng 1 sản phẩm + cùng phân loại vào giỏ nhiều lần.
     */
    public String getItemKey() {
        return productId + "|" + (variantName == null ? "" : variantName);
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getVariantName() {
        return variantName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(int delta) {
        this.quantity += delta;
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
        return nf.format(value) + "\u20ab"; // ₫
    }
}
