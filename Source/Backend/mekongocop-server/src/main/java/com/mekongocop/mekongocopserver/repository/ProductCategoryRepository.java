package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
}
