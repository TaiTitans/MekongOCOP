package com.mekongocop.mekongocopserver.entity.voucher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mekongocop.mekongocopserver.entity.User;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "voucher_users")
public class VoucherUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

   @ManyToOne
   @JoinColumn(name = "voucher_id", nullable = false)
   @JsonIgnore
   private Voucher voucher;

   @ManyToOne
   @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int usage_count;
}
