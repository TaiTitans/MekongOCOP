package com.mekongocop.mekongocopserver.repository;

import com.mekongocop.mekongocopserver.entity.ChatAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface ChatAttachmentRepository extends JpaRepository<ChatAttachment, Integer> {
}
