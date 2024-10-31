package com.mekongocop.mekongocopserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;


@Data
public class ChatSessionsDTO {

        private int session_id;
        private int user_id;   // Ánh xạ từ userId trong JSON


        private int store_id; // Tương tự, chỉ cần store ID
        private List<ChatMessageDTO> messages;  // Danh sách các tin nhắn trong phiên chat

        // Có thể thêm các trường bổ sung như thời gian tạo hoặc cập nhật
        private Timestamp created_at;
        private Timestamp updated_at;
        // Constructor không tham số
        public ChatSessionsDTO() {
        }
    }



