package com.mekongocop.mekongocopserver.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    private String message_content;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp created_at;
    private Boolean is_read;

    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatAttachment> attachments;
}

