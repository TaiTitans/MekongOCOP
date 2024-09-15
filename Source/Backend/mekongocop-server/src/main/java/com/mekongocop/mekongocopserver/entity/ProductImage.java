package com.mekongocop.mekongocopserver.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_image")
public class ProductImage {
    public ProductImage(int image_id, String image_url, Boolean is_primary, Product product) {
        this.image_id = image_id;
        this.image_url = image_url;
        this.is_primary = is_primary;
        this.product = product;
    }

    public ProductImage(String imageUrl, boolean isPrimary) {
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Boolean getIs_primary() {
        return is_primary;
    }

    public void setIs_primary(Boolean is_primary) {
        this.is_primary = is_primary;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int image_id;
    private String image_url;
    private Boolean is_primary;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public ProductImage() {

    }
}
