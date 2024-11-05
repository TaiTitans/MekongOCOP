package com.mekongocop.mekongocopserver.util;

import com.mekongocop.mekongocopserver.dto.VouchersDTOs.VoucherDTO;
import com.mekongocop.mekongocopserver.entity.SpecialDay;
import com.mekongocop.mekongocopserver.repository.SpecialDayRepository;
import com.mekongocop.mekongocopserver.repository.VoucherRepository;
import com.mekongocop.mekongocopserver.service.VoucherService;
import com.mekongocop.mekongocopserver.service.notification.NotificationProducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VoucherScheduler {
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private SpecialDayRepository specialDayRepository;
    @Autowired
    private NotificationProducer notificationProducer;
    @Async
    @Scheduled(cron = "0 0 0 * * *") // Chạy hàng ngày lúc 0h
    public void createSpecialDayVouchers() {
        LocalDate today = LocalDate.now();

        // Truy vấn danh sách ngày đặc biệt từ cơ sở dữ liệu
        List<LocalDate> specialDays = specialDayRepository.findAll().stream()
                .map(SpecialDay::getSpecial_day)
                .collect(Collectors.toList());


        if (specialDays.contains(today)) {
            VoucherDTO voucherDTO = new VoucherDTO();
            voucherDTO.setCode("SPECIAL_DAY_OFFER");
            voucherDTO.setType("System");
            voucherDTO.setDiscountType("Percentage");
            voucherDTO.setDiscountValue(20.0);
            voucherDTO.setMinSpend(100.000);
            voucherDTO.setStartDate(LocalDateTime.now());
            voucherDTO.setEndDate(LocalDateTime.now().plusDays(1));
            voucherDTO.setTotalLimit(500);
            voucherDTO.setUserLimit(1);
            voucherDTO.setStatus("Active");

            voucherService.createVoucher(voucherDTO);
            String notificationMessage = "Ngày đặc biệt sử dụng ngay voucher SPECIAL_DAY_OFFER giảm đến 20% giá trị đơn hàng.";
            notificationProducer.sendNotification(notificationMessage);
        }
    }

}
