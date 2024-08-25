package com.mekongocop.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mekongocop.userservice.entity.UserProfile;
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
}