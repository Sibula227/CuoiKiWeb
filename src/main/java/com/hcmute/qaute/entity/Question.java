package com.hcmute.qaute.entity;

import com.hcmute.qaute.entity.enums.QuestionPriority;
import com.hcmute.qaute.entity.enums.QuestionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Enumerated(EnumType.STRING)
    private QuestionPriority priority = QuestionPriority.NORMAL;

    @Enumerated(EnumType.STRING)
    private QuestionStatus status = QuestionStatus.PENDING;

    // --- CÁC TRƯỜNG BỔ SUNG THEO ERD ---
    @Column(name = "tags_cached")
    private String tagsCached; // Lưu dạng chuỗi ví dụ: "hoc-phi, tot-nghiep" để search nhanh

    @Column(name = "closed_at")
    private LocalDateTime closedAt;
    // ------------------------------------

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}