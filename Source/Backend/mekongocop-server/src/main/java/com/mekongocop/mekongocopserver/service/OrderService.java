package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.OrderDTO;
import com.mekongocop.mekongocopserver.dto.OrderItemDTO;
import com.mekongocop.mekongocopserver.entity.Order;
import com.mekongocop.mekongocopserver.entity.OrderItem;
import com.mekongocop.mekongocopserver.repository.OrderItemRepository;
import com.mekongocop.mekongocopserver.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    public static final Logger log = LoggerFactory.getLogger(OrderService.class);
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemService orderItemService;

    public OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrder_id(order.getOrder_id());
        orderDTO.setTotal_price(order.getTotal_price());
        orderDTO.setPayment(order.getPayment());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setCreated_at(order.getCreated_at());
        orderDTO.setUpdated_at(order.getUpdated_at());

        // Chuyển đổi danh sách OrderItem entity sang DTO
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(orderItem -> orderItemService.convertOrderItemEntityToDTO(orderItem))
                .collect(Collectors.toList());
        orderDTO.setItems(itemDTOs);

        return orderDTO;
    }

    // Convert OrderDTO to entity
    public Order convertToEntity(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrder_id(orderDTO.getOrder_id());
        order.setTotal_price(orderDTO.getTotal_price());
        order.setPayment(orderDTO.getPayment());
        order.setStatus(orderDTO.getStatus());
        order.setCreated_at(orderDTO.getCreated_at());
        order.setUpdated_at(orderDTO.getUpdated_at());

        // Chuyển đổi danh sách OrderItemDTO sang entity
        List<OrderItem> items = orderDTO.getItems().stream()
                .map(orderItemDTO -> orderItemService.convertOrderItemDTOToEntity(orderItemDTO))
                .collect(Collectors.toList());
        order.setItems(items);

        return order;
    }



}
