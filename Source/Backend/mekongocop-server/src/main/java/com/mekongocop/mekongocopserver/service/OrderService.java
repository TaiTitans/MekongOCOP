package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.*;
import com.mekongocop.mekongocopserver.entity.*;
import com.mekongocop.mekongocopserver.entity.voucher.Voucher;
import com.mekongocop.mekongocopserver.entity.voucher.VoucherUsers;
import com.mekongocop.mekongocopserver.repository.*;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    public static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private UserNotificationRepository notificationRepository;
    @Autowired
    private VietQRService vietQRService;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private VoucherUsersRepository voucherUsersRepository;

    public OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrder_id(order.getOrder_id());
        orderDTO.setTotal_price(order.getTotal_price());
        orderDTO.setPayment(order.getPayment());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setAddress(order.getAddress());
        orderDTO.setShip(order.getShip());
        orderDTO.setQr_code_url(order.getQr_code_url());
        orderDTO.setCreated_at(order.getCreated_at());
        orderDTO.setUpdated_at(order.getUpdated_at());

        // Chuyển đổi danh sách OrderItem entity sang DTO
        orderDTO.setItems(order.getItems().stream()
                .map(orderItemService::convertOrderItemEntityToDTO)
                .collect(Collectors.toList()));

        return orderDTO;
    }

    public Order convertToEntity(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrder_id(orderDTO.getOrder_id());
        order.setTotal_price(orderDTO.getTotal_price());
        order.setPayment(orderDTO.getPayment());
        order.setStatus(orderDTO.getStatus());
        order.setAddress(orderDTO.getAddress());
        order.setShip(orderDTO.getShip());
        order.setQr_code_url(orderDTO.getQr_code_url());
        order.setCreated_at(orderDTO.getCreated_at());
        order.setUpdated_at(orderDTO.getUpdated_at());

        // Lấy user từ user_id và gán vào order
        User user = userRepository.findById(orderDTO.getUser_id())
                .orElseThrow(() -> new RuntimeException("User not found"));
        order.setUser(user);  // Gán User vào Order

        // Chuyển đổi danh sách OrderItemDTO sang entity
        order.setItems(orderDTO.getItems().stream()
                .map(orderItemDTO -> orderItemService.convertOrderItemDTOToEntity(orderItemDTO, order, productRepository.findById(orderItemDTO.getProductId()).orElseThrow()))
                .collect(Collectors.toList()));

        return order;
    }





    // Lấy tất cả đơn hàng của 1 người dùng
    public List<OrderDTO> getAllOrdersByUserId(String token) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            List<Order> orders = orderRepository.findByUserId(userId);
            return orders.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // Lấy đơn hàng cụ thể của người dùng từ token
    public OrderDTO getOrderById(String token, int orderId) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token); // Lấy userId từ token

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Kiểm tra xem đơn hàng có thuộc về người dùng không
            if (order.getUser().getUser_id() != userId) {
                throw new RuntimeException("You do not have permission to view this order");
            }

            return convertToDTO(order);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public List<OrderDTO> getOrderByStore(String token){
        try {
            // Lấy userId từ token
            int userId = jwtTokenProvider.getUserIdFromToken(token);

            // Tìm kiếm Store dựa trên userId
            Optional<Store> storeOptional = storeRepository.findByUserId(userId);

            if (storeOptional.isPresent()) {
                // Lấy storeEntity từ Optional
                Store storeEntity = storeOptional.get();

                // Lấy danh sách Order dựa trên store_id
                List<Order> orders = orderRepository.findOrdersByStoreId(storeEntity.getStore_id());

                // Chuyển đổi từ Order sang OrderDTO
                return orders.stream()
                        .map(this::convertToDTO) // Hàm chuyển đổi từ Order sang OrderDTO
                        .collect(Collectors.toList());
            } else {
                throw new RuntimeException("Store not found for the user.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving orders: " + e.getMessage(), e);
        }
    }


// ORDER========================================================================================================
        private boolean isVoucherApplicable(Voucher voucher, BigDecimal totalPrice, int userId) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate = voucher.getStart_date();
            LocalDateTime endDate = voucher.getEnd_date();

            log.info("Current time: " + now);
            log.info("Voucher start time: " + startDate);
            log.info("Voucher end time: " + endDate);
            log.info("Voucher minimum spend: " + voucher.getMind_spend());
            log.info("Total price: " + totalPrice);

            // Kiểm tra thời gian và tổng giá trị đơn hàng
            boolean isApplicable = !now.isBefore(startDate) && !now.isAfter(endDate) && totalPrice.compareTo(BigDecimal.valueOf(voucher.getMind_spend())) >= 0;

            // Kiểm tra số lần sử dụng của người dùng
            long usageCount = voucher.getVoucherUsers().stream()
                    .filter(vu -> vu.getUser().getUser_id() == userId)
                    .mapToLong(VoucherUsers::getUsage_count)
                    .sum();

            log.info("User usage count: " + usageCount);
            log.info("User limit: " + voucher.getUser_limit());
            log.info("Total limit: " + voucher.getTotal_limit());

            isApplicable = isApplicable && usageCount < voucher.getUser_limit() && voucher.getTotal_limit() > 0;

            log.info("Is voucher applicable: " + isApplicable);

            if (now.isBefore(startDate)) {
                log.warn("Voucher is not yet applicable.");
            } else if (now.isAfter(endDate)) {
                log.warn("Voucher has expired.");
            } else if (totalPrice.compareTo(BigDecimal.valueOf(voucher.getMind_spend())) < 0) {
                log.warn("Total price does not meet the minimum spend requirement.");
            } else if (usageCount >= voucher.getUser_limit()) {
                log.warn("User has reached the usage limit for this voucher.");
            } else if (voucher.getTotal_limit() <= 0) {
                log.warn("Voucher has reached the total usage limit.");
            }

            return isApplicable;
        }

    @Transactional
    public OrderDTO createOrder(String token, String address, String paymentMethod, String voucherCode) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            CartDTO cart = cartService.getCartWithProductDetails(token);
            if (cart == null || cart.getCartItemList().isEmpty()) {
                throw new RuntimeException("Cart is empty");
            }

            BigDecimal totalPrice = calculateTotalPrice(cart.getCartItemList());

            // Áp dụng mã giảm giá voucher
            if (voucherCode != null && !voucherCode.isEmpty()) {
                Voucher voucher = voucherRepository.findByCode(voucherCode)
                        .orElseThrow(() -> new RuntimeException("Invalid voucher code"));
                log.info("Voucher details: " + voucher.toString());
                if ("ACTIVE".equalsIgnoreCase(voucher.getStatus()) && isVoucherApplicable(voucher, totalPrice, userId)) {
                    totalPrice = applyVoucherDiscount(totalPrice, voucher); // Cập nhật lại totalPrice sau khi áp dụng giảm giá

                    // Trừ đi total_limit của voucher
                    voucher.setTotal_limit(voucher.getTotal_limit() - 1);

                    // Lưu thông tin vào bảng voucher_users
                    VoucherUsers voucherUsers = voucher.getVoucherUsers().stream()
                            .filter(vu -> vu.getUser().getUser_id() == userId)
                            .findFirst()
                            .orElse(new VoucherUsers());
                    voucherUsers.setVoucher(voucher);
                    voucherUsers.setUser(user);
                    voucherUsers.setUsage_count(voucherUsers.getUsage_count() + 1);
                    voucherRepository.save(voucher);
                    voucherUsersRepository.save(voucherUsers);
                } else {
                    log.warn("Voucher status: " + voucher.getStatus());
                    throw new RuntimeException("Voucher is not applicable or inactive");
                }
            }

            // Cộng phí ship sau khi áp dụng voucher
            BigDecimal shipFee = BigDecimal.valueOf(30000);
            totalPrice = totalPrice.add(shipFee);

            // Khởi tạo orderDTO với total_price sau khi đã áp dụng giảm giá và cộng ship
            OrderDTO orderDTO = createOrderDTO(userId, cart, totalPrice);
            orderDTO.setAddress(address);
            orderDTO.setPayment(paymentMethod);
            orderDTO.setShip(shipFee);

            // Tạo đơn hàng trước khi xử lý thanh toán
            Order order = createOrder(orderDTO, user);
            orderDTO.setOrder_id(order.getOrder_id());

            // Xử lý thanh toán và cập nhật trạng thái, qr_code_url
            processPayment(orderDTO, order);

            // Xóa giỏ hàng trong Redis sau khi tạo đơn hàng
            cartService.clearCart(token);

            return orderDTO;
        } catch (Exception e) {
            log.error("Error creating order: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private OrderDTO createOrderDTO(int userId, CartDTO cart, BigDecimal totalPrice) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setShip(BigDecimal.valueOf(30000));
        orderDTO.setUser_id(userId);
        orderDTO.setTotal_price(totalPrice);
        orderDTO.setItems(cart.getCartItemList().stream().map(cartItem -> {
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            orderItemDTO.setProductId(cartItem.getProductId());
            orderItemDTO.setQuantity(cartItem.getQuantity());
            orderItemDTO.setPrice(cartItem.getPrice());
            return orderItemDTO;
        }).collect(Collectors.toList()));
        orderDTO.setCreated_at(new Timestamp(new Date().getTime()));
        orderDTO.setUpdated_at(new Timestamp(new Date().getTime()));
        return orderDTO;
    }


        private BigDecimal applyVoucherDiscount(BigDecimal totalPrice, Voucher voucher) {
            if ("Percentage".equalsIgnoreCase(voucher.getType())) {
                // Áp dụng giảm giá phần trăm
                BigDecimal discount = totalPrice.multiply(BigDecimal.valueOf(voucher.getDiscount_value())).divide(BigDecimal.valueOf(100));
                totalPrice = totalPrice.subtract(discount);
            } else if ("Fixed".equalsIgnoreCase(voucher.getType())) {
                // Áp dụng giảm giá cố định
                totalPrice = totalPrice.subtract(BigDecimal.valueOf(voucher.getDiscount_value()));
            }

            // Đảm bảo totalPrice không âm
            if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
                totalPrice = BigDecimal.ZERO;
            }

            return totalPrice;
        }


        private Order createOrder(OrderDTO orderDTO, User user) {
            Order order = convertToEntity(orderDTO);
            order.setUser(user);
            order.setTotal_price(orderDTO.getTotal_price());
            order.setStatus("Request");
            orderRepository.save(order);
            orderItemService.saveAll(order.getItems());
            return order;
        }


        private void processPayment(OrderDTO orderDTO, Order order) {
            if (orderDTO.getPayment().equals("Cash")) {
                order.setStatus("Request");
            } else if (orderDTO.getPayment().equals("VietQR")) {
                String qrCodeUrl = vietQRService.processVietQRPayment(orderDTO.getTotal_price(), "PHAN PHAT TAI", "0349413880", "Thanh toan MekongOCOP "+ orderDTO.getOrder_id());
                if (qrCodeUrl != null) {
                    order.setStatus("Request");
                    order.setQr_code_url(qrCodeUrl);
                } else {
                    throw new RuntimeException("VietQR payment failed.");
                }
            } else {
                throw new IllegalArgumentException("Invalid payment method: " + orderDTO.getPayment());
            }
            orderRepository.save(order);
            orderDTO.setQr_code_url(order.getQr_code_url());
            orderDTO.setStatus(order.getStatus());
        }
        private BigDecimal calculateTotalPrice(List<CartItemDTO> cartItems) {
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (CartItemDTO cartItem : cartItems) {
                BigDecimal itemTotalPrice = cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
                totalPrice = totalPrice.add(itemTotalPrice);
            }
            return totalPrice;
        }

// END ===========================================================================================================

    @Transactional
    public OrderDTO updateOrderStatusToPending(int orderId) {
        try {
            // Lấy đơn hàng theo orderId
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Kiểm tra trạng thái hiện tại có hợp lệ để chuyển sang Pending không
            if (!order.getStatus().equals("Request")) {
                throw new RuntimeException("Order status is not valid for this update");
            }

            // Cập nhật trạng thái đơn hàng
            order.setStatus("Pending");

            // Trừ số lượng sản phẩm tương ứng
            for (OrderItem orderItem : order.getItems()) {
                Product product = orderItem.getProduct();
                if (product.getProduct_quantity() < orderItem.getQuantity()) {
                    throw new RuntimeException("Not enough quantity in stock for product: " + product.getProduct_name());
                }
                // Trừ số lượng sản phẩm
                product.setProduct_quantity(product.getProduct_quantity() - orderItem.getQuantity());
                productRepository.save(product);
            }

            // Cập nhật đơn hàng
            orderRepository.save(order);

            // Gửi thông báo cho người dùng
            sendOrderStatusNotification(order.getUser().getUser_id(), order.getOrder_id(), "Đơn hàng {order_id} đang được giao.");

            // Trả về DTO sau khi cập nhật
            return convertToDTO(order);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void sendOrderStatusNotification(int userId, int orderId, String message) {
        UserNotification notification = new UserNotification();
        notification.setUserId(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
        notification.setMessage(message.replace("{order_id}", String.valueOf(orderId)));
        notification.setSent_at(LocalDateTime.now());
        notificationRepository.save(notification);
    }
    @Transactional
    public OrderDTO updateOrderStatusToCancel(int orderId) {
        try {
            // Lấy đơn hàng theo orderId
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Cập nhật trạng thái đơn hàng
            order.setStatus("Cancel");

            // Lưu đơn hàng sau khi cập nhật
            orderRepository.save(order);

            // Trả về DTO sau khi cập nhật
            return convertToDTO(order);
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
    @Transactional
    public OrderDTO updateOrderStatusToSuccess(String token, int orderId) {
        try {
            // Lấy userId từ token
            int userId = jwtTokenProvider.getUserIdFromToken(token);

            // Tìm đơn hàng theo userId và orderId
            Order order = orderRepository.findByUserIdAndOrderId(userId, orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found or user does not have permission"));

            // Kiểm tra nếu trạng thái hiện tại là "Pending" thì mới cho phép cập nhật
            if (!"Pending".equals(order.getStatus())) {
                throw new RuntimeException("Order status must be 'Pending' to update to 'Success'");
            }

            // Cập nhật trạng thái đơn hàng
            order.setStatus("Success");

            // Lưu đơn hàng sau khi cập nhật
            orderRepository.save(order);

            // Chuyển đổi sang DTO và trả về
            return convertToDTO(order);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update order status", e);
        }
    }



    @Transactional
    public OrderDTO requestOrderCancellation(int orderId) {
        try{
        // Lấy đơn hàng theo orderId
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Kiểm tra trạng thái hiện tại có thể yêu cầu hủy không
        if (!order.getStatus().equals("Request")) {
            throw new RuntimeException("Order status is not valid for cancellation request");
        }

        // Cập nhật trạng thái đơn hàng thành Cancel_Request
        order.setStatus("Cancel_Request");

        // Lưu đơn hàng sau khi cập nhật
        orderRepository.save(order);

        // Trả về DTO sau khi cập nhật
        return convertToDTO(order);
    }catch (Exception e){

        throw new RuntimeException(e);
        }
    }


    public Long getTotalOrder(){
        return orderRepository.countOrders();
    }
    public BigDecimal getTotalRevenueThisMonth() {
        return orderRepository.totalRevenueThisMonth();
    }

    public BigDecimal getTotalRevenueToday() {
        return orderRepository.totalRevenueToday();
    }

    public BigDecimal getTotalRevenueThisYear() {
        return orderRepository.totalRevenueThisYear();
    }
}

