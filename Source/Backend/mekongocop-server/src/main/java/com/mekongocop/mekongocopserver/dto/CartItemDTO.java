package com.mekongocop.mekongocopserver.dto;

import java.math.BigDecimal;

public class CartItemDTO {

    public CartItemDTO() {
    }


    public CartItemDTO(int productId, int quantity, String productName, BigDecimal price) {
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    private int productId;
    private int quantity;
    private String productName;
    private BigDecimal price;
}
