package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.StoreDTO;
import com.mekongocop.mekongocopserver.entity.Store;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.StoreRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

    private StoreDTO convertStoreToDTO(Store store) {
    return new StoreDTO(
            store.getStore_id(),
            store.getUser_id().getUser_id(),
            store.getStore_name(),
            store.getStore_description(),
            store.getStore_address(),
            store.getStore_logo(),
            store.getStore_banner(),
            store.getStatus(),
            store.getCreated_at(),
            store.getUpdated_at()
    );
}

public Store convertDTOToStore(StoreDTO storeDTO) {
    return modelMapper.map(storeDTO, Store.class);
}


    @Cacheable(value = "users", key = "#userId")
    public User getUserById(int userId) throws Exception {
        return userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
    }

    @Async
    @Transactional
    public CompletableFuture<Void> addStore(StoreDTO storeDTO, MultipartFile logo, MultipartFile banner) {
    return CompletableFuture.runAsync(()->{
        try {
            User user = getUserById(storeDTO.user_id);
            Store store = convertDTOToStore(storeDTO);
            if(logo != null && banner != null) {
                try{
                    Map<String, Object> uploadLogo = cloudinaryService.uploadImage(logo, String.valueOf(storeDTO.user_id));
                    String logoUrl = (String) uploadLogo.get("secure_url");
                    Map<String, Object> uploadBanner = cloudinaryService.uploadImage(banner, String.valueOf(storeDTO.user_id));
                    String bannerUrl = (String) uploadBanner.get("secure_url");
                    if(logoUrl != null && bannerUrl != null) {
                        store.setStore_logo(logoUrl);
                        store.setStore_banner(bannerUrl);
                        log.info("Image uploaded");
                    }else{
                        log.warn("Image not uploaded");
                    }
                }catch (Exception e){
                    log.error("Failed to upload image", e);
                }
            }else{
                log.warn("No image file provided for upload");
            }
            store.setCreated_at(new Date());
            store.setUpdated_at(new Date());
            storeRepository.save(store);
        } catch (Exception e) {
            log.error("Failed to add store", e);
            throw new RuntimeException(e);
        }
    });
    }

}
