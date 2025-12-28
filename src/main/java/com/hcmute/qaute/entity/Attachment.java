package com.hcmute.qaute.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // File này thuộc câu hỏi nào? (Có thể null nếu thuộc về câu trả lời)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    // File này thuộc câu trả lời nào? (Có thể null nếu thuộc về câu hỏi)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @Column(nullable = false)
    private String filename; // Tên file gốc: cv.pdf

    @Column(name = "content_type", length = 100)
    private String contentType; // image/png, application/pdf

    @Column(name = "file_size")
    private Long fileSize; // Byte

    @Column(name = "storage_path", nullable = false, length = 1000)
    private String storagePath; // Đường dẫn lưu trên server hoặc URL S3

    // Ai là người upload?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}