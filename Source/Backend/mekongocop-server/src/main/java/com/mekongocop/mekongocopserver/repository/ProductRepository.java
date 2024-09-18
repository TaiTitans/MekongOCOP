package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.Product;
import com.mekongocop.mekongocopserver.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.product_id = :product_id AND p.store = :store")
    Optional<Product> findByIdAndStore(@Param("product_id") int product_id, @Param("store") Store store);

    @Query("SELECT p FROM Product p WHERE p.store.store_id = :storeId")
    List<Product> findAllByStoreId(@Param("storeId") int storeId);

    @Query("SELECT p FROM Product p WHERE p.productCategory.category_id = :categoryId")
    List<Product> findAllByCategoryId(@Param("categoryId") int categoryId);

    @Query("SELECT p FROM Product p WHERE p.province.province_id = :provinceId")
    List<Product> findAllByProvinceId(@Param("provinceId") int provinceId);
}
