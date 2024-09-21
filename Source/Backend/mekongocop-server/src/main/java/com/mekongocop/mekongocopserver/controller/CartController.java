package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.common.StatusResponse;
import com.mekongocop.mekongocopserver.dto.CartDTO;
import com.mekongocop.mekongocopserver.dto.CartItemDTO;
import com.mekongocop.mekongocopserver.service.CartService;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/common/cart")
    public ResponseEntity<StatusResponse<Void>> addCart(@RequestHeader("Authorization") String token, @RequestBody CartItemDTO cartItem) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is error", null));
            }
            cartService.addCart(validToken, cartItem);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Add product to cart success", null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", e.getMessage(), null));
        }
    }


    @GetMapping("/common/cart")
    public ResponseEntity<StatusResponse<CartDTO>> getCartDetails(@RequestHeader("Authorization") String token) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is error", null));
            }
            CartDTO cartDTO = cartService.getCartWithProductDetails(validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Get cart success", cartDTO));

        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", e.getMessage(), null));
        }
    }
    @DeleteMapping("/common/cart")
    public ResponseEntity<StatusResponse<Void>> deleteCart(@RequestHeader("Authorization") String token, @RequestParam int productId) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is error", null));
            }
            cartService.removeFromCart(validToken, productId);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Remove cart success", null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", e.getMessage(), null));
        }
    }

    @PutMapping("/common/cart")
    public ResponseEntity<StatusResponse<Void>> updateCart(@RequestHeader("Authorization") String token, @RequestParam int productId, @RequestParam int newQuantity) {
        try {
            String validToken = TokenExtractor.extractToken(token);
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is error", null));
            }
            cartService.updateCartItemQuantity(validToken, productId, newQuantity);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Update cart success", null));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", e.getMessage(), null));
        }
    }


}
