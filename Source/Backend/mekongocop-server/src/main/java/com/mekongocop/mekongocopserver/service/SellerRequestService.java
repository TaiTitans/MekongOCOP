package com.mekongocop.mekongocopserver.service;


import com.mekongocop.mekongocopserver.dto.SellerRequestDTO;
import com.mekongocop.mekongocopserver.entity.Role;
import com.mekongocop.mekongocopserver.entity.SellerRequest;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.entity.UserProfile;
import com.mekongocop.mekongocopserver.repository.SellerRequestRepository;
import com.mekongocop.mekongocopserver.repository.UserProfileRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class SellerRequestService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    public static final Logger log = LoggerFactory.getLogger(SellerRequestService.class);
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private SellerRequestRepository sellerRequestRepository;

    @Autowired
    private EmailService emailService;
    private SellerRequestDTO convertSellerRequestToDTO(SellerRequest sellerRequest) {
        return new SellerRequestDTO(
                sellerRequest.getRequest_id(),
                sellerRequest.getUser_id().getUser_id(),
                sellerRequest.getCertification(),
                sellerRequest.getStatus(),
                sellerRequest.getRequest_date()
        );
    }

    public SellerRequest convertSellerRequestDTOtoSellerRequest(SellerRequestDTO sellerRequestDTO) {
        return modelMapper.map(sellerRequestDTO, SellerRequest.class);
    }
    @Cacheable(value = "users", key = "#userId")
    public User getUserById(int userId) throws Exception {
        return userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
    }


    public List<SellerRequestDTO> getAllSellerRequests() {
        try{
            List<SellerRequest> sellerRequests = sellerRequestRepository.findAll();
            List<SellerRequestDTO> sellerRequestDTOs = sellerRequests.stream().map(this::convertSellerRequestToDTO).collect(Collectors.toList());
            return sellerRequestDTOs;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error getting all sellerRequests");
        }
    }

    public void updateSellerRequestStatus(int sellerRequestId, String status) {
        try {
            SellerRequest sellerRequest = sellerRequestRepository.findById(sellerRequestId)
                    .orElseThrow(() -> new Exception("Request not found"));

            sellerRequest.setStatus(status);
            sellerRequestRepository.save(sellerRequest);

            if (status.equals("Accept")) {
                User user = sellerRequest.getUser_id();
                user.getRoles().add(new Role(3, "ROLE_SELLER"));
                userRepository.save(user);
                emailService.sendApprovalEmail(user.getEmail(), user.getUsername());
            } else if (status.equals("Reject")) {
                emailService.sendRejectionEmail(sellerRequest.getUser_id().getEmail(), sellerRequest.getUser_id().getUsername());
            }
        } catch (Exception e) {
            log.error("Error updating seller request status: {}", e.getMessage());
            throw new RuntimeException("Error updating seller request status");
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Void> saveSellerRequest(MultipartFile file, String token) {
        return CompletableFuture.runAsync(() -> {
            try {
                int userId = jwtTokenProvider.getUserIdFromToken(token);
                Optional<User> userOptional = userRepository.findById(userId);

                if (userOptional.isPresent()) {
                    if (file != null && !file.isEmpty()) {
                        SellerRequest request = new SellerRequest();
                        User user = userOptional.get();

                        try {
                            // Upload image to Cloudinary
                            Map<String, Object> uploadCertification = cloudinaryService.uploadImage(file, String.valueOf(userId));
                            String imageUrl = (String) uploadCertification.get("secure_url");

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                request.setCertification(imageUrl);
                                log.info("Image uploaded successfully. URL: {}", imageUrl);
                            } else {
                                log.warn("Image upload successful but URL is null");
                            }

                            // Set attributes for seller request
                            request.setStatus("Pending");
                            request.setRequest_date(new Date());
                            request.setUser_id(user);

                            // Save seller request to DB
                            sellerRequestRepository.save(request);

                        } catch (Exception e) {
                            log.error("Failed to upload image", e);
                            throw new RuntimeException("Failed to upload image", e);
                        }

                    } else {
                        log.warn("Uploaded file is null or empty");
                        throw new RuntimeException("Invalid uploaded file");
                    }
                } else {
                    log.warn("User not found");
                    throw new RuntimeException("User not found");
                }

            } catch (Exception e) {
                log.error("Error adding seller request", e);
                throw new RuntimeException("Error adding seller request", e);
            }
        });
    }
}
