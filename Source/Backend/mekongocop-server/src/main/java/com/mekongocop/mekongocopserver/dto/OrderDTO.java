package com.mekongocop.mekongocopserver.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
@Data
public class OrderDTO {



    private int order_id;
    private int user_id;
    private BigDecimal total_price;
    private String payment;
    private String status;
    private String address;
    private BigDecimal ship;
    private Timestamp created_at;
    private Timestamp updated_at;
    private String qr_code_url;
    private List<OrderItemDTO> items;
}
