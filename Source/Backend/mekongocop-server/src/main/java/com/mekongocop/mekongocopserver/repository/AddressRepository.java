package com.mekongocop.mekongocopserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mekongocop.mekongocopserver.entity.Address;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    @Query("SELECT a FROM Address a WHERE a.user.user_id = :userId")
    List<Address> findAllByUserId(@Param("userId") Integer userId);
}
