package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatSession.session_id = :sessionId")
    List<ChatMessage> findByChatSession_SessionId(@Param("sessionId") int sessionId);
}
