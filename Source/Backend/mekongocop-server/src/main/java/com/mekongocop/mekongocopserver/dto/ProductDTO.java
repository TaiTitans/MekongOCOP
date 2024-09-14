package com.mekongocop.mekongocopserver.dto;

import java.util.List;

public class ProductDTO {


    public ProductDTO(int productId, String productName, String productDescription, int productPrice, int productQuantity, int category, int province, List<ProductImageDTO> productImages, List<ReviewDTO> reviews) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.category = category;
        this.province = province;
        this.productImages = productImages;
        this.reviews = reviews;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public List<ProductImageDTO> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImageDTO> productImages) {
        this.productImages = productImages;
    }

    public List<ReviewDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDTO> reviews) {
        this.reviews = reviews;
    }

    private int productId;
    private String productName;
    private String productDescription;
    private int productPrice;
    private int productQuantity;
    private int category;
    private int province;
    private List<ProductImageDTO> productImages;
    private List<ReviewDTO> reviews;

    public ProductDTO() {

    }
}
