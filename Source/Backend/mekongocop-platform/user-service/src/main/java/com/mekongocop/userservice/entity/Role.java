package com.mekongocop.userservice.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {
    public Role(Integer role_id, String role_name) {
        this.role_id = role_id;
        this.role_name = role_name;
    }

    public Role() {

    }

    public Role(String role_name) {
        this.role_name = role_name;
    }


    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer role_id;
    private String role_name;
}