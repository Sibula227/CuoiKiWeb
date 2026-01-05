package com.hcmute.qaute.repository;

import com.hcmute.qaute.entity.ChatMessage;
import com.hcmute.qaute.entity.ChatSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // Get last N messages for context
    List<ChatMessage> findBySessionOrderByCreatedAtAsc(ChatSession session);

    List<ChatMessage> findBySessionOrderByCreatedAtDesc(ChatSession session, Pageable pageable);
}
