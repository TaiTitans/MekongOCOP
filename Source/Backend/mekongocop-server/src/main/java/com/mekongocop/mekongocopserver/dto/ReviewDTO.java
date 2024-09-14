package com.mekongocop.mekongocopserver.dto;

import com.mekongocop.mekongocopserver.entity.User;

import java.util.Date;

public class ReviewDTO {
    public ReviewDTO(int reviewId, int rating, String reviewContent, String createdAt, String userName) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.reviewContent = reviewContent;
        this.createdAt = createdAt;
        this.userName = userName;
    }

    public int reviewId;
    public int rating;
    public String reviewContent;
    public String createdAt;
    public String userName;

    public ReviewDTO(int reviewId, int rating, String comment, Date createdAt, User userId) {
    }
}
