package com.mekongocop.mekongocopserver.repository;


import com.mekongocop.mekongocopserver.entity.Store;
import com.mekongocop.mekongocopserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Integer> {
    @Query("SELECT s FROM Store s WHERE s.user_id.user_id = :user_id")
    Optional<Store> findByUserId(@Param("user_id") Integer user_id);
}
