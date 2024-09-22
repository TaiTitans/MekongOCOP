package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.common.StatusResponse;
import com.mekongocop.mekongocopserver.dto.ProductDTO;
import com.mekongocop.mekongocopserver.entity.Favorite;
import com.mekongocop.mekongocopserver.service.FavoriteService;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/common/favorite/{id}")
    public ResponseEntity<StatusResponse<Void>> addFavorite(@RequestHeader("Authorization") String token, @PathVariable int id) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is not valid", null));
            }
            favoriteService.addFavorite(id, validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Favorite added", null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Add favorite failed", null));
        }
    }


    @GetMapping("/common/favorite")
    public ResponseEntity<StatusResponse<List<ProductDTO>>> getFavorites(@RequestHeader("Authorization") String token) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is not valid", null));
            }
          List<ProductDTO> productDTOList =  favoriteService.getListProductFromFavorite(validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Favorite list", productDTOList));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Get favorite failed", null));
        }
    }
}
