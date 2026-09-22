package com.estore.model;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Model đại diện cho một sản phẩm hiển thị ở trang chi tiết sản phẩm
 * (giao diện lấy cảm hứng từ trang chi tiết sản phẩm của Shopee).
 */
public class Product implements Serializable {

    private int id;
    private String name;
    private String badge;              // ví dụ: "Yêu Thích+"
    private List<String> breadcrumb = new ArrayList<>();
    private String category;
    private double rating;
    private int reviewCount;
    private int soldCount;
    private double price;              // giá thấp nhất đang bán
    private double maxPrice;           // giá cao nhất trong các phân loại
    private double originalPrice;      // giá gốc thấp nhất (trước giảm)
    private double maxOriginalPrice;   // giá gốc cao nhất
    private int discountPercent;
    private List<String> images = new ArrayList<>();
    private String imageFileName;      // tên file ảnh gốc trong DB (vd: product-1.jpg), dùng cho form admin
    private List<Variant> variants = new ArrayList<>();
    private String shippingInfo;
    private String description;
    private List<String> highlights = new ArrayList<>();

    private int quantity;              // số lượng tồn kho (lấy từ cột `quantity` trong DB)

    private String shopName;
    private String shopAvatar;
    private int shopReviews;
    private String shopResponseRate;
    private String shopJoinDate;
    private int shopProductCount;
    private int shopFollowers;

    public Product() {
    }

    // ----- getters / setters -----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public List<String> getBreadcrumb() {
        return breadcrumb;
    }

    public void setBreadcrumb(List<String> breadcrumb) {
        this.breadcrumb = breadcrumb;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getMaxOriginalPrice() {
        return maxOriginalPrice;
    }

    public void setMaxOriginalPrice(double maxOriginalPrice) {
        this.maxOriginalPrice = maxOriginalPrice;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public List<Variant> getVariants() {
        return variants;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }

    public String getShippingInfo() {
        return shippingInfo;
    }

    public void setShippingInfo(String shippingInfo) {
        this.shippingInfo = shippingInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<String> highlights) {
        this.highlights = highlights;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAvatar() {
        return shopAvatar;
    }

    public void setShopAvatar(String shopAvatar) {
        this.shopAvatar = shopAvatar;
    }

    public int getShopReviews() {
        return shopReviews;
    }

    public void setShopReviews(int shopReviews) {
        this.shopReviews = shopReviews;
    }

    public String getShopResponseRate() {
        return shopResponseRate;
    }

    public void setShopResponseRate(String shopResponseRate) {
        this.shopResponseRate = shopResponseRate;
    }

    public String getShopJoinDate() {
        return shopJoinDate;
    }

    public void setShopJoinDate(String shopJoinDate) {
        this.shopJoinDate = shopJoinDate;
    }

    public int getShopProductCount() {
        return shopProductCount;
    }

    public void setShopProductCount(int shopProductCount) {
        this.shopProductCount = shopProductCount;
    }

    public int getShopFollowers() {
        return shopFollowers;
    }

    public void setShopFollowers(int shopFollowers) {
        this.shopFollowers = shopFollowers;
    }

    // ----- helpers dùng trực tiếp trong JSP -----

    public String getThumbnail() {
        return images.isEmpty() ? "" : images.get(0);
    }

    public boolean isHasPriceRange() {
        return maxPrice > price;
    }

    public String getFormattedPrice() {
        return format(price);
    }

    public String getFormattedMaxPrice() {
        return format(maxPrice);
    }

    public String getFormattedOriginalPrice() {
        return format(originalPrice);
    }

    public String getFormattedMaxOriginalPrice() {
        return format(maxOriginalPrice);
    }

    private String format(double value) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(value) + "\u20ab"; // ₫
    }
}
