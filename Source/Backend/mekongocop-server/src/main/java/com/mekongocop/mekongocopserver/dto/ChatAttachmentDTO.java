package com.mekongocop.mekongocopserver.dto;

import lombok.Data;

@Data
public class ChatAttachmentDTO {
    private int attachment_id;
    private int message_id;    // ID của tin nhắn mà file đính kèm thuộc về
    private String file_url;   // Đường dẫn tới file đính kèm
}
