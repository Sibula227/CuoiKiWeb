package com.hcmute.qaute.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Thông báo này gửi cho ai?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    // Loại thông báo: NEW_ANSWER, ASSIGNED, SYSTEM...
    @Column(length = 100)
    private String type;

    // ID tham chiếu (Ví dụ: ID của câu hỏi hoặc câu trả lời liên quan)
    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "is_read")
    private Boolean isRead = false; // Mặc định là chưa đọc

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isRead == null) this.isRead = false;
    }
}