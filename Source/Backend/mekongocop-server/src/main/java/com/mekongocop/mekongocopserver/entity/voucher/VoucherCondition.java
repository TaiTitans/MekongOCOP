package com.mekongocop.mekongocopserver.entity.voucher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "voucher_conditions")
public class VoucherCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "voucher_id", nullable = false)
    @JsonIgnore
    private Voucher voucher;

    @Column(name = "condition_type", nullable = false)
    private String conditionType;

    @Column(name = "condition_auth", nullable = false)
    private int conditionAuth;

}
