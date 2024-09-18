package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.common.StatusResponse;
import com.mekongocop.mekongocopserver.dto.ReviewDTO;
import com.mekongocop.mekongocopserver.service.ReviewService;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/common/product/{id}/review")
    public ResponseEntity<StatusResponse<ReviewDTO>> addReview(@RequestBody ReviewDTO reviewDTO, @PathVariable int id, @RequestHeader("Authorization") String token) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is not found", null));
            }
            ReviewDTO addedReview = reviewService.addReview(reviewDTO, id, validToken);

            if (addedReview == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new StatusResponse<>("Error", "User has already reviewed this product", null));
            }
            return ResponseEntity.ok(new StatusResponse<>("Success", "Review added", null));

        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Add review failed", null));
        }
    }

    @PutMapping("/common/product/{id}/review")
    public ResponseEntity<StatusResponse<ReviewDTO>> updateReview(@RequestBody ReviewDTO reviewDTO, @PathVariable int id, @RequestHeader("Authorization") String token) {
        try {
            String validToken = TokenExtractor.extractToken(token);
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is not valid", null));
            }
            ReviewDTO updatedReview = reviewService.updateReview(reviewDTO, id, validToken);
            if(updatedReview == null){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new StatusResponse<>("Error", "Update has not already reviewed this product", null));
            }
            return ResponseEntity.ok(new StatusResponse<>("Success", "Review updated", updatedReview));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StatusResponse<>("Error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Update review failed", null));
        }
    }

    @DeleteMapping("/common/product/{id}/review")
    public ResponseEntity<StatusResponse<Void>> deleteReview(@PathVariable int id, @RequestHeader("Authorization") String token) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is not valid", null));
            }
            reviewService.deleteReview(id, validToken);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Review deleted", null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Delete review failed", null));
        }
    }

}
