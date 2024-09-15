package com.mekongocop.mekongocopserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mekongocop.mekongocopserver.dto.ProductDTO;
import com.mekongocop.mekongocopserver.dto.ProductImageDTO;
import com.mekongocop.mekongocopserver.dto.ReviewDTO;
import com.mekongocop.mekongocopserver.entity.*;
import com.mekongocop.mekongocopserver.repository.*;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Qualifier("objectMapper")
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private ProductImageRepository productImageRepository;

    public ProductDTO convertJsonToDTO(String json)throws IOException {
        return objectMapper.readValue(json, ProductDTO.class);
    }
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
        productDTO.setStore(product.getStore().getStore_id());
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


    public Product convertToEntity(ProductDTO productDTO, Province province, ProductCategory category,Set<ProductImage> productImages, Set<Review> reviews, Store store) {
        Product product = new Product();
        product.setProduct_id(productDTO.getProductId());
        product.setProduct_name(productDTO.getProductName());
        product.setProduct_description(productDTO.getProductDescription());
        product.setProduct_price(productDTO.getProductPrice());
        product.setProduct_quantity(productDTO.getProductQuantity());

        if (province != null) {
            product.setProvince(province);
        }
        product.setStore(store);
        product.setProductCategory(category);
        product.setProduct_images(productImages);
        product.setReviews(reviews);

        return product;
    }

    @Transactional
    @Async
    public void addProduct(String productJson, List<MultipartFile> images, String token) {
        try {
            ProductDTO productDTO = objectMapper.readValue(productJson, ProductDTO.class);
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<Store> storeOptional = storeRepository.findByUserId(userId);

            if (storeOptional.isPresent()) {
                Store store = storeOptional.get();

                Province province = provinceRepository.findById(productDTO.getProvince())
                        .orElseThrow(() -> new RuntimeException("Province not found"));
                ProductCategory category = productCategoryRepository.findById(productDTO.getCategory())
                        .orElseThrow(() -> new RuntimeException("Category not found"));

                // Tạo đối tượng Product với các thuộc tính cần thiết
                Product product = convertToEntity(productDTO, province, category, new HashSet<>(), new HashSet<>(), store);

                // Lưu Product trước để tạo ID
                Product savedProduct = productRepository.save(product);

                // Tạo và liên kết các ProductImage với Product
                List<ProductImage> productImages = new ArrayList<>();
                boolean isPrimary = true;
                for (MultipartFile image : images) {
                    Map<String, Object> uploadResult = cloudinaryService.uploadProductImage(image, String.valueOf(userId), String.valueOf(store.getStore_id()), String.valueOf(savedProduct.getProduct_id()));
                    String imageUrl = uploadResult.get("url").toString();

                    ProductImage productImage = new ProductImage();
                    productImage.setImage_url(imageUrl);
                    productImage.setIs_primary(isPrimary);
                    productImage.setProduct(savedProduct);

                    productImages.add(productImage);

                    // Chỉ có hình ảnh đầu tiên là chính
                    isPrimary = false;
                }

                // Lưu các ProductImage đã liên kết với Product
                productImageRepository.saveAll(productImages);
            } else {
                log.warn("Store not found");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Async
    public void updateProduct(int productId, String productJson, List<MultipartFile> images, String token) {
        try {
            ProductDTO productDTO = objectMapper.readValue(productJson, ProductDTO.class);
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<Store> storeOptional = storeRepository.findByUserId(userId);

            if (storeOptional.isPresent()) {
                Store store = storeOptional.get();

                // Tìm sản phẩm cần cập nhật
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                // Cập nhật thông tin sản phẩm
                product.setProduct_name(productDTO.getProductName());
                product.setProduct_description(productDTO.getProductDescription());
                product.setProduct_price(productDTO.getProductPrice());
                product.setProduct_quantity(productDTO.getProductQuantity());

                Province province = provinceRepository.findById(productDTO.getProvince())
                        .orElseThrow(() -> new RuntimeException("Province not found"));
                ProductCategory category = productCategoryRepository.findById(productDTO.getCategory())
                        .orElseThrow(() -> new RuntimeException("Category not found"));

                product.setProvince(province);
                product.setProductCategory(category);

                // Cập nhật các ProductImage
                updateProductImage(product, images, userId, product.getProduct_id());

                // Lưu thay đổi vào cơ sở dữ liệu
                productRepository.save(product);
            } else {
                log.warn("Store not found");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteProduct(int productId, String token) {
        int userId = jwtTokenProvider.getUserIdFromToken(token);
        Optional<Store> storeOptional = storeRepository.findByUserId(userId);

        if (storeOptional.isPresent()) {
            Store store = storeOptional.get();

            // Tìm sản phẩm cần xóa
            Product product = productRepository.findByIdAndStore(productId, store)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Xóa các ProductImage liên quan
            productImageRepository.deleteByProduct(product);

            // Xóa sản phẩm
            productRepository.delete(product);
        } else {
            log.warn("Store not found");
        }
    }

    private void updateProductImage(Product product, List<MultipartFile> images, int userId, int productId) {

        try {
            productImageRepository.deleteByProduct(product);

            List<ProductImage> productImages = new ArrayList<>();
            boolean isPrimary = true;
            for (MultipartFile image : images) {
                Map<String, Object> uploadResult = cloudinaryService.uploadProductImage(image, String.valueOf(userId), String.valueOf(productId), String.valueOf(product.getProduct_id()));
                String imageUrl = uploadResult.get("url").toString();
                ProductImage productImage = new ProductImage();
                productImage.setImage_url(imageUrl);
                productImage.setIs_primary(isPrimary);
                productImage.setProduct(product);
                productImages.add(productImage);
                isPrimary = false;
            }
            productImageRepository.saveAll(productImages);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }






}
