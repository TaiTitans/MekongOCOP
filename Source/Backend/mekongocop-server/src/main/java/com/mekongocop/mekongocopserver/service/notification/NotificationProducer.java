package com.mekongocop.mekongocopserver.service.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.entity.UserNotification;
import com.mekongocop.mekongocopserver.repository.UserNotificationRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.management.Notification;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationProducer {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private ObjectMapper objectMapper;
    private static final String TOPIC = "notifications";
    private final UserRepository userRepository;

    public NotificationProducer(KafkaTemplate<String, String> kafkaTemplate, UserNotificationRepository userNotificationRepository, UserRepository userRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.userNotificationRepository = userNotificationRepository;
        this.userRepository = userRepository;
    }

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserNotificationRepository userNotificationRepository;

    public void sendNotification(String message) {
        kafkaTemplate.send(TOPIC, message);
    }

    public List<UserNotification> getNotifications(String token) {
        try (Jedis jedis = jedisPool.getResource()) {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            if (userId == -1) {
                return null;
            }

            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

            String cacheKey = "user_notifications:" + userId;
            String cachedNotifications = jedis.get(cacheKey);

            if (cachedNotifications != null) {
                return objectMapper.readValue(cachedNotifications, objectMapper.getTypeFactory().constructCollectionType(List.class, UserNotification.class));
            }

            List<UserNotification> userNotifications = userNotificationRepository.findByUserId(user);
            jedis.set(cacheKey, objectMapper.writeValueAsString(userNotifications));
            jedis.expire(cacheKey, 180); // Cache for 15 minutes

            return userNotifications;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
