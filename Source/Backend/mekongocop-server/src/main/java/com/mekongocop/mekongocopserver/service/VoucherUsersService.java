package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.VouchersDTOs.VoucherUserDTO;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.entity.voucher.Voucher;
import com.mekongocop.mekongocopserver.entity.voucher.VoucherUsers;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.repository.VoucherRepository;
import com.mekongocop.mekongocopserver.repository.VoucherUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoucherUsersService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private VoucherUsersRepository voucherUsersRepository;


    public VoucherUserDTO convertToDTO(VoucherUsers voucherUsers) {
        VoucherUserDTO voucherUserDTO = new VoucherUserDTO();

        voucherUserDTO.setId(voucherUsers.getId());
        voucherUserDTO.setVoucher_id(voucherUsers.getVoucher().getVoucher_id());
        voucherUserDTO.setUser_id(voucherUsers.getUser().getUser_id());
        voucherUserDTO.setUsageCount(voucherUsers.getUsage_count());

        return voucherUserDTO;
    }
    public VoucherUsers convertToEntity(VoucherUserDTO voucherUserDTO, Voucher voucher, User user) {
        VoucherUsers voucherUsers = new VoucherUsers();

        voucherUsers.setId(voucherUserDTO.getId());
        voucherUsers.setVoucher(voucher);
        voucherUsers.setUser(user);
        voucherUsers.setUsage_count(voucherUserDTO.getUsageCount());

        return voucherUsers;
    }





}
