package com.hcmute.qaute.repository;

import com.hcmute.qaute.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // Xem lịch sử hoạt động của 1 user (Admin check nếu có nghi ngờ)
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}