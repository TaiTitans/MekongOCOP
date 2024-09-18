package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.Product;
import com.mekongocop.mekongocopserver.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product = :product")
    void deleteByProduct(@Param("product") Product product);

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product = :product")
    List<ProductImage> findByProduct(@Param("product") Product product);

}
