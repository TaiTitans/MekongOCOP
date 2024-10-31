package com.mekongocop.mekongocopserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mekongocop.mekongocopserver.dto.*;
import com.mekongocop.mekongocopserver.entity.*;
import com.mekongocop.mekongocopserver.repository.*;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import jakarta.transaction.Transactional;
import jdk.jfr.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.math.BigDecimal;
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

    @Autowired
    private JedisPool jedisPool;
    public ProductDTO convertJsonToDTO(String json)throws IOException {
        return objectMapper.readValue(json, ProductDTO.class);
    }
    public static final Logger log = LoggerFactory.getLogger(ProductService.class);


    // Chuyển đổi từ List<ProductDTO> sang JSON
    private String convertToJson(List<ProductDTO> productDTOs) {
        // Sử dụng ObjectMapper hoặc Gson để chuyển đổi
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(productDTOs);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Chuyển đổi từ JSON sang List<ProductDTO>
    private List<ProductDTO> convertFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, new TypeReference<List<ProductDTO>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();

        productDTO.setProductId(product.getProduct_id());
        productDTO.setProductName(product.getProduct_name());
        productDTO.setProductDescription(product.getProduct_description());
        productDTO.setProductPrice(product.getProduct_price());
        productDTO.setProductQuantity(product.getProduct_quantity());

        // Ánh xạ province_id
        Province province = product.getProvince();
        if (province != null) {
            productDTO.setProvinceId(province.getProvince_id());
            productDTO.setProvinceName(province.getProvince_name());
        }


        // Ánh xạ ProductCategory thành ProductCategoryDTO
        ProductCategory category = product.getProductCategory();
        if (category != null) {
            productDTO.setCategoryId(category.getCategory_id());  // Trả về category_id
            productDTO.setCategoryName(category.getCategory_name());  // Trả về category_name trong API GET
        } // Thay vì chỉ category_id

        // Ánh xạ store_id
        productDTO.setStore(product.getStore().getStore_id());

        // Ánh xạ danh sách hình ảnh
        List<ProductImageDTO> productImageDTOs = product.getProduct_images().stream()
                .map(image -> new ProductImageDTO(image.getImage_id(), image.getImage_url(), image.getIs_primary()))
                .collect(Collectors.toList());
        productDTO.setProductImages(productImageDTOs);

        // Ánh xạ danh sách đánh giá (reviews)
        List<ReviewDTO> reviewDTOs = product.getReviews().stream()
                .map(review -> new ReviewDTO(review.getReview_id(), review.getRating(), review.getComment(), review.getCreated_at(), review.getUser_id().getUser_id(), review.getUser_id().getUsername(),review.getProduct().getProduct_id()))
                .collect(Collectors.toList());
        productDTO.setReviews(reviewDTOs);

        return productDTO;
    }



    public Product convertToEntity(ProductDTO productDTO, Province province, ProductCategory category, Set<ProductImage> productImages, Set<Review> reviews, Store store) {
        Product product = new Product();

        product.setProduct_id(productDTO.getProductId());
        product.setProduct_name(productDTO.getProductName());
        product.setProduct_description(productDTO.getProductDescription());
        product.setProduct_price(productDTO.getProductPrice());
        product.setProduct_quantity(productDTO.getProductQuantity());

        // Ánh xạ province nếu tồn tại
        if (province != null) {
            product.setProvince(province);
        }

        // Ánh xạ category từ ProductDTO
        if (category != null) {
            product.setProductCategory(category);
        }

        // Ánh xạ store
        product.setStore(store);

        // Ánh xạ danh sách hình ảnh
        product.setProduct_images(productImages);

        // Ánh xạ danh sách đánh giá (reviews)
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

                Province province = provinceRepository.findById(productDTO.getProvinceId())
                        .orElseThrow(() -> new RuntimeException("Province not found"));
                ProductCategory category = productCategoryRepository.findById(productDTO.getCategoryId())
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

                Province province = provinceRepository.findById(productDTO.getProvinceId())
                        .orElseThrow(() -> new RuntimeException("Province not found"));
                ProductCategory category = productCategoryRepository.findById(productDTO.getCategoryId())
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

            // Lấy danh sách các ProductImage liên quan đến sản phẩm
            List<ProductImage> productImages = productImageRepository.findByProduct(product);

            // Xóa các ProductImage và hình ảnh trên Cloudinary
            for (ProductImage productImage : productImages) {
                cloudinaryService.deleteProductImage(productImage.getImage_url());
                productImageRepository.delete(productImage);
            }

            // Xóa sản phẩm
            productRepository.delete(product);
        } else {
            log.warn("Store not found");
        }
    }

    private void updateProductImage(Product product, List<MultipartFile> images, int userId, int productId) {
        try {
            // Lấy danh sách các ProductImage cũ
            List<ProductImage> oldProductImages = productImageRepository.findByProduct(product);

            // Xóa các ProductImage cũ
            productImageRepository.deleteByProduct(product);

            // Xóa các hình ảnh cũ trên Cloudinary
            for (ProductImage oldProductImage : oldProductImages) {
                cloudinaryService.deleteProductImage(oldProductImage.getImage_url());
            }

            // Tạo và liên kết các ProductImage mới với Product
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

                // Chỉ có hình ảnh đầu tiên là chính
                isPrimary = false;
            }

            // Lưu các ProductImage mới
            productImageRepository.saveAll(productImages);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void updatePrice(int productId, BigDecimal newPrice, String token) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<Store> storeOptional = storeRepository.findByUserId(userId);
            if (storeOptional.isPresent()) {
                Store store = storeOptional.get();

                // Tìm sản phẩm cần cập nhật
                Product product = productRepository.findByIdAndStore(productId, store)
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                product.setProduct_price(newPrice);
                productRepository.save(product);
            } else {
                log.warn("Store not found");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void updateQuantity(int productId, int quantity, String token) {
        try{
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<Store> storeOptional = storeRepository.findByUserId(userId);
            if (storeOptional.isPresent()) {
                Store store = storeOptional.get();
                Product product = productRepository.findByIdAndStore(productId, store).orElseThrow(()->new RuntimeException("Product not found"));
                product.setProduct_quantity(quantity);
                productRepository.save(product);
            }else{
                log.warn("Store not found");
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<ProductDTO> getNewFeedProduct() {
        try {
            Jedis jedis = jedisPool.getResource();
            String key = "newfeed_product";

            String valueKey = jedis.get("newfeed_product");
            if(valueKey != null){

                return convertFromJson(valueKey);
            }
            Pageable pageable = PageRequest.of(0, 10); // Lấy 10 sản phẩm mới nhất
            List<Product> products = productRepository.findTop10Products(pageable);


            List<ProductDTO> productDTOs = products.stream().map(this::convertToDTO).collect(Collectors.toList());

            String jsonProductsDTO = convertToJson(productDTOs);
            jedis.set(key, jsonProductsDTO);
            jedis.expire(key, 3600);
            return productDTOs;


        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<ProductDTO> getAllProductsOfStore(int storeId) {
        try {
            Optional<Store> storeOptional = storeRepository.findById(storeId);
            if (storeOptional.isPresent()) {
                List<Product> products = productRepository.findAllByStoreId(storeId);
                // Chuyển đổi từ List<Product> sang List<ProductDTO>
                return products.stream()
                        .map(this::convertToDTO) // Chuyển từng Product thành ProductDTO
                        .collect(Collectors.toList());
            } else {
                log.warn("Store not found");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProductDTO> getAllProductOfCategory(int categoryId) {
        try {
            Optional<ProductCategory> categoryOptional = productCategoryRepository.findById(categoryId);
            if (categoryOptional.isPresent()) {
                List<Product> products = productRepository.findAllByCategoryId(categoryId);
                return products.stream().map(this::convertToDTO).collect(Collectors.toList());
            } else {
                log.warn("Category not found");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProductDTO> getAllProductOfProvince(int provinceId) {
        try {
            Optional<Province> provinceOptional = provinceRepository.findById(provinceId);
            if (provinceOptional.isPresent()) {
                List<Product> products = productRepository.findAllByProvinceId(provinceId);
                return products.stream().map(this::convertToDTO).collect(Collectors.toList());
            } else {
                log.warn("Province not found");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ProductDTO getProductById(int productId) {
        try {
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                // Sử dụng hàm convertToDTO để chuyển đổi Product thành ProductDTO
                return convertToDTO(product);
            } else {
                log.warn("Product not found with id: " + productId);
                return null;
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching product with id: " + productId, e);
            throw new RuntimeException(e);
        }
    }

    public Long getAllProduct(){
        return productRepository.countAllProduct();
    }

    public List<ProductDTO> getAllProductList(){
        List<Product> products = productRepository.findAll();
        return convertProductListToProductDTOList(products);
    }

    private List<ProductDTO> convertProductListToProductDTOList(List<Product> products) {
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Page<ProductDTO> searchProducts(String productName, int page, int size) {
        String redisKey = "product_search::" + productName.toLowerCase() + "::page::" + page + "::size::" + size;

        // Try to get the product list from Redis cache
        try (Jedis jedis = jedisPool.getResource()) {
            String cachedProducts = jedis.get(redisKey);
            if (cachedProducts != null) {
                return deserializeProductDTOPage(cachedProducts, page, size);  // Thay đổi phần deserialization
            }
        }

        // Fetch từ database nếu không có trong Redis
        Page<Product> productsPage = productRepository.findByProductNameContainingIgnoreCase(productName, PageRequest.of(page, size));
        List<ProductDTO> productDTOList = productsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Serialize sản phẩm thành JSON và lưu vào Redis
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(redisKey, 3600, serializeProductDTOList(productDTOList));  // Lưu dạng list thay vì page
        }

        return new PageImpl<>(productDTOList, PageRequest.of(page, size), productsPage.getTotalElements());
    }

    // Serialize ProductDTO list to JSON for Redis
    private String serializeProductDTOList(List<ProductDTO> productDTOList) {
        try {
            return new ObjectMapper().writeValueAsString(productDTOList);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing product DTO list", e);
        }
    }

    // Deserialize list và tái tạo Page
    private Page<ProductDTO> deserializeProductDTOPage(String data, int page, int size) {
        try {
            List<ProductDTO> productDTOList = new ObjectMapper().readValue(data, new TypeReference<List<ProductDTO>>() {});
            return new PageImpl<>(productDTOList, PageRequest.of(page, size), productDTOList.size());
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing product DTO list", e);
        }
    }

}
