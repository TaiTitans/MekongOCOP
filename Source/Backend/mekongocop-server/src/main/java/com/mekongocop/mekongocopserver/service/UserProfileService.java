package com.mekongocop.mekongocopserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mekongocop.mekongocopserver.dto.UserProfileDTO;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.entity.UserProfile;
import com.mekongocop.mekongocopserver.repository.UserProfileRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@EnableCaching
public class UserProfileService {
    private static final Logger logger =  LoggerFactory.getLogger(UserProfileService.class);
    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    public UserProfileDTO convertJsonToDTO(String json) throws IOException {
        return objectMapper.readValue(json, UserProfileDTO.class);
    }

    public UserProfileDTO convertUserProfileToUserProfileDTO(UserProfile userProfile) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.profileId = userProfile.getProfile_id();
        dto.user_id = userProfile.getUser_id().getUser_id();
        dto.full_name = userProfile.getFull_name();
        dto.birthday = userProfile.getBirthday();
        dto.sex = userProfile.getSex();
        dto.bio = userProfile.getBio();
        dto.user_profile_image = userProfile.getProfile_picture();
        return dto;
    }

    public UserProfile convertUserProfileDTOToUserProfile(UserProfileDTO userProfileDTO) {
        UserProfile userProfile = new UserProfile();
        userProfile.setProfile_id(userProfileDTO.profileId);

      User user = userRepository.findById(userProfileDTO.user_id).orElseThrow(() -> new EntityNotFoundException("User not found"));
      userProfile.setUser_id(user);

        userProfile.setFull_name(userProfileDTO.full_name);
        userProfile.setBirthday(userProfileDTO.birthday);
        userProfile.setSex(userProfileDTO.sex);
        userProfile.setBio(userProfileDTO.bio);
        userProfile.setProfile_picture(userProfileDTO.user_profile_image);

        return userProfile;
    }
    @Cacheable(value = "users", key = "#userId")
    public User getUserById(int userId) throws Exception {
        return userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
    }


    public void updateProfile(String token, UserProfileDTO userProfileDTO) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            UserProfile userProfile = userProfileRepository.findByUserId(userId);

            if (userProfile == null) {
                throw new EntityNotFoundException("User profile not found for userId: " + userId);
            }

            // Cập nhật các trường từ DTO, giữ lại profile_id và user_id
            userProfile.setFull_name(userProfileDTO.full_name);
            userProfile.setBirthday(userProfileDTO.birthday);
            userProfile.setSex(userProfileDTO.sex);
            userProfile.setBio(userProfileDTO.bio);
            // Giữ nguyên hình ảnh cũ nếu không có hình mới
            if (userProfileDTO.user_profile_image != null) {
                userProfile.setProfile_picture(userProfileDTO.user_profile_image);
            }

            // Lưu profile đã cập nhật
            userProfileRepository.save(userProfile);

        } catch (EntityNotFoundException e) {
            throw new RuntimeException("User not found", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while updating user profile", e);
        }
    }


    public void updateBio(String token, UserProfileDTO userProfileDTO){
        try{
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<UserProfile> userProfileOptional = userProfileRepository.findById(userId);
            if(userProfileOptional.isPresent()){
                UserProfile userProfile = userProfileOptional.get();
                userProfile.setBio(userProfileDTO.bio);
                userProfileRepository.save(userProfile);
            }else{
                throw new Exception("User not found");
            }
        }catch (Exception e){
            throw new RuntimeException("An error occurred while editing bio");
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Void> addProfile(UserProfileDTO userProfileDTO, MultipartFile file, String token) {
        return CompletableFuture.runAsync(() -> {
            try {
                int userId = jwtTokenProvider.getUserIdFromToken(token);
                userProfileDTO.setUser_id(userId);
                UserProfile userProfile = convertUserProfileDTOToUserProfile(userProfileDTO);

                if (file != null) {
                    try {
                        Map<String, Object> uploadResult = cloudinaryService.uploadImage(file, String.valueOf(userProfileDTO.user_id));
                        String imageUrl = (String) uploadResult.get("secure_url");
                        if (imageUrl != null) {
                            userProfile.setProfile_picture(imageUrl);
                            logger.info("Image uploaded successfully. URL: {}", imageUrl);
                        } else {
                            logger.warn("Image upload successful but URL is null");
                        }
                    } catch (Exception e) {
                        logger.error("Failed to upload image", e);
                    }
                } else {
                    logger.info("No image file provided for upload");
                }
                UserProfile savedProfile = userProfileRepository.save(userProfile);
                logger.info("User profile saved successfully. Profile ID: {}", savedProfile.getProfile_id());
            } catch (Exception e) {
                logger.error("Error adding user profile", e);
                throw new RuntimeException("Error adding user profile", e);
            }
        });
    }


    public UserProfileDTO getUserProfileByToken(String token) throws Exception {
        int userId = jwtTokenProvider.getUserIdFromToken(token);
        UserProfile userProfile = userProfileRepository.findByUserId(userId);
        if (userProfile == null) {
            throw new EntityNotFoundException("User profile not found for userId: " + userId);
        }
        UserProfileDTO userProfileDTO = convertUserProfileToUserProfileDTO(userProfile);
        return userProfileDTO;
    }
    public boolean hasUserProfile(int userId) {
        // Kiểm tra số lượng profile liên quan đến userId
        return userProfileRepository.countByUserId(userId) > 0;
    }

    public UserProfileDTO getProfileByUserId(int userId){
        UserProfile userProfile = userProfileRepository.findByUserId(userId);
        if (userProfile == null) {
            throw new EntityNotFoundException("User profile not found for userId: " + userId);
        }
        UserProfileDTO userProfileDTO = convertUserProfileToUserProfileDTO(userProfile);
        return userProfileDTO;
    }

}
