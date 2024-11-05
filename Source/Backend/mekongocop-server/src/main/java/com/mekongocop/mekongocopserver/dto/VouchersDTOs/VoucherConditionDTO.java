package com.mekongocop.mekongocopserver.dto.VouchersDTOs;

import lombok.Data;

@Data
public class VoucherConditionDTO {
    private int id;
    private int voucherId;
    private String conditionType;
    private int conditionAuth;
}
