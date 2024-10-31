package com.mekongocop.mekongocopserver.controller;

import com.mekongocop.mekongocopserver.dto.ChatMessageDTO;
import com.mekongocop.mekongocopserver.entity.ChatMessage;
import com.mekongocop.mekongocopserver.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/")
public class ChatMessageController {
    @Autowired
    private ChatMessageService chatMessageService;

    // API lấy tin nhắn theo session_id
    @GetMapping("common/chatMessage/session/{session_id}")
    public ResponseEntity<List<ChatMessageDTO>> getChatMessagesBySessionId(@PathVariable int session_id) {
        List<ChatMessage> chatMessages = chatMessageService.findBySessionId(session_id);
        List<ChatMessageDTO> chatMessageDtos = chatMessages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(chatMessageDtos, HttpStatus.OK);
    }

    // API cập nhật trạng thái đã đọc
    @PutMapping("common/chatMessage/read/{message_id}")
    public ResponseEntity<String> markMessageAsRead(@PathVariable int message_id) {
        ChatMessage message = chatMessageService.findById(message_id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setIsRead(true);
        chatMessageService.save(message);

        return new ResponseEntity<>("Message marked as read", HttpStatus.OK);
    }
    private ChatMessageDTO convertToDto(ChatMessage chatMessage) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSession_id(chatMessage.getChatSession().getSessionId());
        dto.setSender_id(chatMessage.getSender().getUser_id());
        dto.setMessage_content(chatMessage.getMessageContent());
        dto.setCreated_at(chatMessage.getCreatedAt());
        dto.setIs_read(chatMessage.getIsRead());
        return dto;
    }
}
