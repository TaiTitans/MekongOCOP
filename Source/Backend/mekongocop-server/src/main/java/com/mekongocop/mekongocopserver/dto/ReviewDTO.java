package com.mekongocop.mekongocopserver.dto;

import com.mekongocop.mekongocopserver.entity.User;

import java.util.Date;

public class ReviewDTO {


    public ReviewDTO(int reviewId, int rating, String reviewContent, Date createdAt, int user_id, String userName, int productId) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.reviewContent = reviewContent;
        this.createdAt = createdAt;
        this.user_id = user_id;
        this.userName = userName;
        this.productId = productId;
    }

    public ReviewDTO() {

    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    private int reviewId;
    private int rating;
    private String reviewContent;
    private Date createdAt;
    private int user_id;
    private String userName;
    private int productId;
}
