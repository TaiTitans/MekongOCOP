package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.dto.UserProfileDTO;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN TRUE ELSE FALSE END FROM UserProfile up WHERE up.user_id = :user")
    boolean existsByUser(@Param("user") User user);

    @Query("SELECT COUNT(up) FROM UserProfile up WHERE up.user_id = :userId")
    int countByUserId(@Param("userId") Integer userId);

    @Query("SELECT up FROM UserProfile up WHERE up.user_id.user_id = :userId")
    UserProfile findByUserId(@Param("userId") int userId);
}
