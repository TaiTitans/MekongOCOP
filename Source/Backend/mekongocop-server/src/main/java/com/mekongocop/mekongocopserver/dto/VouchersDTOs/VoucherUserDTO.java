package com.mekongocop.mekongocopserver.dto.VouchersDTOs;

import lombok.Data;

@Data
public class VoucherUserDTO {
    private int id;
    private int voucher_id;
    private int user_id;
    private int usageCount;
}
