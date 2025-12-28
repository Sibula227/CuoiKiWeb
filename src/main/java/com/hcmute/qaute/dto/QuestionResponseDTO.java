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
    
    // Thông tin người hỏi (chỉ lấy tên và avatar để hiển thị)
    private Long studentId;
    private String studentName;
    private String studentAvatar;

    // Thông tin phòng ban
    private Integer departmentId;
    private String departmentName;

    private QuestionPriority priority;
    private QuestionStatus status;
    
    private LocalDateTime createdAt;
    private String timeAgo; // Ví dụ: "2 giờ trước" (Xử lý ở Mapper)
    
    private List<String> tagNames; // List tên các tag: ["Học phí", "Học lại"]
}