package com.hcmute.qaute.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // Lưu ID thôi, không cần quan hệ cứng để tránh lỗi khi xóa user

    @Column(nullable = false, length = 200)
    private String action; // LOGIN, DELETE_QUESTION...

    @Column(name = "object_type", length = 100)
    private String objectType; // QUESTION, USER...

    @Column(name = "object_id", length = 100)
    private String objectId;

    @Column(columnDefinition = "JSON")
    private String details; // Lưu chi tiết thay đổi (nếu dùng MySQL 5.7+ có hỗ trợ JSON)

    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}