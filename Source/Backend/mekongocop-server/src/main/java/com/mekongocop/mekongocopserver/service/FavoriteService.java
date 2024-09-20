package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.FavoriteDTO;
import com.mekongocop.mekongocopserver.dto.ProductDTO;
import com.mekongocop.mekongocopserver.entity.Favorite;
import com.mekongocop.mekongocopserver.entity.Product;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.FavoriteRepository;
import com.mekongocop.mekongocopserver.repository.ProductRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ProductService productService;

    public Favorite convertToEntity(FavoriteDTO favoriteDTO) {
        return modelMapper.map(favoriteDTO, Favorite.class);
    }

    public FavoriteDTO convertToDto(Favorite favorite) {
        return modelMapper.map(favorite, FavoriteDTO.class);
    }


    public void addFavorite(int productId, String token) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

            Optional<Favorite> favorite = favoriteRepository.findByUserIdAndProductId(userId, productId);
            if (favorite.isPresent()) {
                favoriteRepository.deleteByUserIdAndProductId(userId, productId);
            } else {
                Favorite newFavorite = new Favorite();
                newFavorite.setUser(user);
                newFavorite.setProduct(product);
                favoriteRepository.save(newFavorite);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<ProductDTO> getListProductFromFavorite(String token) {
        try {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            // Ensure the user exists
            userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

            // Fetch favorite products as entities
            List<Product> favoriteProducts = favoriteRepository.findFavoriteProductsByUserId(userId);

            // Convert products to ProductDTO using ModelMapper
            return favoriteProducts.stream()
                    .map(product -> productService.convertToDTO(product))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
