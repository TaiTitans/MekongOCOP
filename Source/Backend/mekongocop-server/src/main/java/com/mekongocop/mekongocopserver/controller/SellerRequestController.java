package com.mekongocop.mekongocopserver.controller;


import com.mekongocop.mekongocopserver.common.StatusResponse;
import com.mekongocop.mekongocopserver.dto.SellerRequestDTO;
import com.mekongocop.mekongocopserver.service.SellerRequestService;
import com.mekongocop.mekongocopserver.service.UserService;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1")
public class SellerRequestController {
    @Autowired
    private SellerRequestService sellerRequestService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    public static final Logger log = LoggerFactory.getLogger(SellerRequestController.class);
    @PostMapping("/user/seller/request")
    public CompletableFuture<ResponseEntity<StatusResponse<Object>>> addSellerRequest(@RequestPart("file") MultipartFile file, @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract the token from the Authorization header
            String validToken = TokenExtractor.extractToken(authHeader);

            // Validate the token
            if (!jwtTokenProvider.validateToken(authHeader)) {
                return CompletableFuture.completedFuture(
                        ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Invalid token", null))
                );
            }

            // Call the service to handle the seller request asynchronously
            return sellerRequestService.saveSellerRequest(file, validToken)
                    .thenApply(result -> ResponseEntity.ok(new StatusResponse<>("Success", "Send request successfully", null)))
                    .exceptionally(e -> {
                        log.error("Error while processing seller request", e);
                        return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Failed to add request", null));
                    });
        } catch (Exception e) {
            log.error("Error in addSellerRequest method", e);
            return CompletableFuture.completedFuture(
                    ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Failed to process request", null))
            );
        }
    }


    @GetMapping("/admin/seller")
    public ResponseEntity<StatusResponse<List<SellerRequestDTO>>> getSellerRequestList() {
        try {
            List<SellerRequestDTO> response = sellerRequestService.getAllSellerRequests();
            return ResponseEntity.ok(new StatusResponse<>("Success", "Get all list seller requests successfully!", response));
        } catch (Exception e) {
            log.error("Error in getSellerRequestList method", e);
            return ResponseEntity.internalServerError()
                    .body(new StatusResponse<>("Error", "Failed to get list of seller requests", null));
        }
    }

    @PatchMapping("/admin/seller/{id}/status")
    public ResponseEntity<StatusResponse<SellerRequestDTO>> updateSellerRequestStatus(
            @PathVariable("id") int sellerRequestId,
            @RequestParam("status") String status) {
        try {
            sellerRequestService.updateSellerRequestStatus(sellerRequestId, status);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Update seller request status successfully", null));
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Failed to update seller request status", null));

        }
    }

}




