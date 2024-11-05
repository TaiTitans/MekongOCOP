package com.mekongocop.mekongocopserver.util;

import com.corundumstudio.socketio.SocketIOServer;
import com.mekongocop.mekongocopserver.dto.ChatMessageDTO;
import com.mekongocop.mekongocopserver.entity.ChatMessage;
import com.mekongocop.mekongocopserver.entity.ChatSessions;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.ChatMessageRepository;
import com.mekongocop.mekongocopserver.repository.ChatSessionsRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.service.ChatMessageService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class SocketIOEventHandler {
    private final SocketIOServer server;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionsRepository chatSessionRepository;
    private final UserRepository userRepository;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    public SocketIOEventHandler(SocketIOServer server,
                                ChatMessageRepository chatMessageRepository,
                                ChatSessionsRepository chatSessionRepository,
                                UserRepository userRepository) {
        this.server = server;
        this.chatMessageRepository = chatMessageRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.userRepository = userRepository;

        server.addConnectListener(client -> {
            String userId = client.getHandshakeData().getSingleUrlParam("userId");
            System.out.println("Client connected: " + client.getSessionId() + " UserID: " + userId);
            client.joinRoom(userId);
        });

        server.addEventListener("send_message", ChatMessageDTO.class, (client, data, ackRequest) -> {
            // Lấy thông tin session và user từ cơ sở dữ liệu
            ChatSessions session = chatSessionRepository.findById(data.getSession_id())
                    .orElseThrow(() -> new RuntimeException("Session not found"));
            User sender = userRepository.findById(data.getSender_id())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Sử dụng convertToEntity để chuyển đổi ChatMessageDTO thành ChatMessage
            ChatMessage message = chatMessageService.convertToEntity(data, session, sender);

            // Thêm giá trị created_at bằng thời gian hiện tại
            message.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // Lưu tin nhắn vào database
            ChatMessage savedMessage = chatMessageRepository.save(message);

            System.out.println("Sending message to room: " + session.getStore().toString());
            System.out.println("Sending message to room: " + session.getUser().getUser_id().toString());
            server.getRoomOperations(session.getStore().toString())
                    .sendEvent("receive_message", savedMessage);
            server.getRoomOperations(session.getUser().getUser_id().toString())
                    .sendEvent("receive_message", savedMessage);

        });

        server.addEventListener("join_room", String.class, (client, roomId, ackRequest) -> {
            client.joinRoom(roomId);
            System.out.println("Client joined room: " + roomId);
        });

        server.addEventListener("leave_room", String.class, (client, roomId, ackRequest) -> {
            client.leaveRoom(roomId);
            System.out.println("Client left room: " + roomId);
        });
    }

    public void sendNotificationToUser(int userId, String message) {
        server.getRoomOperations(String.valueOf(userId)).sendEvent("receive_notification", message);
    }

    @PostConstruct
    private void startServer() {
        server.start();
    }

    @PreDestroy
    private void stopServer() {
        server.stop();
    }
}