package com.mekongocop.mekongocopserver.dto;

public class ProductImageDTO {
    public ProductImageDTO(int imageId, String imageUrl, Boolean isPrimary) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary;
    }

    public int imageId;
    public String imageUrl;
    public Boolean isPrimary;
}
