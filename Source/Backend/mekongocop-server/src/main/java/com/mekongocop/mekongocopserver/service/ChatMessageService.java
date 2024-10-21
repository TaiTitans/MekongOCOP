package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.ChatAttachmentDTO;
import com.mekongocop.mekongocopserver.dto.ChatMessageDTO;
import com.mekongocop.mekongocopserver.entity.ChatAttachment;
import com.mekongocop.mekongocopserver.entity.ChatMessage;
import com.mekongocop.mekongocopserver.entity.ChatSessions;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.ChatMessageRepository;
import com.mekongocop.mekongocopserver.repository.ChatSessionsRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private ChatAttachmentService chatAttachmentService;
    @Autowired
    @Lazy
    private ChatSessionsRepository chatSessionsRepository;
    @Autowired
    private UserRepository userRepository;
   @Autowired
   private ChatSessionsService chatSessionsService;

    public ChatMessage convertToEntity(ChatMessageDTO messageDTO, ChatSessions chatSession, User sender) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage_id(messageDTO.getMessage_id());
        chatMessage.setChatSession(chatSession);  // ChatSession entity từ service hoặc repository
        chatMessage.setSender(sender);            // User entity của người gửi
        chatMessage.setMessage_content(messageDTO.getMessage_content());
        chatMessage.setCreated_at(messageDTO.getCreated_at());
        chatMessage.setIs_read(messageDTO.is_read());

        // Sửa lỗi tại đây
        List<ChatAttachment> attachments = messageDTO.getAttachments().stream()
                .map(attachmentDTO -> chatAttachmentService.convertToEntity(attachmentDTO, chatMessage))// Gọi hàm convertToEntity cho ChatAttachment
                .collect(Collectors.toList());


        chatMessage.setAttachments(attachments);
        return chatMessage;
    }


    public ChatMessageDTO convertToDTO(ChatMessage chatMessage) {
        ChatMessageDTO messageDTO = new ChatMessageDTO();
        messageDTO.setMessage_id(chatMessage.getMessage_id());
        messageDTO.setSession_id(chatMessage.getChatSession().getSession_id());
        messageDTO.setSender_id(chatMessage.getSender().getUser_id());
        messageDTO.setMessage_content(chatMessage.getMessage_content());
        messageDTO.setCreated_at(chatMessage.getCreated_at());
        messageDTO.set_read(chatMessage.getIs_read());

        // Chuyển đổi các file đính kèm (nếu có)
        List<ChatAttachmentDTO> attachmentDTOs = chatMessage.getAttachments().stream()
                .map(attachment -> chatAttachmentService.convertToDTO(attachment)) // Correct method reference
                .collect(Collectors.toList());

        messageDTO.setAttachments(attachmentDTOs);
        return messageDTO;
    }

    // Lưu tin nhắn vào DB
    public void saveMessageToDatabase(ChatMessageDTO messageDto) {
        // Tìm session_id dựa trên user_id và store_id
        Optional<ChatSessions> optionalChatSession = chatSessionsRepository.findByUserIdAndStoreId(messageDto.getSender_id(), messageDto.getStore_id());

        // Kiểm tra nếu có giá trị trong Optional
        ChatSessions chatSession = optionalChatSession
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        // Tìm sender dựa trên sender_id
        User sender = userRepository.findById(messageDto.getSender_id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo một đối tượng ChatMessage mới
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatSession(chatSession);
        chatMessage.setSender(sender);
        chatMessage.setMessage_content(messageDto.getMessage_content());
        chatMessage.setCreated_at(new Timestamp(System.currentTimeMillis()));
        chatMessage.setIs_read(false);

        // Lưu tin nhắn vào database
        chatMessageRepository.save(chatMessage);

        // Cập nhật thời gian mới nhất cho ChatSession
        chatSession.setUpdated_at(new Timestamp(System.currentTimeMillis()));
        chatSessionsService.save(chatSession);
    }



    // Lấy tin nhắn theo session_id
    public List<ChatMessage> findBySessionId(int sessionId) {
        return chatMessageRepository.findByChatSession_SessionId(sessionId);
    }

    // Tìm tin nhắn theo message_id
    public Optional<ChatMessage> findById(int messageId) {
        return chatMessageRepository.findById(messageId);
    }



    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }


}
