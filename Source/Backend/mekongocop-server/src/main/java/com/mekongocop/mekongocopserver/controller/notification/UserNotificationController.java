package com.mekongocop.mekongocopserver.controller.notification;

import com.mekongocop.mekongocopserver.common.StatusResponse;
import com.mekongocop.mekongocopserver.entity.UserNotification;
import com.mekongocop.mekongocopserver.repository.UserNotificationRepository;
import com.mekongocop.mekongocopserver.service.notification.NotificationProducer;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import com.mekongocop.mekongocopserver.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class UserNotificationController {
@Autowired
private NotificationProducer notificationProducer;
    private final UserNotificationRepository userNotificationRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public UserNotificationController(UserNotificationRepository userNotificationRepository, JwtTokenProvider jwtTokenProvider) {
        this.userNotificationRepository = userNotificationRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/common/notification/history")
    public ResponseEntity<StatusResponse<List<UserNotification>>> getUserNotifications(@RequestHeader("Authorization") String token) {
        try{
            String validToken = TokenExtractor.extractToken(token);
            if(!jwtTokenProvider.validateToken(token)){
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Error token", null));
            }
            List<UserNotification> listNoti = notificationProducer.getNotifications(validToken);
            return ResponseEntity.ok().body(new StatusResponse<>("Success", "Noti list", listNoti));

        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Get Noti Failed", null));
        }
    }
}