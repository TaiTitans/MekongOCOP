package com.mekongocop.mekongocopserver.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {


    public Order(int order_id, User user, BigDecimal total_price, String payment, String status, String address, BigDecimal ship, Date created_at, Date updated_at, List<OrderItem> items) {
        this.order_id = order_id;
        this.user = user;
        this.total_price = total_price;
        this.payment = payment;
        this.status = status;
        this.address = address;
        this.ship = ship;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.items = items;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public @Pattern(regexp = "VNPay|Cash", message = "Status must be VNPay, Cash") String getPayment() {
        return payment;
    }

    public void setPayment(@Pattern(regexp = "VNPay|Cash", message = "Status must be VNPay, Cash") String payment) {
        this.payment = payment;
    }

    public BigDecimal getTotal_price() {
        return total_price;
    }

    public void setTotal_price(BigDecimal total_price) {
        this.total_price = total_price;
    }

    public @Pattern(regexp = "Request|Pending|Success", message = "Status must be Request, Cancel, or Success") String getStatus() {
        return status;
    }

    public void setStatus(@Pattern(regexp = "Request|Pending|Success", message = "Status must be Request, Cancel, or Success") String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getShip() {
        return ship;
    }

    public void setShip(BigDecimal ship) {
        this.ship = ship;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int order_id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private BigDecimal total_price;
    @Pattern(regexp = "VNPay|Cash", message = "Status must be VNPay, Cash")
    private String payment;
    @Pattern(regexp = "Request|Pending|Success|Cancel|Cancel_Request", message = "Status must be Request, Cancel, or Success")
    private String status;

    private String address;
    private BigDecimal ship;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created_at;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updated_at;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    public Order() {

    }
}
