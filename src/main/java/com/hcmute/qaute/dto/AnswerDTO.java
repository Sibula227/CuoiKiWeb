package com.hcmute.qaute.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AnswerDTO {
    private Long id;
    private Long questionId;
    
    // SỬA TÊN CÁC BIẾN NÀY ĐỂ KHỚP VỚI HTML
    private String username;     // Thay cho authorName
    private String roleCode;     // Thay cho authorRole (QUAN TRỌNG: HTML dùng cái này để check màu)
    private String avatarUrl;    // Thay cho authorAvatar

    private String content;
    private LocalDateTime createdAt;
    
    // THÊM BIẾN NÀY (HTML cần để hiển thị "5 phút trước")
    private String timeAgo;
    
    // Giữ nguyên list này nếu bạn muốn phát triển tính năng file đính kèm sau này
    // private List<AttachmentDTO> attachments; 
}