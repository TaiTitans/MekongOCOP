package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.dto.VouchersDTOs.VoucherDTO;
import com.mekongocop.mekongocopserver.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1")
public class VoucherController {
    @Autowired
    private VoucherService voucherService;

    @PostMapping("/admin/voucher")
    public ResponseEntity<?> createVoucher(@RequestBody VoucherDTO voucherDTO){
        try{
            VoucherDTO createdVoucher = voucherService.createVoucher(voucherDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVoucher);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/admin/voucher")
    public ResponseEntity<List<VoucherDTO>> getAllVouchers() {
        List<VoucherDTO> vouchers = voucherService.getAllVouchers();
        return ResponseEntity.ok(vouchers);
    }

    // Endpoint để xóa voucher theo ID
    @DeleteMapping("/admin/voucher/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable int id) {
        try {
            voucherService.deleteVoucher(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
