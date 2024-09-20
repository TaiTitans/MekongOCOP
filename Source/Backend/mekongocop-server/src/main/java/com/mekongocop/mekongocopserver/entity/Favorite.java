package com.mekongocop.mekongocopserver.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "favorite")
public class Favorite {
    public Favorite(int favorite_id, Product product, User user) {
        this.favorite_id = favorite_id;
        this.product = product;
        this.user = user;
    }

    public int getFavorite_id() {
        return favorite_id;
    }

    public void setFavorite_id(int favorite_id) {
        this.favorite_id = favorite_id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int favorite_id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Favorite() {

    }
}
