package com.mekongocop.mekongocopserver.dto.VouchersDTOs;

import com.mekongocop.mekongocopserver.entity.voucher.VoucherCondition;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class VoucherDTO {
    private int voucherId;
    private String code;
    private String type;
    private String discountType;
    private Double discountValue;
    private Double minSpend;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int totalLimit;
    private int userLimit;
    private String status;
    private List<VoucherUserDTO> voucherUser;
//    private List<VoucherConditionDTO> voucherCondition;
}
