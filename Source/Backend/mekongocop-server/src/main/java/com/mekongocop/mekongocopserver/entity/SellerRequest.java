package com.mekongocop.mekongocopserver.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

import java.util.Date;

@Entity
@Table(name="seller_request")
public class SellerRequest {
    public SellerRequest(int request_id, User user_id, String certification, String status, Date request_date) {
        this.request_id = request_id;
        this.user_id = user_id;
        this.certification = certification;
        this.status = status;
        this.request_date = request_date;
    }

    public int getRequest_id() {
        return request_id;
    }

    public void setRequest_id(int request_id) {
        this.request_id = request_id;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public @Pattern(regexp = "Pending|Accept|Reject", message = "Status must be Pending, Accept, or Reject") String getStatus() {
        return status;
    }

    public void setStatus(@Pattern(regexp = "Pending|Accept|Reject", message = "Status must be Pending, Accept, or Reject") String status) {
        this.status = status;
    }

    public Date getRequest_date() {
        return request_date;
    }

    public void setRequest_date(Date request_date) {
        this.request_date = request_date;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int request_id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user_id;
    private String certification;
    @Pattern(regexp = "Pending|Accept|Reject", message = "Status must be Pending, Accept, or Reject")
    private String status;
    private Date request_date;

    public SellerRequest() {

    }
}
