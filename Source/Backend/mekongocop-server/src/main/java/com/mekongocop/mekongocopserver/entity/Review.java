package com.mekongocop.mekongocopserver.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "review")
public class Review {
    public Review(int review_id, int rating, Date created_at, String comment, Product product_id, User user_id) {
        this.review_id = review_id;
        this.rating = rating;
        this.created_at = created_at;
        this.comment = comment;
        this.product_id = product_id;
        this.user_id = user_id;
    }

    public int getReview_id() {
        return review_id;
    }

    public void setReview_id(int review_id) {
        this.review_id = review_id;
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

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Product getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Product product_id) {
        this.product_id = product_id;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int review_id;
    private int rating;
    private String comment;
    private Date created_at;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product_id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user_id;

    public Review() {

    }
}
