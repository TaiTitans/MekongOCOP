package com.mekongocop.mekongocopserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mekongocop.mekongocopserver.dto.CartDTO;
import com.mekongocop.mekongocopserver.dto.CartItemDTO;
import com.mekongocop.mekongocopserver.entity.Product;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.ProductRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;



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
        try{
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User Not Found"));
            String cartKey = "cart:"+userId;
            Jedis jedis = jedisPool.getResource();
            String cartData = jedis.get(cartKey);
            CartDTO cart;
            if(cartData == null){
                cart = new CartDTO();
            }else{
            cart = objectMapper.readValue(cartData, CartDTO.class);
            }
            // Lấy thông tin sản phẩm từ database
            Product product = productRepository.findById(cartItemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product Not Found"));

            // Kiểm tra số lượng tồn kho
            if (product.getProduct_quantity() < cartItemDTO.getQuantity()) {
                throw new RuntimeException("Not enough stock available for product: " + product.getProduct_name());
            }

            boolean exists = false;
            for (CartItemDTO item : cart.getCartItemList()) {
                if (item.getProductId() == cartItemDTO.getProductId()) {
                    // Nếu sản phẩm đã có trong giỏ hàng, cộng thêm số lượng
                    int newQuantity = item.getQuantity() + cartItemDTO.getQuantity();

                    // Kiểm tra xem tổng số lượng có vượt quá số lượng tồn kho
                    if (product.getProduct_quantity() < newQuantity) {
                        throw new RuntimeException("Not enough stock available for product: " + product.getProduct_name());
                    }

                    item.setQuantity(newQuantity);
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                cart.getCartItemList().add(cartItemDTO); // Thêm sản phẩm mới vào giỏ hàng
            }
            jedis.set(cartKey, objectMapper.writeValueAsString(cart));
            jedis.expire(cartKey, CART_TTL_SECONDS);

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public void removeFromCart(String token, int productId) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));
            Jedis jedis = jedisPool.getResource();
            String cartKey = "cart:" + userId;
            String cartData = jedis.get(cartKey);

            if (cartData != null) {
                CartDTO cart = objectMapper.readValue(cartData, CartDTO.class);
                cart.getCartItemList().removeIf(item -> item.getProductId() == productId);

                // Lưu lại giỏ hàng sau khi xóa sản phẩm
                jedis.set(cartKey, objectMapper.writeValueAsString(cart));

                // Gia hạn TTL sau khi cập nhật giỏ hàng
                jedis.expire(cartKey, CART_TTL_SECONDS);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateCartItemQuantity(String token, int productId, int newQuantity) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));
            Jedis jedis = jedisPool.getResource();
            String cartKey = "cart:" + userId;
            String cartData = jedis.get(cartKey);

            if (cartData == null) {
                throw new RuntimeException("Cart is empty. Cannot update quantity.");
            }

            CartDTO cart = objectMapper.readValue(cartData, CartDTO.class);
            boolean productExistsInCart = false;

            // Lấy thông tin sản phẩm từ cơ sở dữ liệu
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product Not Found"));

            // Kiểm tra số lượng tồn kho
            if (product.getProduct_quantity() < newQuantity) {
                throw new RuntimeException("Not enough stock available for product: " + product.getProduct_name());
            }

            // Duyệt qua danh sách sản phẩm trong giỏ hàng để cập nhật số lượng
            for (CartItemDTO item : cart.getCartItemList()) {
                if (item.getProductId() == productId) {
                    item.setQuantity(newQuantity);  // Cập nhật số lượng sản phẩm
                    productExistsInCart = true;
                    break;
                }
            }

            if (!productExistsInCart) {
                throw new RuntimeException("Product does not exist in the cart.");
            }

            // Lưu lại giỏ hàng sau khi cập nhật số lượng
            jedis.set(cartKey, objectMapper.writeValueAsString(cart));
            jedis.expire(cartKey, CART_TTL_SECONDS);  // Gia hạn TTL sau khi cập nhật giỏ hàng

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
            }
            jedis.expire(cartKey, CART_TTL_SECONDS);
            return cart;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void clearCart(String token) {
        try {
            // Lấy userId từ JWT token
            int userId = jwtTokenProvider.getUserIdFromToken(token);

            // Tạo khóa giỏ hàng dựa trên userId
            String cartKey = "cart:" + userId;

            // Lấy kết nối đến Redis
            Jedis jedis = jedisPool.getResource();

            // Xóa giỏ hàng khỏi Redis
            jedis.del(cartKey);

        } catch (Exception e) {
            throw new RuntimeException("Failed to clear the cart", e);
        }
    }


}
