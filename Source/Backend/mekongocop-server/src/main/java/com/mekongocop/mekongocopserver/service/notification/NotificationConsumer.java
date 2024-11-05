package com.mekongocop.mekongocopserver.service.notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.UserNotificationRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.util.SocketIOEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.mekongocop.mekongocopserver.entity.UserNotification;
@Service
public class NotificationConsumer {

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SocketIOEventHandler socketIOEventHandler;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @KafkaListener(topics = "notifications", groupId = "notification-group")
    public void listen(String message) {
        System.out.println("Received message: " + message);

        // Lấy danh sách toàn bộ người dùng
        List<User> users = userRepository.findAll();

        // Chia danh sách người dùng thành các đợt 10000 người dùng
        int batchSize = 10000;
        for (int i = 0; i < users.size(); i += batchSize) {
            int start = i;
            int end = Math.min(i + batchSize, users.size());

            // Lên lịch gửi thông báo cho đợt này
            scheduler.schedule(() -> sendNotifications(users.subList(start, end), message), (i / batchSize) * 10, TimeUnit.SECONDS);
        }
    }

    private void sendNotifications(List<User> users, String message) {
        for (User user : users) {
            // Lưu thông báo vào cơ sở dữ liệu
            UserNotification userNotification = new UserNotification();
            userNotification.setUserId(user);
            userNotification.setMessage(message);
            userNotification.setSent_at(LocalDateTime.now());
            userNotificationRepository.save(userNotification);

            // Gửi thông báo đến người dùng qua WebSocket
            socketIOEventHandler.sendNotificationToUser(user.getUser_id(), message);
        }
    }
}