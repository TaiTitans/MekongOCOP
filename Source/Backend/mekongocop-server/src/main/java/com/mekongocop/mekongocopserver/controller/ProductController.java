package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.common.StatusResponse;
import com.mekongocop.mekongocopserver.dto.ProductDTO;
import com.mekongocop.mekongocopserver.entity.Product;
import com.mekongocop.mekongocopserver.service.ProductService;
import com.mekongocop.mekongocopserver.service.StoreService;
import com.mekongocop.mekongocopserver.service.UserService;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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


    @PutMapping("/seller/store/product/{id}")
    public ResponseEntity<StatusResponse<Object>> updateProduct(@PathVariable int id,@RequestPart("dto") String dto ,@RequestPart("image") List<MultipartFile> images, @RequestHeader("Authorization") String token){
        try{
            String validToken = TokenExtractor.extractToken(token);
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token error", null));
            }
            productService.updateProduct(id, dto, images, validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Product updated", null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Failed", "Failed to process request", null));
        }
    }

    @DeleteMapping("/seller/store/product/{id}")
    public ResponseEntity<StatusResponse<Object>> deleteProduct(@PathVariable int id,@RequestHeader("Authorization") String token){
        try{
            String validToken = TokenExtractor.extractToken(token);
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token error", null));
            }
            productService.deleteProduct(id, validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Product deleted", null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Failed", "Failed to process request", null));
        }
    }

    @PatchMapping("/seller/store/product/{id}/price")
    public ResponseEntity<StatusResponse<Void>> updatePrice(@PathVariable int id, @RequestParam int newPrice, @RequestHeader("Authorization") String token){
        try{
            String validToken = TokenExtractor.extractToken(token);
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token error", null));
            }
            productService.updatePrice(id, newPrice, validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Product updated", null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Failed", "Failed to process request", null));
        }
    }

    @PatchMapping("/seller/store/product/{id}/quantity")
    public ResponseEntity<StatusResponse<Void>> updateQuantity(@PathVariable int id, @RequestParam int newQuantity, @RequestHeader("Authorization") String token){
        try{
            String validToken = TokenExtractor.extractToken(token);
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token error", null));
            }
            productService.updateQuantity(id, newQuantity, validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Product updated", null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Failed", "Failed to process request", null));
        }
    }

    @GetMapping("/common/store/{id}/product")
    public ResponseEntity<StatusResponse<List<ProductDTO>>> getStoreProducts(@PathVariable int id) {
        try {
            List<ProductDTO> productList = productService.getAllProductsOfStore(id);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Product list", productList));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Failed", "Failed to process request", null));
        }
    }


    @GetMapping("/common/category/{id}/product")
    public ResponseEntity<StatusResponse<List<ProductDTO>>> getCategoryProducts(@PathVariable int id) {
        try{
            List<ProductDTO> productDTOList = productService.getAllProductOfCategory(id);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Product list", productDTOList));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Failed", "Failed to process request", null));
        }
    }

    @GetMapping("/common/province/{id}/product")
    public ResponseEntity<StatusResponse<List<ProductDTO>>> getProvinceProducts(@PathVariable int id) {
        try{
            List<ProductDTO> productDTOList = productService.getAllProductOfProvince(id);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Product list", productDTOList));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Failed", "Failed to process request", null));
        }
    }

    @GetMapping("/common/store/product/{id}")
    public ResponseEntity<StatusResponse<ProductDTO>> getProduct(@PathVariable int id) {
        try {
            ProductDTO productDTO = productService.getProductById(id);
            if (productDTO != null) {
                return ResponseEntity.ok(new StatusResponse<>("Success", "Product details", productDTO));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new StatusResponse<>("Failed", "Product not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new StatusResponse<>("Failed", "Failed to process request", null));
        }
    }



}



