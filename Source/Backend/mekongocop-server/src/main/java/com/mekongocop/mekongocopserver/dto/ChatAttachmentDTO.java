package com.mekongocop.mekongocopserver.dto;

import lombok.Data;

@Data
public class ChatAttachmentDTO {
    private int attachment_id;
    private int message_id;
    private String file_url;
}
