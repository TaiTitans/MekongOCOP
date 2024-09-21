package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.OrderItemDTO;
import com.mekongocop.mekongocopserver.entity.Order;
import com.mekongocop.mekongocopserver.entity.OrderItem;
import com.mekongocop.mekongocopserver.entity.Product;
import com.mekongocop.mekongocopserver.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;

    public OrderItem convertOrderItemDTOToEntity(OrderItemDTO orderItemDTO) {
        OrderItem orderItem = new OrderItem();

        // Chuyển đổi các trường đơn giản
        orderItem.setOrder_item_id(orderItemDTO.getOrderItemId());
        orderItem.setQuantity(orderItemDTO.getQuantity());
        orderItem.setPrice(orderItemDTO.getPrice());

        // Ánh xạ orderId sang Order entity (Chỉ cần set ID, không cần fetch toàn bộ đối tượng)
        Order order = new Order();
        order.setOrder_id(orderItemDTO.getOrderId());
        orderItem.setOrder(order);

        // Ánh xạ productId sang Product entity (Chỉ cần set ID, không cần fetch toàn bộ đối tượng)
        Product product = new Product();
        product.setProduct_id(orderItemDTO.getProductId());
        orderItem.setProduct(product);

        return orderItem;
    }


    public OrderItemDTO convertOrderItemEntityToDTO(OrderItem orderItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();

        // Chuyển đổi các trường đơn giản
        orderItemDTO.setOrderItemId(orderItem.getOrder_item_id());
        orderItemDTO.setQuantity(orderItem.getQuantity());
        orderItemDTO.setPrice(orderItem.getPrice());

        // Ánh xạ order và product (chỉ lấy ID)
        if (orderItem.getOrder() != null) {
            orderItemDTO.setOrderId(orderItem.getOrder().getOrder_id());
        }

        if (orderItem.getProduct() != null) {
            orderItemDTO.setProductId(orderItem.getProduct().getProduct_id());
        }

        return orderItemDTO;
    }

}
