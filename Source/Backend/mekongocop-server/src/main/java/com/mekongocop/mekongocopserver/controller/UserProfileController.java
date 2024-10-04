package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.common.StatusResponse;
import com.mekongocop.mekongocopserver.dto.UserProfileDTO;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.entity.UserProfile;
import com.mekongocop.mekongocopserver.repository.UserProfileRepository;
import com.mekongocop.mekongocopserver.service.UserProfileService;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/user")
public class UserProfileController {
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserProfileRepository userProfileRepository;

    @PostMapping("/profile")
    public CompletableFuture<ResponseEntity<StatusResponse<UserProfileDTO>>> addUserProfile(
            @RequestPart("dto") String dto,
            @RequestPart("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String validToken = TokenExtractor.extractToken(token);
                if(!jwtTokenProvider.validateToken(token)){
                    return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token error", null));
                }
                UserProfileDTO userProfileDTO = userProfileService.convertJsonToDTO(dto);
                userProfileService.addProfile(userProfileDTO, file, validToken);
                return ResponseEntity.ok(new StatusResponse<>("Success", "Add profile successfully", userProfileDTO));
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Failed to add profile", null));
            }
        });
    }

    @PutMapping("/profile")
    public ResponseEntity<StatusResponse<UserProfileDTO>> updateUserProfile(@RequestHeader("Authorization") String authHeader, @RequestBody UserProfileDTO userProfileDTO) {
        try {
            String tokenCheck = TokenExtractor.extractToken(authHeader);
            if(!jwtTokenProvider.validateToken(authHeader)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token error", null));
            }
            userProfileService.updateProfile(tokenCheck, userProfileDTO);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Profile updated successfully", userProfileDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new StatusResponse<>("Error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An error occurred while updating profile", null));
        }
    }

    @PatchMapping("/profile/bio")
    public ResponseEntity<StatusResponse<String>> updateBio(@RequestHeader("Authorization") String authHeader, @RequestBody UserProfileDTO userProfileDTO) {
        String token = String.valueOf(jwtTokenProvider.validateToken(authHeader));
        if (token == null) {
            return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Invalid token", null));
        }

        try{
            String validToken = TokenExtractor.extractToken(authHeader);
                userProfileService.updateBio(validToken, userProfileDTO);
                return ResponseEntity.ok(new StatusResponse<>("Success", "Update bio successfully", null));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new StatusResponse<>("Error", e.getMessage(), null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", e.getMessage(), null));
        }
    }


    @GetMapping("/profile")
    public ResponseEntity<StatusResponse<UserProfileDTO>> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        try{
            String validToken = TokenExtractor.extractToken(authHeader);
            if(!jwtTokenProvider.validateToken(authHeader)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Invalid token", null));
            }
            UserProfileDTO userProfile = userProfileService.getUserProfileByToken(validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "User profile successfully", userProfile));
        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StatusResponse<>("Error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StatusResponse<>("Error", "An error occurred while getting user profile", null));
        }
    }
    @GetMapping("/checkProfile")
    public ResponseEntity<Boolean> checkProfile(@RequestParam int userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId);

        if (userProfile == null) {
            // Trả về false hoặc thông báo không tìm thấy profile
            return ResponseEntity.ok(false);
        }

        // Tiến hành kiểm tra nếu profile tồn tại
        return ResponseEntity.ok(userProfile.getUser_id() != null);
    }

}
