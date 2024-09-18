package com.mekongocop.mekongocopserver.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    public CloudinaryService(@Value("${CLOUDINARY_URL}") String cloudinaryUrl) {
        this.cloudinary = new Cloudinary(cloudinaryUrl);
        logger.info("Cloudinary initialized with URL: {}", cloudinaryUrl);
    }
    @Async
    public Map<String, Object> uploadImage(MultipartFile file, String userId) throws IOException {
        String folderPath = "mekongocop/" + userId;
        logger.info("Attempting to upload image for user {} to folder {}", userId, folderPath);

        try {
            Map<String, Object> params = ObjectUtils.asMap(
                    "folder", folderPath,
                    "resource_type", "auto"
            );
            Map<String, Object> result = this.cloudinary.uploader().upload(file.getBytes(), params);
            logger.info("Image uploaded successfully. Result: {}", result);
            return result;
        } catch (IOException e) {
            logger.error("Failed to upload image for user {}", userId, e);
            throw e;
        }
    }

    @Async
    public Map<String, Object> uploadProductImage(MultipartFile file, String userId, String storeId, String productId) throws IOException {
        String folderPath = "mekongocop/" + userId + "/" + storeId + "/" + productId;
        logger.info("Attempting to upload image for user {} to folder {}", userId, folderPath);

        try {
            Map<String, Object> params = ObjectUtils.asMap(
                    "folder", folderPath,
                    "resource_type", "auto"
            );
            Map<String, Object> result = this.cloudinary.uploader().upload(file.getBytes(), params);
            logger.info("Image uploaded successfully. Result: {}", result);
            return result;
        } catch (IOException e) {
            logger.error("Failed to upload image for user {}", userId, e);
            throw e;
        }
    }

    public void deleteProductImage(String imageUrl) {
        try {
            // Lấy ID của hình ảnh từ URL
            String[] parts = imageUrl.split("/");
            String publicId = parts[parts.length - 1].split("\\.")[0];

            // Xóa hình ảnh trên Cloudinary
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Error deleting product image from Cloudinary", e);
        }
    }


}

