package com.mekongocop.mekongocopserver.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int address_id;
    private String address_description;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
