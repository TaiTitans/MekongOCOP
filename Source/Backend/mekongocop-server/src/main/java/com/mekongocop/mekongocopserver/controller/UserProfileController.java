package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.common.StatusResponse;
import com.mekongocop.mekongocopserver.dto.UserProfileDTO;
import com.mekongocop.mekongocopserver.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/user")
public class UserProfileController {
    @Autowired
    private UserProfileService userProfileService;

    @PostMapping("/profile")
    public CompletableFuture<ResponseEntity<StatusResponse<UserProfileDTO>>> addUserProfile(
            @RequestPart("dto") String dto,
            @RequestPart("file") MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserProfileDTO userProfileDTO = userProfileService.convertJsonToDTO(dto);
                userProfileService.addProfile(userProfileDTO, file);
                return ResponseEntity.ok(new StatusResponse<>("Success", "Add profile successfully", userProfileDTO));
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Failed to add profile", null));
            }
        });
    }




}
