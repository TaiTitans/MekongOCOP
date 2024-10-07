package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.common.StatusResponse;
import com.mekongocop.mekongocopserver.dto.OrderDTO;
import com.mekongocop.mekongocopserver.service.OrderService;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/common/order")
    public ResponseEntity<StatusResponse<OrderDTO>> addOrder(@RequestHeader("Authorization") String token, @RequestParam String address, @RequestParam String payment) {
        try {
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Error token", null));
            }
          OrderDTO orderDTO = orderService.createOrder(validToken, address, payment);
            return ResponseEntity.ok().body(new StatusResponse<>("Success", "Order created", orderDTO));
        }catch (Exception e) {
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
            List<OrderDTO> orderDTOList = orderService.getAllOrdersByUserId(validToken);
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


}
