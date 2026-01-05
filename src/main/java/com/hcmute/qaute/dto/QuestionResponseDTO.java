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
    private Long studentId; // ID trong database (dùng để link)
    private String studentName;
    private String studentAvatar;

    // --- THÊM DÒNG QUAN TRỌNG NÀY ---
    private String studentIdCode; // Chứa MSSV thật (VD: 2011037)
    // --------------------------------

    private String studentFaculty; // Khoa
    private String studentCohort; // Khóa

    // Thông tin phòng ban
    private Integer departmentId;
    private String departmentName;

    private QuestionPriority priority;
    private QuestionStatus status;

    private LocalDateTime createdAt;
    private String timeAgo;

    private Integer viewCount; // Thêm trường này

    private String tagsCached; // Chuỗi tags phân tách bởi dấu phẩy
    private List<com.hcmute.qaute.entity.Attachment> attachments; // List file đính kèm
}