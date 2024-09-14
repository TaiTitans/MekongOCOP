package com.mekongocop.mekongocopserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mekongocop.mekongocopserver.dto.UserProfileDTO;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.entity.UserProfile;
import com.mekongocop.mekongocopserver.repository.UserProfileRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
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
        return modelMapper.map(userProfile, UserProfileDTO.class);
    }

    public UserProfile convertUserProfileDTOToUserProfile(UserProfileDTO userProfileDTO) {
        return modelMapper.map(userProfileDTO, UserProfile.class);
    }

    @Cacheable(value = "users", key = "#userId")
    public User getUserById(int userId) throws Exception {
        return userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
    }


    public void updateProfile(String token, UserProfileDTO userProfileDTO) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<UserProfile> userProfileOptional = userProfileRepository.findById(userId);
            if (userProfileOptional.isPresent()) {
                UserProfile userProfile = userProfileOptional.get();
                modelMapper.map(userProfileDTO, userProfile);
                userProfileRepository.save(userProfile);
            } else {
                throw new Exception("User not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while editing user profile");
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


    public UserProfile getUserProfileByToken(String token){
        try{
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<UserProfile> userProfileOptional = userProfileRepository.findById(userId);
            if(userProfileOptional.isPresent()){
                UserProfile userProfile = userProfileOptional.get();
                return userProfile;
            }else{
                throw new Exception("User not found");
            }
        }catch (Exception e){
            throw new RuntimeException("An error occurred while getting user profile");
        }
    }



}
