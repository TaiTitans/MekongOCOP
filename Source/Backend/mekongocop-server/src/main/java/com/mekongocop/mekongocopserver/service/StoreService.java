package com.mekongocop.mekongocopserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mekongocop.mekongocopserver.dto.StoreDTO;
import com.mekongocop.mekongocopserver.entity.Store;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.StoreRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class StoreService {
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public static final Logger log = LoggerFactory.getLogger(SellerRequestService.class);
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Qualifier("objectMapper")
    @Autowired
    private ObjectMapper objectMapper;
    @PersistenceContext
    private EntityManager entityManager;

    private StoreDTO convertStoreToDTO(Store store) {
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.store_id = store.getStore_id(); // Lấy ID của cửa hàng
        storeDTO.user_id = store.getUser_id().getUser_id(); // Lấy ID của người dùng từ User object
        storeDTO.store_name = store.getStore_name(); // Tên cửa hàng
        storeDTO.store_description = store.getStore_description(); // Mô tả cửa hàng
        storeDTO.store_address = store.getStore_address(); // Địa chỉ cửa hàng
        storeDTO.store_logo = store.getStore_logo(); // Logo cửa hàng
        storeDTO.store_banner = store.getStore_banner(); // Banner cửa hàng
        storeDTO.status = store.getStatus(); // Trạng thái (Active/Banded)
        storeDTO.created_at = store.getCreated_at(); // Ngày tạo cửa hàng
        storeDTO.updated_at = store.getUpdated_at(); // Ngày cập nhật cửa hàng

        log.info("Converted StoreDTO: {}", storeDTO); // Log kết quả chuyển đổi
        return storeDTO;
    }


    public Store convertDTOToStore(StoreDTO storeDTO) {
    return modelMapper.map(storeDTO, Store.class);
}

public StoreDTO convertJsonToDTO(String json)throws IOException {
        return objectMapper.readValue(json, StoreDTO.class);
}

    @Cacheable(value = "users", key = "#userId")
    public User getUserById(int userId) throws Exception {
        return userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
    }

    @Transactional
    public void addStore(StoreDTO storeDTO, MultipartFile logo, MultipartFile banner, String token) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<User> userOptional = userRepository.findById(userId);
            Store store = convertDTOToStore(storeDTO);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Merge the user to ensure it's managed by Hibernate
                User managedUser = entityManager.merge(user);
                store.setUser_id(managedUser);

                // Upload files (logo and banner)
                if (logo != null && banner != null) {
                    try {
                        Map<String, Object> uploadLogo = cloudinaryService.uploadImage(logo, String.valueOf(storeDTO.user_id));
                        String logoUrl = (String) uploadLogo.get("secure_url");
                        Map<String, Object> uploadBanner = cloudinaryService.uploadImage(banner, String.valueOf(storeDTO.user_id));
                        String bannerUrl = (String) uploadBanner.get("secure_url");
                        if (logoUrl != null && bannerUrl != null) {
                            store.setStore_logo(logoUrl);
                            store.setStore_banner(bannerUrl);
                            log.info("Images uploaded");
                        } else {
                            log.warn("Images not uploaded");
                        }
                    } catch (Exception e) {
                        log.error("Failed to upload images", e);
                    }
                } else {
                    log.warn("No image file provided for upload");
                }

                // Set store status and timestamps
                store.setStatus("Active");
                store.setCreated_at(new Date());
                store.setUpdated_at(new Date());

                // Save the store
                entityManager.merge(store);

            } else {
                log.error("User not found");
            }
        } catch (Exception e) {
            log.error("Failed to add store", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void updateStore(StoreDTO storeDTO, String token) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Tìm Store hiện có từ cơ sở dữ liệu
                Optional<Store> existingStoreOptional = storeRepository.findByUserId(userId);

                if (existingStoreOptional.isPresent()) {
                    Store existingStore = existingStoreOptional.get();

                    // Cập nhật các trường cụ thể từ StoreDTO
                    if (storeDTO.store_name != null) {
                        existingStore.setStore_name(storeDTO.store_name);
                    }
                    if (storeDTO.store_description != null) {
                        existingStore.setStore_description(storeDTO.store_description);
                    }
                    if (storeDTO.store_address != null) {
                        existingStore.setStore_address(storeDTO.store_address);
                    }

                    // Giữ nguyên các trường khác
                    existingStore.setUpdated_at(new Date());

                    // Đảm bảo rằng User được quản lý bởi Hibernate
                    User managedUser = entityManager.merge(user);
                    existingStore.setUser_id(managedUser);

                    // Lưu Store cập nhật
                    storeRepository.save(existingStore);
                } else {
                    log.error("Store not found");
                    throw new Exception("Store not found");
                }
            } else {
                log.error("User not found");
                throw new Exception("User not found");
            }
        } catch (Exception e) {
            log.error("Failed to update store", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void updateLogoStore(MultipartFile logo, String token) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<Store> storeOptional = storeRepository.findByUserId(userId);

            if (storeOptional.isPresent()) {
                Store store = storeOptional.get();

                // Upload new logo to Cloudinary
                Map<String, Object> uploadResult = cloudinaryService.uploadImage(logo, String.valueOf(userId));
                String logoUrl = (String) uploadResult.get("secure_url");

                if (logoUrl != null) {
                    store.setStore_logo(logoUrl);
                    store.setUpdated_at(new Date());
                    storeRepository.save(store);
                } else {
                    log.warn("Failed to upload logo");
                    throw new RuntimeException("Logo upload failed");
                }
            } else {
                log.error("Store not found for user");
                throw new Exception("Store not found");
            }
        } catch (Exception e) {
            log.error("Failed to update store logo", e);
            throw new RuntimeException(e);
        }
    }


    @Transactional
    public void updateBannerStore(MultipartFile banner, String token) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<Store> storeOptional = storeRepository.findByUserId(userId);

            if (storeOptional.isPresent()) {
                Store store = storeOptional.get();

                // Upload new banner to Cloudinary
                Map<String, Object> uploadResult = cloudinaryService.uploadImage(banner, String.valueOf(userId));
                String bannerUrl = (String) uploadResult.get("secure_url");

                if (bannerUrl != null) {
                    store.setStore_banner(bannerUrl);
                    store.setUpdated_at(new Date());
                    storeRepository.save(store);
                } else {
                    log.warn("Failed to upload banner");
                    throw new RuntimeException("Banner upload failed");
                }
            } else {
                log.error("Store not found for user");
                throw new Exception("Store not found");
            }
        } catch (Exception e) {
            log.error("Failed to update store banner", e);
            throw new RuntimeException(e);
        }
    }

    public Store getStore(String token) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<Store> storeOptional = storeRepository.findByUserId(userId);
            if (storeOptional.isPresent()) {
                Store store = storeOptional.get();
                return store;
            } else {
                throw new Exception("Store not found for user");
            }
        } catch (Exception e) {
            log.error("Error in getting store by token", e);
            throw new RuntimeException("Error retrieving store data");
        }
    }

    public Store getStoreData(int storeId){
        try{
            Optional<Store> store = storeRepository.findById(storeId);
            if (store.isPresent()) {
                Store storeResult =  store.get();
                return storeResult;

            }else {
                throw new Exception("Store not found");
            }

        }catch (Exception e){
            log.error("Error in getting store data", e);
            throw new RuntimeException("Error retrieving store data");
        }
    }

    public List<StoreDTO> getAllStore(){
        List<Store> stores = storeRepository.findAll();
        return convertStoreListToStoreDTOList(stores);
    }

    private List<StoreDTO> convertStoreListToStoreDTOList(List<Store> stores) {
        return stores.stream().map(this::convertStoreToDTO).collect(Collectors.toList());
    }
    public void updateStoreStatusToBanded(int storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found with ID: " + storeId));

        // Kiểm tra trạng thái hiện tại và chuyển đổi
        if ("Active".equals(store.getStatus())) {
            store.setStatus("Banded");
        } else if ("Banded".equals(store.getStatus())) {
            store.setStatus("Active");
        } else {
            throw new IllegalStateException("Invalid store status");
        }

        // Lưu lại thay đổi và trả về trạng thái mới
        storeRepository.save(store);
    }
}

