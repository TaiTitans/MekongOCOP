package com.mekongocop.mekongocopserver.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatMessageDTO {
    private int message_id;
    private int session_id;   // ID của phiên chat mà tin nhắn thuộc về
    private int sender_id;    // ID của người gửi (user hoặc seller)
    private String message_content;
    private String created_at;  // Thời gian gửi tin nhắn
    private boolean is_read;    // Trạng thái đã đọc của tin nhắn

    // Danh sách các file đính kèm trong tin nhắn
    private List<ChatAttachmentDTO> attachments;
}
