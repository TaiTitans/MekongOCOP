package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.ProductDTO;
import com.mekongocop.mekongocopserver.dto.ProductImageDTO;
import com.mekongocop.mekongocopserver.dto.ReviewDTO;
import com.mekongocop.mekongocopserver.entity.*;
import com.mekongocop.mekongocopserver.repository.ProductRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    public static final Logger log = LoggerFactory.getLogger(ProductService.class);


    public ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProduct_id());
        productDTO.setProductName(product.getProduct_name());
        productDTO.setProductDescription(product.getProduct_description());
        productDTO.setProductPrice(product.getProduct_price());
        productDTO.setProductQuantity(product.getProduct_quantity());
        productDTO.setProvince(product.getProvince().getProvince_id());
        productDTO.setCategory(product.getProductCategory().getCategory_id());
        List<ProductImageDTO> productImageDTOs = product.getProduct_images().stream()
                .map(image -> new ProductImageDTO(image.getImage_id(), image.getImage_url(), image.getIs_primary())) // Assuming ProductImageDTO has suitable constructor
                .collect(Collectors.toList());
        productDTO.setProductImages(productImageDTOs);

        List<ReviewDTO> reviewDTOs = product.getReviews().stream()
                .map(review -> new ReviewDTO(review.getReview_id(), review.getRating(), review.getComment(), review.getCreated_at(), review.getUser_id()))
                .collect(Collectors.toList());
        productDTO.setReviews(reviewDTOs);

        return productDTO;
    }


    public Product convertToEntity(ProductDTO productDTO, Province province, ProductCategory category,Set<ProductImage> productImages, Set<Review> reviews) {
        Product product = new Product();
        product.setProduct_id(productDTO.getProductId());
        product.setProduct_name(productDTO.getProductName());
        product.setProduct_description(productDTO.getProductDescription());
        product.setProduct_price(productDTO.getProductPrice());
        product.setProduct_quantity(productDTO.getProductQuantity());

        if (province != null) {
            product.setProvince(province);
        }
        product.setProductCategory(category);
        product.setProduct_images(productImages);
        product.setReviews(reviews);

        return product;
    }




}
