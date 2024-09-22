package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.dto.ProductDTO;
import com.mekongocop.mekongocopserver.entity.Favorite;
import com.mekongocop.mekongocopserver.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    @Query("SELECT f FROM Favorite f WHERE f.user.user_id = :userId AND f.product.product_id = :productId")
    Optional<Favorite> findByUserIdAndProductId(@Param("userId") int userId, @Param("productId") int productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Favorite f WHERE f.user.user_id = :userId AND f.product.product_id = :productId")
    void deleteByUserIdAndProductId(@Param("userId") int userId, @Param("productId") int productId);

    @Query("SELECT f.product FROM Favorite f WHERE f.user.user_id = :userId")
    List<Product> findFavoriteProductsByUserId(@Param("userId") int userId);

}
