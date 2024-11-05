package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.voucher.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    Optional<Voucher> findByCode(String code);
}
