package com.mekongocop.mekongocopserver.dto;

public class ProductImageDTO {
    public ProductImageDTO(int imageId, String imageUrl, Boolean isPrimary) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary;
    }
    public ProductImageDTO() {
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }

    public int imageId;
    public String imageUrl;
    public Boolean isPrimary;
}
