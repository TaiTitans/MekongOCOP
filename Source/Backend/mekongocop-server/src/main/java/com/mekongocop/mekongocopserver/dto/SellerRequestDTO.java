package com.mekongocop.mekongocopserver.dto;

import com.mekongocop.mekongocopserver.entity.User;

import java.util.Date;

public class SellerRequestDTO {
    public SellerRequestDTO(int seller_id, int user_id, String certification, String status, Date request_date) {
        this.seller_id = seller_id;
        this.user_id = user_id;
        this.certification = certification;
        this.status = status;
        this.request_date = request_date;
    }

    public int seller_id;
    public int user_id;
    public String certification;
    public String status;
    public Date request_date;
}
