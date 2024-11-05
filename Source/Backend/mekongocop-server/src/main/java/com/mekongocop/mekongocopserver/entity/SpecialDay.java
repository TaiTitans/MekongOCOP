package com.mekongocop.mekongocopserver.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "special_days")
public class SpecialDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "special_day")
    private LocalDate special_day;
}
