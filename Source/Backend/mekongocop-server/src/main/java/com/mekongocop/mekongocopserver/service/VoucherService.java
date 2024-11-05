package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.VouchersDTOs.VoucherConditionDTO;
import com.mekongocop.mekongocopserver.dto.VouchersDTOs.VoucherDTO;
import com.mekongocop.mekongocopserver.dto.VouchersDTOs.VoucherUserDTO;
import com.mekongocop.mekongocopserver.entity.voucher.Voucher;
import com.mekongocop.mekongocopserver.entity.voucher.VoucherCondition;
import com.mekongocop.mekongocopserver.entity.voucher.VoucherUsers;
import com.mekongocop.mekongocopserver.repository.VoucherConditionRepository;
import com.mekongocop.mekongocopserver.repository.VoucherRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VoucherService {
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private VoucherConditionRepository voucherConditionRepository;

    public VoucherDTO convertToDTO(Voucher voucher) {
        VoucherDTO voucherDTO = new VoucherDTO();

        voucherDTO.setVoucherId(voucher.getVoucher_id());
        voucherDTO.setCode(voucher.getCode());
        voucherDTO.setType(voucher.getType());
        voucherDTO.setDiscountType(voucher.getDiscount_type());
        voucherDTO.setDiscountValue(voucher.getDiscount_value());
        voucherDTO.setMinSpend(voucher.getMind_spend());
        voucherDTO.setStartDate(voucher.getStart_date());
        voucherDTO.setEndDate(voucher.getEnd_date());
        voucherDTO.setTotalLimit(voucher.getTotal_limit());
        voucherDTO.setUserLimit(voucher.getUser_limit());
        voucherDTO.setStatus(voucher.getStatus());

        // Chuyển đổi Set<VoucherUsers> thành List<VoucherUserDTO> và xử lý null
        List<VoucherUserDTO> voucherUserDTOs = (voucher.getVoucherUsers() != null) ?
                voucher.getVoucherUsers().stream()
                        .map(voucherUser -> {
                            VoucherUserDTO voucherUserDTO = new VoucherUserDTO();
                            voucherUserDTO.setId(voucherUser.getId());
                            voucherUserDTO.setVoucher_id(voucher.getVoucher_id()); // Lấy voucher_id trực tiếp từ Voucher
                            voucherUserDTO.setUser_id(voucherUser.getUser().getUser_id());
                            voucherUserDTO.setUsageCount(voucherUser.getUsage_count());
                            return voucherUserDTO;
                        })
                        .collect(Collectors.toList()) : Collections.emptyList();
        voucherDTO.setVoucherUser(voucherUserDTOs);
        return voucherDTO;
    }

    public Voucher convertToEntity(VoucherDTO voucherDTO) {
        Voucher voucher = new Voucher();

        voucher.setVoucher_id(voucherDTO.getVoucherId());
        voucher.setCode(voucherDTO.getCode());
        voucher.setType(voucherDTO.getType());
        voucher.setDiscount_type(voucherDTO.getDiscountType());
        voucher.setDiscount_value(voucherDTO.getDiscountValue());
        voucher.setMind_spend(voucherDTO.getMinSpend());
        voucher.setStart_date(voucherDTO.getStartDate());
        voucher.setEnd_date(voucherDTO.getEndDate());
        voucher.setTotal_limit(voucherDTO.getTotalLimit());
        voucher.setUser_limit(voucherDTO.getUserLimit());
        voucher.setStatus(voucherDTO.getStatus());

        // Chuyển đổi List<VoucherUserDTO> thành Set<VoucherUsers> và xử lý null
        Set<VoucherUsers> voucherUsers = (voucherDTO.getVoucherUser() != null) ?
                voucherDTO.getVoucherUser().stream()
                        .map(voucherUserDTO -> {
                            VoucherUsers voucherUser = new VoucherUsers();
                            voucherUser.setId(voucherUserDTO.getId());
                            voucherUser.setUsage_count(voucherUserDTO.getUsageCount());
                            return voucherUser;
                        })
                        .collect(Collectors.toSet()) : Collections.emptySet();
        voucher.setVoucherUsers(voucherUsers);

        return voucher;
    }

    @Transactional
    public VoucherDTO createVoucher(VoucherDTO voucherDTO) {
        try{
            Voucher voucher = convertToEntity(voucherDTO);
            validateVoucher(voucher);
            Voucher savedVoucher = voucherRepository.save(voucher);
            return convertToDTO(savedVoucher);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    private void validateVoucher(Voucher voucher){
        if(voucher.getStart_date().isAfter(voucher.getEnd_date())){
            throw new IllegalArgumentException("Voucher start date is after end date");
        }
        if(voucher.getDiscount_value()<0){
            throw new IllegalArgumentException("Voucher discount value is negative");
        }
    }

    public List<VoucherDTO> getAllVouchers() {
        return voucherRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteVoucher(int voucherId) {
        if (voucherRepository.existsById(voucherId)) {
            voucherRepository.deleteById(voucherId);
        } else {
            throw new NoSuchElementException("Voucher not found with ID: " + voucherId);
        }
    }

}
