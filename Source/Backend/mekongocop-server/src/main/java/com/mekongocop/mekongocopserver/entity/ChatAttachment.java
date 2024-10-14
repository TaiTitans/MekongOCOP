package com.mekongocop.mekongocopserver.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chat_attachments")
public class ChatAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int attachment_id;
    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessage chatMessage;
    private String file_url;
}
