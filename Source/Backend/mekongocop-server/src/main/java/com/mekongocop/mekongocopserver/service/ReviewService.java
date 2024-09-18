package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.ReviewDTO;
import com.mekongocop.mekongocopserver.entity.Product;
import com.mekongocop.mekongocopserver.entity.Review;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.ProductRepository;
import com.mekongocop.mekongocopserver.repository.ReviewRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public static final Logger log = LoggerFactory.getLogger(Review.class);

    public ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();

        // Gán giá trị từ entity sang DTO
        dto.setReviewId(review.getReview_id());
        dto.setRating(review.getRating());
        dto.setReviewContent(review.getComment());

        dto.setCreatedAt(review.getCreated_at());

        User user = review.getUser_id();
        if (user != null) {
            dto.setUser_id(user.getUser_id());
            dto.setUserName(user.getUsername());
        }

        dto.setProductId(review.getProduct().getProduct_id());


        return dto;
    }

    public Review convertToEntity(ReviewDTO dto, Product product, User user) throws ParseException {
        Review review = new Review();

        // Gán giá trị từ DTO sang entity
        review.setReview_id(dto.getReviewId());
        review.setRating(dto.getRating());
        review.setComment(dto.getReviewContent());

        // Chuyển đổi định dạng ngày tháng
        review.setCreated_at(dto.getCreatedAt());

        // Gán product và user vào review
        review.setProduct(product);
        review.setUser_id(user);

        return review;
    }

    public ReviewDTO addReview(ReviewDTO dto, int productId, String token) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<User> user = userRepository.findById(userId);
            if (!user.isPresent()) {
                throw new RuntimeException("User not found");
            }
            User userResult = user.get();
            Optional<Product> product = productRepository.findById(productId);
            if (!product.isPresent()) {

                throw new RuntimeException("Product not found");
            }
            Optional<Review> existingReview = reviewRepository.findReviewByUserAndProduct(userId, productId);
            if (existingReview.isPresent()) {
                log.error("Review already exists");
                return null;
            }


            Product productResult = product.get();


            dto.setProductId(productResult.getProduct_id());
            dto.setUser_id(userId);
            dto.setCreatedAt(new Date());

            Review review = convertToEntity(dto, productResult, userResult);
            Review savedReview = reviewRepository.save(review);
           return convertToDTO(savedReview);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ReviewDTO updateReview(ReviewDTO dto, int productId, String token) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);

            // Tìm người dùng
            Optional<User> user = userRepository.findById(userId);
            if (!user.isPresent()) {
                throw new RuntimeException("User not found");
            }
            User userResult = user.get();

            // Tìm sản phẩm
            Optional<Product> product = productRepository.findById(productId);
            if (!product.isPresent()) {
                throw new RuntimeException("Product not found");
            }
            Product productResult = product.get();

            // Tìm review hiện có của người dùng cho sản phẩm
            Optional<Review> existingReview = reviewRepository.findReviewByUserAndProduct(userId, productId);
            if (!existingReview.isPresent()) {
              return null;
            }

            // Review đã tồn tại, tiến hành cập nhật
            Review reviewToUpdate = existingReview.get();
            reviewToUpdate.setRating(dto.getRating()); // Cập nhật rating
            reviewToUpdate.setComment(dto.getReviewContent()); // Cập nhật nội dung đánh giá
            reviewToUpdate.setCreated_at(new Date()); // Cập nhật thời gian chỉnh sửa

            // Lưu review đã cập nhật
            Review updatedReview = reviewRepository.save(reviewToUpdate);

            // Trả về DTO của review đã cập nhật
            return convertToDTO(updatedReview);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void deleteReview(int productId, String token) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);

            // Tìm người dùng
            Optional<User> user = userRepository.findById(userId);
            if (!user.isPresent()) {
                throw new RuntimeException("User not found");
            }

            // Tìm sản phẩm
            Optional<Product> product = productRepository.findById(productId);
            if (!product.isPresent()) {
                throw new RuntimeException("Product not found");
            }

            // Tìm review hiện có của người dùng cho sản phẩm
            Optional<Review> existingReview = reviewRepository.findReviewByUserAndProduct(userId, productId);
            if (!existingReview.isPresent()) {
                throw new RuntimeException("Review not found");
            }

            // Xóa review
            reviewRepository.delete(existingReview.get());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

