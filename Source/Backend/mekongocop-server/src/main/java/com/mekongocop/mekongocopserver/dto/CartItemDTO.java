package com.mekongocop.mekongocopserver.dto;

public class CartItemDTO {

    public CartItemDTO() {
    }


    public CartItemDTO(int productId, int quantity, String productName, int price) {
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    private int productId;
    private int quantity;
    private String productName;
    private int price;
}
