package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.common.StatusResponse;
import com.mekongocop.mekongocopserver.dto.ProductDTO;
import com.mekongocop.mekongocopserver.service.ProductService;
import com.mekongocop.mekongocopserver.service.StoreService;
import com.mekongocop.mekongocopserver.service.UserService;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @PostMapping("/seller/store/product")
    public ResponseEntity<StatusResponse<Object>> addProduct(@RequestPart("dto") String dto, @RequestPart("image") List<MultipartFile> images, @RequestHeader("Authorization") String token) {
        try {
            String validToken = TokenExtractor.extractToken(token);
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token error", null));
            }
            productService.addProduct(dto, images, validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Product added", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Failed", "Failed to process request", null));
        }
    }


}
