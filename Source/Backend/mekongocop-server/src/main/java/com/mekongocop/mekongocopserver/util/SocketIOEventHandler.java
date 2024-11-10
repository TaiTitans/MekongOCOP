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
            String sessionId = client.getHandshakeData().getSingleUrlParam("sessionId");
            if (sessionId != null) {
                System.out.println("Client connected: " + client.getSessionId() + " SessionID: " + sessionId);
                client.joinRoom("chat_room_" + sessionId);  // Join room based on sessionId
            } else {
                System.out.println("Client connected: " + client.getSessionId() + " but SessionID is null");
            }
        });

        server.addEventListener("send_message", ChatMessageDTO.class, (client, data, ackRequest) -> {
            try {
                // Fetch session and user from the database
                ChatSessions session = chatSessionRepository.findById(data.getSession_id())
                        .orElseThrow(() -> new RuntimeException("Session not found"));
                User sender = userRepository.findById(data.getSender_id())
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // Convert ChatMessageDTO to ChatMessage
                ChatMessage message = chatMessageService.convertToEntity(data, session, sender);
                message.setCreatedAt(new Timestamp(System.currentTimeMillis()));

                // Save message to the database
                ChatMessage savedMessage = chatMessageRepository.save(message);
                ChatMessageDTO chatMessageDTO = chatMessageService.convertToDTO(savedMessage);
                // Broadcast message to the chat room based on sessionId
                // Broadcast message to the chat room based on sessionId
                String roomId = "chat_room_" + session.getSessionId();
                System.out.println("Sending message to room: " + roomId);
                System.out.println("Sending message : " + chatMessageDTO);
                server.getRoomOperations(roomId).sendEvent("receive_message", chatMessageDTO);
            } catch (Exception e) {
                System.err.println("Error handling send_message event: " + e.getMessage());
                e.printStackTrace();
            }
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