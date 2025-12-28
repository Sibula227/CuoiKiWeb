package com.hcmute.qaute.dto;

import com.hcmute.qaute.entity.enums.AssignmentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssignmentDTO {
    private Long id;
    
    // Thông tin câu hỏi
    private Long questionId;
    private String questionTitle;

    // Phân công cho ai
    private Long assignedToId;
    private String assignedToName;

    // Ai phân công
    private String assignedByName;

    private String note;
    private AssignmentStatus status;
    private LocalDateTime createdAt;
}