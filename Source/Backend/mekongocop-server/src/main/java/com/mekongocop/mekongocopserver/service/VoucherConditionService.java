package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.VouchersDTOs.VoucherConditionDTO;
import com.mekongocop.mekongocopserver.entity.voucher.Voucher;
import com.mekongocop.mekongocopserver.entity.voucher.VoucherCondition;
import com.mekongocop.mekongocopserver.repository.VoucherConditionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoucherConditionService {
    @Autowired
    private VoucherConditionRepository voucherConditionRepository;
    // Chuyển từ DTO sang Entity
    public VoucherCondition convertToEntity(VoucherConditionDTO dto, Optional<Voucher> voucher) {
        VoucherCondition voucherCondition = new VoucherCondition();

        voucherCondition.setId(dto.getId());
        voucherCondition.setConditionType(dto.getConditionType());
        voucherCondition.setConditionAuth(dto.getConditionAuth());

        // Liên kết Voucher vào nếu có
        voucher.ifPresent(voucherCondition::setVoucher);

        return voucherCondition;
    }

    // Chuyển từ Entity sang DTO
    public VoucherConditionDTO convertToDTO(VoucherCondition entity) {
        VoucherConditionDTO dto = new VoucherConditionDTO();

        dto.setId(entity.getId());
        dto.setVoucherId(entity.getVoucher().getVoucher_id());
        dto.setConditionType(entity.getConditionType());
        dto.setConditionAuth(entity.getConditionAuth());

        return dto;
    }
}
