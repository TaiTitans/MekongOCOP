package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.common.StatusResponse;
import com.mekongocop.mekongocopserver.dto.OrderDTO;
import com.mekongocop.mekongocopserver.repository.OrderRepository;
import com.mekongocop.mekongocopserver.service.OrderService;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private OrderRepository orderRepository;
    @PostMapping("common/order")
    public ResponseEntity<StatusResponse<List<OrderDTO>>> addOrder(
            @RequestHeader("Authorization") String token,
            @RequestParam String address,
            @RequestParam String payment,
            @RequestParam(required = false) String voucherCode) {
        try {
            String validToken = TokenExtractor.extractToken(token);
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Error token", null));
            }
            // Truyền voucherCode vào createOrder và nhận về List<OrderDTO>
            List<OrderDTO> orderDTOList = orderService.createOrder(validToken, address, payment, voucherCode);
            return ResponseEntity.ok().body(new StatusResponse<>("Success", "Order(s) created", orderDTOList));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Create Order Failed", null));
        }
    }



    @GetMapping("/common/order")
    public ResponseEntity<StatusResponse<List<OrderDTO>>> getOrder(@RequestHeader("Authorization") String token) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Error token", null));
            }
            List<OrderDTO> orderDTOList = orderService.getAllOrdersByUserId(validToken);
            return ResponseEntity.ok().body(new StatusResponse<>("Success", "Order list", orderDTOList));

        }catch (Exception e) {
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Get Order Failed", null));
        }
    }

    @GetMapping("/common/order/{id}")
    public ResponseEntity<StatusResponse<OrderDTO>> getOrderById(@RequestHeader("Authorization") String token, @PathVariable int id) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Error token", null));
            }
            OrderDTO orderDTO = orderService.getOrderById(validToken, id);
            return ResponseEntity.ok().body(new StatusResponse<>("Success", "Order found", orderDTO));
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Get Order Failed", null));
        }
    }

    @GetMapping("/seller/store/order")
    public ResponseEntity<StatusResponse<List<OrderDTO>>> getSellerOrder(@RequestHeader("Authorization") String token) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Error token", null));
            }
            List<OrderDTO> orderDTOList = orderService.getOrderByStore(validToken);
            return ResponseEntity.ok().body(new StatusResponse<>("Success", "Order list", orderDTOList));
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Get Order Failed", null));
        }
    }


    @PatchMapping("/seller/store/order/{id}/pending")
    public ResponseEntity<StatusResponse<Void>> updateOrderPending(@PathVariable int id) {
        try{
            orderService.updateOrderStatusToPending(id);
            return ResponseEntity.ok().body(new StatusResponse<>("Success", "Order pending updated", null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Update Order Failed", null));
        }
    }

    @PatchMapping("/seller/store/order/{id}/cancel")
    public ResponseEntity<StatusResponse<Void>> updateOrderCancel(@PathVariable int id) {
        try{
            orderService.updateOrderStatusToCancel(id);
            return ResponseEntity.ok().body(new StatusResponse<>("Success", "Order cancel updated", null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Update Order Failed", null));
        }
    }
    @PatchMapping("/common/order/{id}/success")
    public ResponseEntity<StatusResponse<Void>> updateOrderSuccess(@RequestHeader("Authorization") String token,@PathVariable int id) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Error token", null));
            }
            orderService.updateOrderStatusToSuccess(validToken, id);
            return ResponseEntity.ok().body(new StatusResponse<>("Success", "Order updated", null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Update Order Failed", null));
        }
    }
    @PatchMapping("/common/order/{id}/cancel")
    public ResponseEntity<StatusResponse<Void>> updateOrderCancelRequest(@PathVariable int id) {
        try{
            orderService.requestOrderCancellation(id);
            return ResponseEntity.ok().body(new StatusResponse<>("Success", "Order cancel request success", null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Update Order Failed", null));
        }
    }


    @GetMapping("/admin/order/count")
    public ResponseEntity<?> countOrderSuccess(){
     Long totalOrders = orderService.getTotalOrder();
        return ResponseEntity.ok(Map.of(
                "totalOrders", totalOrders
        ));
    }

    @GetMapping("/admin/order/total")
    public ResponseEntity<?> totalRevenue() {
        try {
            BigDecimal today = orderService.getTotalRevenueToday();
            BigDecimal month = orderService.getTotalRevenueThisMonth();
            BigDecimal year = orderService.getTotalRevenueThisYear();

            return ResponseEntity.ok(Map.of(
                    "totalToday", today != null ? today : BigDecimal.ZERO,
                    "totalMonth", month != null ? month : BigDecimal.ZERO,
                    "totalYear", year != null ? year : BigDecimal.ZERO
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching revenue data: " + e.getMessage());
        }
    }

    @GetMapping("seller/store/total/{storeId}")
    public Map<String, BigDecimal> calculateStoreRevenue(@PathVariable int storeId) {
        LocalDateTime now = LocalDateTime.now();

        // Doanh thu trong ngày của cửa hàng
        LocalDateTime startOfDay = now.with(LocalTime.MIN);
        BigDecimal dayRevenue = orderRepository.calculateTotalRevenueByStore(storeId, startOfDay, now, "Success");

        // Doanh thu trong tháng của cửa hàng
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        BigDecimal monthRevenue = orderRepository.calculateTotalRevenueByStore(storeId, startOfMonth, now, "Success");

        // Doanh thu trong năm của cửa hàng
        LocalDateTime startOfYear = now.with(TemporalAdjusters.firstDayOfYear()).with(LocalTime.MIN);
        BigDecimal yearRevenue = orderRepository.calculateTotalRevenueByStore(storeId, startOfYear, now, "Success");

        // Trả về kết quả
        Map<String, BigDecimal> revenueMap = new HashMap<>();
        revenueMap.put("dayRevenue", dayRevenue != null ? dayRevenue : BigDecimal.ZERO);
        revenueMap.put("monthRevenue", monthRevenue != null ? monthRevenue : BigDecimal.ZERO);
        revenueMap.put("yearRevenue", yearRevenue != null ? yearRevenue : BigDecimal.ZERO);

        return revenueMap;
    }


    @GetMapping("seller/store/order/total/{storeId}")
    public Map<String, Long> calculateOrderCountByStore(@PathVariable int storeId) {
        LocalDateTime now = LocalDateTime.now();

        // Đếm số đơn hàng trong ngày của cửa hàng
        LocalDateTime startOfDay = now.with(LocalTime.MIN);
        long dayOrderCount = orderRepository.calculateOrderCountByStore(storeId, startOfDay, now);

        // Đếm số đơn hàng trong tháng của cửa hàng
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        long monthOrderCount = orderRepository.calculateOrderCountByStore(storeId, startOfMonth, now);

        // Đếm số đơn hàng trong năm của cửa hàng
        LocalDateTime startOfYear = now.with(TemporalAdjusters.firstDayOfYear()).with(LocalTime.MIN);
        long yearOrderCount = orderRepository.calculateOrderCountByStore(storeId, startOfYear, now);

        // Trả về kết quả
        Map<String, Long> orderCountMap = new HashMap<>();
        orderCountMap.put("dayOrderCount", dayOrderCount);
        orderCountMap.put("monthOrderCount", monthOrderCount);
        orderCountMap.put("yearOrderCount", yearOrderCount);

        return orderCountMap;
    }
}
