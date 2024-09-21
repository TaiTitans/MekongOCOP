package com.mekongocop.mekongocopserver.dto;

import java.util.Date;
import java.util.List;

public class OrderDTO {


    public OrderDTO(int order_id, int user_id, String payment, int total_price, String status, Date created_at, Date updated_at, List<OrderItemDTO> items) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.payment = payment;
        this.total_price = total_price;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.items = items;
    }

    public OrderDTO() {

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

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
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

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    private int order_id;
    private int user_id;
    private int total_price;
    private String payment;
    private String status;
    private Date created_at;
    private Date updated_at;
    private List<OrderItemDTO> items;
}
