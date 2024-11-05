package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.entity.SpecialDay;
import com.mekongocop.mekongocopserver.repository.SpecialDayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1")
public class SpecialDayController {

   @Autowired
    private SpecialDayRepository specialDayRepository;

    // Thêm ngày đặc biệt
    @PostMapping("/admin/specialday")
    public ResponseEntity<SpecialDay> addSpecialDay(@RequestBody LocalDate specialDay) {
        SpecialDay addspecialDay = new SpecialDay();
        addspecialDay.setSpecial_day(specialDay);
        SpecialDay savedSpecialDay = specialDayRepository.save(addspecialDay);
        return ResponseEntity.ok(savedSpecialDay);
    }


    // Xóa ngày đặc biệt
    @DeleteMapping("/admin/specialday/{id}")
    public ResponseEntity<Void> deleteSpecialDay(@PathVariable int id) {
        if (!specialDayRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        specialDayRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Lấy tất cả các ngày đặc biệt
    @GetMapping("/admin/specialday")
    public ResponseEntity<List<SpecialDay>> getAllSpecialDays() {
        List<SpecialDay> specialDays = specialDayRepository.findAll();
        return ResponseEntity.ok(specialDays);
    }


}
