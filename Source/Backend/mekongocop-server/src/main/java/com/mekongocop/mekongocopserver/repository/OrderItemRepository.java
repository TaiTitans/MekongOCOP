package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
}
