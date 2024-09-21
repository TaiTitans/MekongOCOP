package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
