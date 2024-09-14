package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
