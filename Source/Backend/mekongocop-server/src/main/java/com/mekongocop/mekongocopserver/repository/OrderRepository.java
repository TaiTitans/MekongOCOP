package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.Order;
import com.mekongocop.mekongocopserver.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Lấy tất cả đơn hàng của người dùng
    @Query("SELECT o FROM Order o WHERE o.user.user_id = :userId")
    List<Order> findByUserId(@Param("userId") int userId);

    @Query("SELECT o FROM Order o JOIN o.items oi JOIN oi.product p WHERE p.store.store_id = :storeId")
    List<Order> findOrdersByStoreId(@Param("storeId") int storeId);

}
