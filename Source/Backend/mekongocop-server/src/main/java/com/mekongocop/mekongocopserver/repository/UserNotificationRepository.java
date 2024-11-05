package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {
    List<UserNotification> findByUserId(User user);
}
