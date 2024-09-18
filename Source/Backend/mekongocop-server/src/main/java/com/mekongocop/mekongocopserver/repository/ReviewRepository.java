package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.Product;
import com.mekongocop.mekongocopserver.entity.Review;
import com.mekongocop.mekongocopserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query("SELECT r FROM Review r WHERE r.user_id.user_id = :user AND r.product.product_id = :product")
    Optional<Review> findReviewByUserAndProduct(@Param("user") int user_id, @Param("product") int product_id);
}
