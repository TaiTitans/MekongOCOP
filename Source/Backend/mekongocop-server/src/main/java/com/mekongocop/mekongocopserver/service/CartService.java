package com.mekongocop.mekongocopserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mekongocop.mekongocopserver.dto.CartDTO;
import com.mekongocop.mekongocopserver.dto.CartItemDTO;
import com.mekongocop.mekongocopserver.entity.Product;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.ProductRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;


@Service
public class CartService {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final int CART_TTL_SECONDS = 3600;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    public CartService(JedisPool jedisPool, JwtTokenProvider jwtTokenProvider) {
        this.jedisPool = jedisPool;
    }

    public void addCart(String token, CartItemDTO cartItemDTO) {
        int userId = jwtTokenProvider.getUserIdFromToken(token);
        String lockKey = "lock:cart:" + userId;

        withRedisLock(lockKey, () -> {
            try (Jedis jedis = jedisPool.getResource()) {
                String cartKey = "cart:" + userId;

                String cartData = jedis.get(cartKey);
                CartDTO cart = cartData == null || cartData.isEmpty() ? new CartDTO() : objectMapper.readValue(cartData, CartDTO.class);

                Product product = productRepository.findById(cartItemDTO.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product Not Found"));

                if (product.getProduct_quantity() < cartItemDTO.getQuantity()) {
                    throw new RuntimeException("Not enough stock available for product: " + product.getProduct_name());
                }

                boolean exists = false;
                for (CartItemDTO item : cart.getCartItemList()) {
                    if (item.getProductId() == cartItemDTO.getProductId()) {
                        int newQuantity = item.getQuantity() + cartItemDTO.getQuantity();
                        if (product.getProduct_quantity() < newQuantity) {
                            throw new RuntimeException("Not enough stock available for product: " + product.getProduct_name());
                        }
                        item.setQuantity(newQuantity);
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    cartItemDTO.setStoreId(product.getStore().getStore_id());
                    cart.getCartItemList().add(cartItemDTO);
                }

                jedis.set(cartKey, objectMapper.writeValueAsString(cart));
                jedis.expire(cartKey, CART_TTL_SECONDS);
            } catch (Exception e) {
                throw new RuntimeException("Error while adding item to cart: " + e.getMessage(), e);
            }
        });
    }





    public void removeFromCart(String token, int productId) {
        int userId = jwtTokenProvider.getUserIdFromToken(token);
        String lockKey = "lock:cart:" + userId;

        withRedisLock(lockKey, () -> {
            try (Jedis jedis = jedisPool.getResource()) {
                String cartKey = "cart:" + userId;
                String cartData = jedis.get(cartKey);

                if (cartData != null) {
                    CartDTO cart = objectMapper.readValue(cartData, CartDTO.class);
                    cart.getCartItemList().removeIf(item -> item.getProductId() == productId);

                    if (cart.getCartItemList().isEmpty()) {
                        jedis.del(cartKey); // Xóa key Redis nếu giỏ hàng rỗng
                    } else {
                        jedis.set(cartKey, objectMapper.writeValueAsString(cart));
                        jedis.expire(cartKey, CART_TTL_SECONDS);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }



    public void updateCartItemQuantity(String token, int productId, int newQuantity) {
        int userId = jwtTokenProvider.getUserIdFromToken(token);
        String lockKey = "lock:cart:" + userId;

        withRedisLock(lockKey, () -> {
            try {
                String cartKey = "cart:" + userId;
                Jedis jedis = jedisPool.getResource();
                String cartData = jedis.get(cartKey);

                if (cartData == null) {
                    throw new RuntimeException("Cart is empty. Cannot update quantity.");
                }

                CartDTO cart = objectMapper.readValue(cartData, CartDTO.class);

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product Not Found"));

                if (product.getProduct_quantity() < newQuantity) {
                    throw new RuntimeException("Not enough stock available for product: " + product.getProduct_name());
                }

                boolean productExistsInCart = false;
                for (CartItemDTO item : cart.getCartItemList()) {
                    if (item.getProductId() == productId) {
                        item.setQuantity(newQuantity);
                        productExistsInCart = true;
                        break;
                    }
                }

                if (!productExistsInCart) {
                    throw new RuntimeException("Product does not exist in the cart.");
                }

                jedis.set(cartKey, objectMapper.writeValueAsString(cart));
                jedis.expire(cartKey, CART_TTL_SECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CartDTO getCartWithProductDetails(String token) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            String cartKey = "cart:" + userId;
            Jedis jedis = jedisPool.getResource();
            String cartData = jedis.get(cartKey);

            if (cartData == null) {
                return new CartDTO(); // Giỏ hàng trống
            }

            CartDTO cart = objectMapper.readValue(cartData, CartDTO.class);

            // Truy vấn thông tin sản phẩm cho mỗi item trong giỏ hàng
            for (CartItemDTO item : cart.getCartItemList()) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product Not Found"));

                // Gán thêm thông tin sản phẩm cho từng item
                item.setProductName(product.getProduct_name());
                item.setPrice(product.getProduct_price());
                item.setStoreId(product.getStore().getStore_id());
            }
            jedis.expire(cartKey, CART_TTL_SECONDS);
            return cart;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void clearCart(String token) {
        int userId = jwtTokenProvider.getUserIdFromToken(token);
        String cartKey = "cart:" + userId;

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(cartKey); // Xóa hoàn toàn key Redis
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear the cart", e);
        }
    }

    public void withRedisLock(String lockKey, Runnable action) {
        String uniqueToken = UUID.randomUUID().toString(); // Token duy nhất cho mỗi khóa
        int timeout = 5000; // 5 giây
        long startTime = System.currentTimeMillis();

        try (Jedis jedis = jedisPool.getResource()) {
            while (true) {
                if (jedis.setnx(lockKey, uniqueToken) == 1) {
                    jedis.expire(lockKey, 10); // Đặt TTL cho khóa (10 giây)
                    try {
                        action.run(); // Thực thi hành động
                    } finally {
                        // Chỉ xóa khóa nếu token khớp
                        String currentValue = jedis.get(lockKey);
                        if (uniqueToken.equals(currentValue)) {
                            jedis.del(lockKey);
                        }
                    }
                    break;
                } else {
                    // Kiểm tra timeout
                    if (System.currentTimeMillis() - startTime > timeout) {
                        throw new RuntimeException("Could not acquire lock on cart. Please try again later.");
                    }
                    Thread.sleep(50); // Chờ 50ms trước khi thử lại
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted while trying to acquire Redis lock.");
        }
    }




}
