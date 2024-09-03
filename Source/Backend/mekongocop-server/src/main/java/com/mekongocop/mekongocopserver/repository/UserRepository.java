package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    User findByUsername(String username);
}
