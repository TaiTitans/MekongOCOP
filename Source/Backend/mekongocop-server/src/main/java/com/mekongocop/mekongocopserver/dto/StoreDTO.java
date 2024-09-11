package com.mekongocop.mekongocopserver.dto;

import java.util.Date;

public class StoreDTO {
    public StoreDTO(int store_id, String store_name, String store_address, String store_logo, String store_banner, String store_description, int user_id, Date created_at, Date updated_at, String status) {
        this.store_id = store_id;
        this.store_name = store_name;
        this.store_address = store_address;
        this.store_logo = store_logo;
        this.store_banner = store_banner;
        this.store_description = store_description;
        this.user_id = user_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.status = status;
    }
    public StoreDTO() {
    }
    public int store_id;
    public String store_name;
    public String store_address;
    public String store_logo;
    public String store_banner;
    public String store_description;
    public int user_id;
    public Date created_at;
    public Date updated_at;
    public String status;

    public StoreDTO(int storeId, Integer userId, String storeName, String storeDescription, String storeAddress, String storeLogo, String storeBanner, String status, Date createdAt, Date updatedAt) {
    }
}
