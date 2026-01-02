package com.hcmute.qaute.dto;

import com.hcmute.qaute.entity.enums.QuestionPriority;
import com.hcmute.qaute.entity.enums.QuestionStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionResponseDTO {
    private Long id;
    private String title;
    private String content;
    
    // Thông tin người hỏi
    private Long studentId;
    private String studentName;
    private String studentAvatar;

    // --- BỔ SUNG 2 DÒNG NÀY ---
    private String studentFaculty; // Khoa
    private String studentCohort;  // Khóa
    // --------------------------

    // Thông tin phòng ban
    private Integer departmentId;
    private String departmentName;

    private QuestionPriority priority;
    private QuestionStatus status;
    
    private LocalDateTime createdAt;
    private String timeAgo;
    
    private List<String> tagNames;
}