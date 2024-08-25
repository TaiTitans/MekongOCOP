package com.mekongocop.userservice.service;
import com.mekongocop.userservice.dto.UserProfileDTO;
import com.mekongocop.userservice.entity.User;
import com.mekongocop.userservice.entity.UserProfile;
import com.mekongocop.userservice.repository.UserProfileRepository;
import com.mekongocop.userservice.repository.UserRepository;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
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


    public UserProfileDTO convertJsonToDTO(String json) throws IOException {
        return objectMapper.readValue(json, UserProfileDTO.class);
    }

    public UserProfileDTO convertUserProfileToUserProfileDTO(UserProfile userProfile) {
        return modelMapper.map(userProfile, UserProfileDTO.class);
    }

    public UserProfile convertUserProfileDTOToUserProfile(UserProfileDTO userProfileDTO) {
        return modelMapper.map(userProfileDTO, UserProfile.class);
    }

    @Cacheable(value = "users", key = "#userId")
    public User getUserById(int userId) throws Exception {
        return userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
    }

    @Async
    @Transactional
    public CompletableFuture<Void> addProfile(UserProfileDTO userProfileDTO, MultipartFile file) {
        return CompletableFuture.runAsync(() -> {
            try {
                User user = getUserById(userProfileDTO.user_id);
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

}