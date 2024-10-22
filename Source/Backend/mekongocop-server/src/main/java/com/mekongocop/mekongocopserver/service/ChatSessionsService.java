package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.ChatMessageDTO;
import com.mekongocop.mekongocopserver.dto.ChatSessionsDTO;
import com.mekongocop.mekongocopserver.entity.ChatMessage;
import com.mekongocop.mekongocopserver.entity.ChatSessions;
import com.mekongocop.mekongocopserver.entity.Store;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.ChatSessionsRepository;
import com.mekongocop.mekongocopserver.repository.StoreRepository;
import com.mekongocop.mekongocopserver.repository.UserProfileRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatSessionsService {
    @Autowired
    private ChatSessionsRepository chatSessionsRepository;
    @Autowired
    @Lazy
    private ChatMessageService chatMessageService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreRepository storeRepository;



    public ChatSessions convertToEntity(ChatSessionsDTO sessionDTO, User user, Store store) {
        ChatSessions chatSession = new ChatSessions();
        chatSession.setSession_id(sessionDTO.getSession_id());
        chatSession.setUser(user);
        chatSession.setStore(store);
        chatSession.setCreated_at(sessionDTO.getCreated_at());
        chatSession.setUpdated_at(sessionDTO.getUpdated_at());

        // Chuyển đổi danh sách messageDTOs thành entities
        List<ChatMessage> messages = sessionDTO.getMessages().stream()
                .map(messageDTO -> chatMessageService.convertToEntity(messageDTO, chatSession, user))
                // Truyền đầy đủ các tham số cần thiết
                .collect(Collectors.toList());

        chatSession.setMessages(messages);
        return chatSession;
    }



    public ChatSessionsDTO convertToDTO(ChatSessions chatSession) {
        ChatSessionsDTO sessionDTO = new ChatSessionsDTO();
        sessionDTO.setSession_id(chatSession.getSession_id());
        sessionDTO.setUser_id(chatSession.getUser().getUser_id());
        sessionDTO.setStore_id(chatSession.getStore().getStore_id());
        sessionDTO.setCreated_at(chatSession.getCreated_at());
        sessionDTO.setUpdated_at(chatSession.getUpdated_at());

        // Check if messages are not null
        List<ChatMessageDTO> messageDTOs = (chatSession.getMessages() != null)
                ? chatSession.getMessages().stream()
                .map(message -> chatMessageService.convertToDTO(message))
                .collect(Collectors.toList())
                : new ArrayList<>();

        sessionDTO.setMessages(messageDTOs);
        return sessionDTO;
    }





    public ChatSessions save(ChatSessions chatSession) {
        return chatSessionsRepository.save(chatSession);
    }

    public ChatSessions createSession(int userId, int storeId) {
        Optional<ChatSessions> existingSession = chatSessionsRepository.findByUserIdAndStoreId(userId, storeId);

        // Nếu phiên chat chưa tồn tại, tạo mới
        if (existingSession.isPresent()) {
            return existingSession.get();
        } else {
            ChatSessions session = new ChatSessions();
            session.setUser(userRepository.findById(userId).orElseThrow());
            session.setStore(storeRepository.findById(storeId).orElseThrow());
            session.setCreated_at(new Timestamp(System.currentTimeMillis()));
            session.setUpdated_at(new Timestamp(System.currentTimeMillis()));
            return chatSessionsRepository.save(session);
        }
    }
    public List<ChatSessionsDTO> findByUserId(int userId) {
        List<ChatSessions> sessions = chatSessionsRepository.findByUser_UserId(userId);
        return sessions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ChatSessionsDTO> findByStoreId(int storeId) {
        List<ChatSessions> sessions = chatSessionsRepository.findByStore_StoreId(storeId);
        return sessions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
