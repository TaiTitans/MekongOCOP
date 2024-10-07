package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    User findByUsername(String username);
    // Đếm tổng số user
    @Query("SELECT COUNT(u) FROM User u")
    Long countAllUsers();

    // Đếm số user có role "ROLE_BUYER"
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.role_name = 'ROLE_BUYER'")
    Long countUsersWithRoleBuyer();

    // Đếm số user có role "ROLE_SELLER"
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.role_name = 'ROLE_SELLER'")
    Long countUsersWithRoleSeller();
}
