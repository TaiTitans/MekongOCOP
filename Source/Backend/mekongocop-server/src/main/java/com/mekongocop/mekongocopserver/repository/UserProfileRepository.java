package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
}
