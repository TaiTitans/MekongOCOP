package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.common.StatusResponse;
import com.mekongocop.mekongocopserver.dto.StoreDTO;
import com.mekongocop.mekongocopserver.entity.Store;
import com.mekongocop.mekongocopserver.repository.StoreRepository;
import com.mekongocop.mekongocopserver.service.StoreService;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1")
public class StoreController {
    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreRepository storeRepository;

    public static final Logger log = LoggerFactory.getLogger(StoreController.class);
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/seller/store")
    public ResponseEntity<StatusResponse<Object>> addStore(
            @RequestPart("dto") String dto,
            @RequestPart("logo") MultipartFile logoFile,
            @RequestPart("banner") MultipartFile bannerFile,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String validToken = TokenExtractor.extractToken(authHeader);

            if (!jwtTokenProvider.validateToken(authHeader)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token error", null));
            }

            // Convert the JSON DTO to StoreDTO object
            StoreDTO storeDTO = storeService.convertJsonToDTO(dto);

            // Call the addStore service method synchronously
            storeService.addStore(storeDTO, logoFile, bannerFile, validToken);

            return ResponseEntity.ok(new StatusResponse<>("Success", "Add Store Successfully", null));
        } catch (Exception e) {
            log.error("Error in addStore method", e);
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Failed to process request", null));
        }
    }

    @PutMapping("seller/store")
    public ResponseEntity<StatusResponse<Object>> updateStore(@RequestPart("dto") String dto, @RequestHeader("Authorization") String authHeader) {
        try {
            String validToken = TokenExtractor.extractToken(authHeader);
            if (!jwtTokenProvider.validateToken(authHeader)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token error", null));
            }
            StoreDTO storeDTO = storeService.convertJsonToDTO(dto);
            storeService.updateStore(storeDTO, validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Update Store Successfully", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Failed to process request", null));
        }
    }

    @PatchMapping("/seller/store/logo")
    public ResponseEntity<StatusResponse<Object>> updateLogoStore(
            @RequestPart("logo") MultipartFile logoFile,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String validToken = TokenExtractor.extractToken(authHeader);

            if (!jwtTokenProvider.validateToken(authHeader)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token error", null));
            }

            storeService.updateLogoStore(logoFile, validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Store logo updated successfully", null));
        } catch (Exception e) {
            log.error("Error updating store logo", e);
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Failed to update store logo", null));
        }
    }


    @PatchMapping("/seller/store/banner")
    public ResponseEntity<StatusResponse<Object>> updateBannerStore(
            @RequestPart("banner") MultipartFile bannerFile,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String validToken = TokenExtractor.extractToken(authHeader);

            if (!jwtTokenProvider.validateToken(authHeader)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token error", null));
            }

            storeService.updateBannerStore(bannerFile, validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Store banner updated successfully", null));
        } catch (Exception e) {
            log.error("Error updating store banner", e);
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Failed to update store banner", null));
        }
    }

    @GetMapping("/seller/store")
    public ResponseEntity<StatusResponse<Store>> getStoreByUserToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String validToken = TokenExtractor.extractToken(authHeader);

            if (!jwtTokenProvider.validateToken(authHeader)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Invalid token", null));
            }

            Store store = storeService.getStore(validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Store data retrieved successfully", store));
        } catch (Exception e) {
            log.error("Failed to retrieve store data", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StatusResponse<>("Error", "Store not found", null));
        }
    }


    @GetMapping("/common/store/data")
    public ResponseEntity<StatusResponse<Store>> getStoreData(@RequestParam int storeId) {
        try {
            Store store = storeService.getStoreData(storeId);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Store data retrieved successfully", store));
        } catch (Exception e) {
            log.error("Failed to retrieve store data", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StatusResponse<>("Error", "Store not found", null));
        }
    }

    @GetMapping("/admin/stores")
    public ResponseEntity<List<StoreDTO>> getAllStores() {
        List<StoreDTO> stores = storeService.getAllStore();
        return ResponseEntity.ok(stores);
    }

    @PatchMapping("/admin/store/{store_id}/status")
    public ResponseEntity<?> updateStoreStatus(@PathVariable("store_id") int storeId) {
        try {
            storeService.updateStoreStatusToBanded(storeId);
            return ResponseEntity.ok(Map.of("message", "Store status updated to Banded successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Store not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unable to update store status"));
        }
    }

    @GetMapping("/seller/store/check")
    public ResponseEntity<?> checkUserStore(@RequestHeader("Authorization") String token) {
        try {
            String validToken = TokenExtractor.extractToken(token);

            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Invalid token", null));
            }
            boolean hasStore = storeService.hasUserStore(validToken);
            return ResponseEntity.ok(hasStore);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(false);
        }
    }

    @GetMapping("/seller/store/id")
    public ResponseEntity<?> getStoreId(@RequestHeader("Authorization") String token){
        try{
            String validToken = TokenExtractor.extractToken(token);

            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Invalid token", null));
            }
            Integer hasStoreId = storeService.getStoreIdByUserId(validToken);
            return ResponseEntity.ok(hasStoreId);
        }catch (Exception e){
            return ResponseEntity.status(500).body(false);
        }
    }

}
