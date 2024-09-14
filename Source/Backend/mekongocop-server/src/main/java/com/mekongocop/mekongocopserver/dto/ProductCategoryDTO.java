package com.mekongocop.mekongocopserver.dto;

import java.util.List;

public class ProductCategoryDTO {
    public ProductCategoryDTO(int category_id, String category_name, List<ProductDTO> products) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.products = products;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }

    private int category_id;
    private String category_name;
    private List<ProductDTO> products;
}
