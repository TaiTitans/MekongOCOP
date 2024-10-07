package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.Order;
import com.mekongocop.mekongocopserver.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Lấy tất cả đơn hàng của người dùng
    @Query("SELECT o FROM Order o WHERE o.user.user_id = :userId")
    List<Order> findByUserId(@Param("userId") int userId);

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


}
