package com.mekongocop.mekongocopserver.util;

import com.mekongocop.mekongocopserver.dto.ProductDTO;
import com.mekongocop.mekongocopserver.entity.Product;
import com.mekongocop.mekongocopserver.repository.ProductRepository;
import com.mekongocop.mekongocopserver.service.ProductService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.math.BigDecimal;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class FlashSaleJob implements Job {
    @Autowired
    private ProductService productService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Flash sale started!");
        applyDiscountToAllProducts();
    }

    private void applyDiscountToAllProducts() {
        List<ProductDTO> products = productService.getAllProductList();
        for (ProductDTO productDTO : products) {
            BigDecimal originalPrice = productDTO.getProductPrice();
            BigDecimal discountedPrice = originalPrice.multiply(BigDecimal.valueOf(0.9)); // Giảm 10%
            productService.updatePrice2(productDTO.getProductId(), discountedPrice);
        }
    }
}