package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.Product;
import com.mekongocop.mekongocopserver.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    @Query(value = "SELECT * FROM products ORDER BY product_id DESC LIMIT 6", nativeQuery = true)
    List<Product> findTop6Products();


    @Query("SELECT COUNT(u) FROM Product u")
    Long countAllProduct();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.store.store_id = :storeId")
    Long countProductsByStore(@Param("storeId") int storeId);

    @Query("SELECT p FROM Product p WHERE LOWER(p.product_name) LIKE LOWER(CONCAT('%', :productName, '%'))")
    Page<Product> findByProductNameContainingIgnoreCase(@Param("productName") String productName, Pageable pageable);

    // Tìm sản phẩm có giá từ 0 đến 100.000
    @Query("SELECT p FROM Product p WHERE p.product_price BETWEEN 0 AND 100000")
    Page<Product> findProductsByPriceRange0To100(Pageable pageable);

    // Tìm sản phẩm có giá từ 100.000 đến 200.000
    @Query("SELECT p FROM Product p WHERE p.product_price BETWEEN 100000 AND 200000")
    Page<Product> findProductsByPriceRange100To200(Pageable pageable);

    // Tìm sản phẩm có giá từ 200.000 đến 500.000
    @Query("SELECT p FROM Product p WHERE p.product_price BETWEEN 200000 AND 500000")
    Page<Product> findProductsByPriceRange200To500(Pageable pageable);

    // Tìm sản phẩm có giá từ 500.000 trở lên
    @Query("SELECT p FROM Product p WHERE p.product_price >= 500000")
    Page<Product> findProductsByPriceAbove500(Pageable pageable);
}
