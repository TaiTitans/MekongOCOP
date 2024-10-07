package com.mekongocop.mekongocopserver.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class OrderDTO {




    public OrderDTO(){

    }

    public OrderDTO(int order_id, int user_id, BigDecimal total_price, String payment, String status, String address, BigDecimal ship, Timestamp created_at, Timestamp updated_at, List<OrderItemDTO> items) {
        this.order_id = order_id;
        this.user_id = user_id;
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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public BigDecimal getTotal_price() {
        return total_price;
    }

    public void setTotal_price(BigDecimal total_price) {
        this.total_price = total_price;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    private int order_id;
    private int user_id;
    private BigDecimal total_price;
    private String payment;
    private String status;
    private String address;
    private BigDecimal ship;
    private Timestamp created_at;
    private Timestamp updated_at;
    private List<OrderItemDTO> items;
}
