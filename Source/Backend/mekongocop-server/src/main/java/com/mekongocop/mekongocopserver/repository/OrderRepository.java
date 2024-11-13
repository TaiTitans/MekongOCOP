package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.Order;
import com.mekongocop.mekongocopserver.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Lấy tất cả đơn hàng của người dùng
    @Query("SELECT o FROM Order o WHERE o.user.user_id = :userId")
    List<Order> findByUserId(@Param("userId") int userId);

    @Query("SELECT o FROM Order o WHERE o.user.user_id = :userId AND o.order_id = :orderId")
    Optional<Order> findByUserIdAndOrderId(@Param("userId") int userId, @Param("orderId") int orderId);

    @Query("SELECT o FROM Order o JOIN o.items oi JOIN oi.product p WHERE p.store.store_id = :storeId")
    List<Order> findOrdersByStoreId(@Param("storeId") int storeId);


    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'Success'")
    Long countOrders();

    // Tính tổng doanh thu của tháng này có status là Success
    @Query("SELECT SUM(o.total_price) FROM Order o WHERE o.status = 'Success' AND MONTH(o.updated_at) = MONTH(CURRENT_DATE) AND YEAR(o.updated_at) = YEAR(CURRENT_DATE)")
    BigDecimal totalRevenueThisMonth();

    // Tính tổng doanh thu của ngày hôm nay có status là Success
    @Query("SELECT SUM(o.total_price) FROM Order o WHERE o.updated_at = CURRENT_DATE")
    BigDecimal totalRevenueToday();


    // Tính tổng doanh thu trong 1 năm có status là Success
    @Query("SELECT SUM(o.total_price) FROM Order o WHERE o.status = 'Success' AND YEAR(o.updated_at) = YEAR(CURRENT_DATE)")
    BigDecimal totalRevenueThisYear();


    @Query("SELECT SUM(oi.price * oi.quantity) FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.product p " +
            "WHERE p.store.store_id = :storeId AND o.status = :status AND o.created_at BETWEEN :startTime AND :endTime")
    BigDecimal calculateTotalRevenueByStore(@Param("storeId") int storeId,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime,
                                            @Param("status") String status);
    @Query("SELECT COUNT(DISTINCT o.order_id) FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.product p " +
            "WHERE p.store.store_id = :storeId AND o.status IN ('Pending', 'Success', 'Cancel') " +
            "AND o.created_at BETWEEN :startTime AND :endTime")
    long calculateOrderCountByStore(@Param("storeId") int storeId,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);
}
