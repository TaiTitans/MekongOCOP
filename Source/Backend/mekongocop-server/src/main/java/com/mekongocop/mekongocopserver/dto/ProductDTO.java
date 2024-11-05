package com.mekongocop.mekongocopserver.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductDTO {


    public ProductDTO(int productId, String productName, String productDescription, BigDecimal productPrice, BigDecimal originalPrice, int productQuantity, int categoryId, String categoryName, int provinceId, String provinceName, List<ProductImageDTO> productImages, List<ReviewDTO> reviews, int store) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.originalPrice = originalPrice;
        this.productQuantity = productQuantity;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.provinceId = provinceId;
        this.provinceName = provinceName;
        this.productImages = productImages;
        this.reviews = reviews;
        this.store = store;
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

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
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

    public int getStore() {
        return store;
    }

    public void setStore(int store) {
        this.store = store;
    }

    private int productId;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private BigDecimal originalPrice;
    private int productQuantity;
    private int categoryId;
    private String categoryName;
    private int provinceId;
    private String provinceName;
    private List<ProductImageDTO> productImages;
    private List<ReviewDTO> reviews;
    private int store;
    public ProductDTO() {

    }
}
