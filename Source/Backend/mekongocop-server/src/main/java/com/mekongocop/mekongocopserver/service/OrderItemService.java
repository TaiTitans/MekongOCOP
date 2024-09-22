package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.OrderItemDTO;
import com.mekongocop.mekongocopserver.entity.Order;
import com.mekongocop.mekongocopserver.entity.OrderItem;
import com.mekongocop.mekongocopserver.entity.Product;
import com.mekongocop.mekongocopserver.repository.OrderItemRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;

    public OrderItem convertOrderItemDTOToEntity(OrderItemDTO orderItemDTO, Order order, Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder_item_id(orderItemDTO.getOrderItemId());
        orderItem.setQuantity(orderItemDTO.getQuantity());
        orderItem.setPrice(orderItemDTO.getPrice());
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        return orderItem;
    }


    public OrderItemDTO convertOrderItemEntityToDTO(OrderItem orderItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        BeanUtils.copyProperties(orderItem, orderItemDTO);

        if (orderItem.getOrder() != null) {
            orderItemDTO.setOrderId(orderItem.getOrder().getOrder_id());
        }

        if (orderItem.getProduct() != null) {
            orderItemDTO.setProductId(orderItem.getProduct().getProduct_id());
        }

        return orderItemDTO;
    }
    public List<OrderItem> saveAll(List<OrderItem> items) {
        return orderItemRepository.saveAll(items);
    }
}
