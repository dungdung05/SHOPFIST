package com.estore.model;

import java.io.Serializable;
import java.sql.Timestamp;

/** 1 đánh giá/bình luận của 1 khách hàng cho 1 sản phẩm. */
public class Review implements Serializable {

    private int id;
    private int productId;
    private int userId;
    private String reviewerName; // họ tên người đánh giá, lấy kèm qua JOIN sang bảng users
    private int rating;          // 1 - 5 sao
    private String comment;
    private Timestamp createdAt;

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
