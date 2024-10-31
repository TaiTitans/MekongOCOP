package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.entity.ChatMessage;
import com.mekongocop.mekongocopserver.entity.ChatSessions;
import com.mekongocop.mekongocopserver.entity.Store;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.ChatMessageRepository;
import com.mekongocop.mekongocopserver.repository.ChatSessionsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class ChatService {
    private final ChatSessionsRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatService(ChatSessionsRepository chatSessionRepository,
                       ChatMessageRepository chatMessageRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatSessions createChatSession(User user, Store store) {
        ChatSessions session = new ChatSessions();
        session.setUser(user);
        session.setStore(store);
        session.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        session.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return chatSessionRepository.save(session);
    }

    public List<ChatMessage> getChatHistory(int sessionId) {
            return chatMessageRepository.findByChatSession_SessionIdOrderByCreatedAtAsc(sessionId);
    }

    public void markMessagesAsRead(int sessionId, int userId) {
        List<ChatMessage> unreadMessages = chatMessageRepository.
        findUnreadMessagesBySessionAndSenderNot(sessionId, userId);
        unreadMessages.forEach(message -> message.setIsRead(true));
        chatMessageRepository.saveAll(unreadMessages);
    }

}
