package com.mekongocop.mekongocopserver.entity.voucher;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int voucher_id;
    private String code;

    @Pattern(regexp = "System|Store", message = "Status must be System, Store")
    private String type;
    @Pattern(regexp = "Percentage|Fixed|Amount", message = "Status must be Percentage, Amount")
    private String discount_type;
    private Double discount_value;
    private Double mind_spend;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private int total_limit;
    private int user_limit;
    @Pattern(regexp = "Active|Expired|Disabled", message = "Status must be Active, Expired, Disabled")
    private String status;
    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VoucherUsers> voucherUsers = new HashSet<>();
}
