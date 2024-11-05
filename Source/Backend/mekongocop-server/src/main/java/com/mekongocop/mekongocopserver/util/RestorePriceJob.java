package com.mekongocop.mekongocopserver.util;

import com.mekongocop.mekongocopserver.dto.ProductDTO;
import com.mekongocop.mekongocopserver.service.ProductService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class RestorePriceJob implements Job {
    @Autowired
    private ProductService productService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Flash sale ended!");
        restoreOriginalPrices();
    }

    private void restoreOriginalPrices() {
        List<ProductDTO> products = productService.getAllProductList();
        for (ProductDTO productDTO : products) {
            BigDecimal originalPrice = productDTO.getOriginalPrice();
            productService.updatePrice2(productDTO.getProductId(), originalPrice);
        }
    }
}