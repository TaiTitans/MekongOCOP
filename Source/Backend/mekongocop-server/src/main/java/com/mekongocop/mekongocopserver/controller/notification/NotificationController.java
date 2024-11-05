package com.mekongocop.mekongocopserver.controller.notification;

import com.mekongocop.mekongocopserver.service.notification.NotificationProducer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class NotificationController {

    private final NotificationProducer notificationProducer;

    public NotificationController(NotificationProducer notificationProducer) {
        this.notificationProducer = notificationProducer;
    }

    @PostMapping("/admin/notification/send")
     public ResponseEntity<String> sendNotification(@RequestParam String message) {
        notificationProducer.sendNotification(message);
        return ResponseEntity.ok("Notification sent successfully");
    }
}