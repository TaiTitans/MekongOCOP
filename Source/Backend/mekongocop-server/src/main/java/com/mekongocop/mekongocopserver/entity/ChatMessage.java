package com.mekongocop.mekongocopserver.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int message_id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSessions chatSession;

    private int sender_id;
    private String message_content;
    private Timestamp created_at;
    private Boolean is_read;

    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatAttachment> attachments;
}

