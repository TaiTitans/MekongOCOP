package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.Product;
import com.mekongocop.mekongocopserver.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.product_id = :id AND p.store = :store")
    Optional<Product> findByIdAndStore(@Param("product_id") int product_id, @Param("store") Store store);
}
