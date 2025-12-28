package com.hcmute.qaute.entity;

import com.hcmute.qaute.entity.enums.AssignmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mapping: Phân công cho câu hỏi nào?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // Mapping: Phân công cho ai? (Người nhận việc)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to", nullable = false)
    private User assignedTo;

    // Mapping: Ai phân công? (Người quản lý)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;

    @Column(length = 500)
    private String note; // Ghi chú khi giao việc (Ví dụ: "Em xem kỹ case này nhé")

    @Enumerated(EnumType.STRING)
    private AssignmentStatus status = AssignmentStatus.OPEN;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}