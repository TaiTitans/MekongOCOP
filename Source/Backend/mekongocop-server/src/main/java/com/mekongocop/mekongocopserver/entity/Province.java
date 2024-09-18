package com.mekongocop.mekongocopserver.entity;

import jakarta.persistence.*;

@Entity
@Table(name="province")
public class Province {
    public Province(int province_id, String province_name) {
        this.province_id = province_id;
        this.province_name = province_name;
    }

    public Province() {

    }

    public int getProvince_id() {
        return province_id;
    }

    public void setProvince_id(int province_id) {
        this.province_id = province_id;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int province_id;
    private String province_name;
}
