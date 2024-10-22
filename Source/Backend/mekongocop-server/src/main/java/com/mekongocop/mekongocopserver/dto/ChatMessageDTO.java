package com.mekongocop.mekongocopserver.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class ChatMessageDTO {
    private int message_id;
    private int session_id;
    private int store_id;
    private int sender_id;    // ID của người gửi (user hoặc seller)
    private String message_content;
    private Timestamp created_at;  // Thời gian gửi tin nhắn
    private boolean is_read;    // Trạng thái đã đọc của tin nhắn

    // Danh sách các file đính kèm trong tin nhắn
    private List<ChatAttachmentDTO> attachments;
}
