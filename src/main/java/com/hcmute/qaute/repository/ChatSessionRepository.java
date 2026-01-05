package com.hcmute.qaute.repository;

import com.hcmute.qaute.entity.ChatSession;
import com.hcmute.qaute.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findByUserOrderByUpdatedAtDesc(User user);

    Optional<ChatSession> findTopByUserOrderByUpdatedAtDesc(User user);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM ChatSession s WHERE s.user = :user")
    void deleteAllByUser(@org.springframework.data.repository.query.Param("user") User user);
}
