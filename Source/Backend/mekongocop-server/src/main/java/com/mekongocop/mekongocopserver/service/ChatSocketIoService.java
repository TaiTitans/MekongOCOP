package com.mekongocop.mekongocopserver.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.mekongocop.mekongocopserver.dto.ChatMessageDTO;
import com.mekongocop.mekongocopserver.dto.ChatSessionsDTO;
import com.mekongocop.mekongocopserver.entity.ChatMessage;
import com.mekongocop.mekongocopserver.entity.ChatSessions;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.ChatSessionsRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
public class ChatSocketIoService {
    private static final Logger logger = LoggerFactory.getLogger(ChatSocketIoService.class);

    private final SocketIOServer socketIOServer;
    private final ChatMessageService chatMessageService;
    private final ChatSessionsService chatSessionsService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ChatSessionsRepository chatSessionsRepository;

    @Autowired
    public ChatSocketIoService(SocketIOServer socketIOServer, ChatMessageService chatMessageService,
                               ChatSessionsService chatSessionsService, UserService userService,
                               UserRepository userRepository, ChatSessionsRepository chatSessionsRepository) {
        this.socketIOServer = socketIOServer;
        this.chatMessageService = chatMessageService;
        this.chatSessionsService = chatSessionsService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.chatSessionsRepository = chatSessionsRepository;
    }

    @PostConstruct
    private void init() {
        logger.info("Initializing Socket.IO server...");

        // Handle new connections
        socketIOServer.addConnectListener(client -> {
            logger.info("Client connected: " + client.getSessionId());
        });

        // Handle disconnections
        socketIOServer.addDisconnectListener(client -> {
            logger.info("Client disconnected: " + client.getSessionId());
        });

        // Listen for "send_message" event
        socketIOServer.addEventListener("send_message", ChatMessageDTO.class, (client, data, ackSender) -> {
            logger.info("Received message from client {}: {}",  data.getMessage_content());

            // Save message to database
            chatMessageService.saveMessageToDatabase(data);

            // Broadcast the message to other clients
            socketIOServer.getBroadcastOperations().sendEvent("receive_message", data);
            logger.info("Broadcasted message to all clients.");
        });

        // Listen for "new_chat_session" event
        socketIOServer.addEventListener("new_chat_session", ChatSessionsDTO.class, (client, data, ackSender) -> {
            logger.info("New chat session created: {}", data);

            // Fetch the user and store details from the DTO
            int userId = data.getUser_id();
            int storeId = data.getStore_id();

            try {
                // Create or retrieve the chat session
                ChatSessions newSession = chatSessionsService.createSession(userId, storeId);

                // Convert the newly created session to DTO and broadcast it
                ChatSessionsDTO sessionDTO = chatSessionsService.convertToDTO(newSession);
                socketIOServer.getBroadcastOperations().sendEvent("new_chat_session_to_seller", sessionDTO);

                logger.info("Chat session saved and broadcasted: {}", sessionDTO);
            } catch (Exception e) {
                logger.error("Failed to create or retrieve chat session", e);
            }
        });

        // Start the Socket.IO server
        try {
            socketIOServer.start();
            logger.info("Socket.IO server started successfully on port: {}", socketIOServer.getConfiguration().getPort());
        } catch (Exception e) {
            logger.error("Failed to start Socket.IO server", e);
        }
    }

    @PreDestroy
    private void stop() {
        logger.info("Stopping Socket.IO server...");
        socketIOServer.stop();
        logger.info("Socket.IO server stopped.");
    }
}
