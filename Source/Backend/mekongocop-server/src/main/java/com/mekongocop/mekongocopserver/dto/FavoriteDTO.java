package com.mekongocop.mekongocopserver.dto;

public class FavoriteDTO {
    public FavoriteDTO(int favoriteId, int productId, int userId) {
        this.favoriteId = favoriteId;
        this.productId = productId;
        this.userId = userId;
    }

    public int getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private int favoriteId;
    private int productId;
    private int userId;
}
