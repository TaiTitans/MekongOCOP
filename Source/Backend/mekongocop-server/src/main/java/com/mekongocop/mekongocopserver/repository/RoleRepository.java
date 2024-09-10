package com.mekongocop.mekongocopserver.repository;


import com.mekongocop.mekongocopserver.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query("SELECT r FROM Role r WHERE r.role_name = :role_name")
    Role findByRolename(String role_name);
}
