package com.mekongocop.mekongocopserver.dto;

import java.util.ArrayList;
import java.util.List;

public class CartDTO {
    public CartDTO(List<CartItemDTO> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public List<CartItemDTO> getCartItemList() {
        return cartItemList;
    }
    public CartDTO() {
        this.cartItemList = new ArrayList<>();
    }

    public void setCartItemList(List<CartItemDTO> cartItemList) {
        this.cartItemList = cartItemList;
    }

    private List<CartItemDTO> cartItemList = new ArrayList<>();
}
