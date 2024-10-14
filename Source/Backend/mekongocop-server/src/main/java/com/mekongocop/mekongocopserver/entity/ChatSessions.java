package com.mekongocop.mekongocopserver.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.security.Timestamp;
import java.util.List;

@Data
@Entity
@Table(name = "chat_sessions")
public class ChatSessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int session_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages;
    private Timestamp created_at;
    private Timestamp updated_at;
}
