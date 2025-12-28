package com.hcmute.qaute.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AnswerDTO {
    private Long id;
    private Long questionId;
    
    private String authorName; // Tên người trả lời
    private String authorRole; // Role người trả lời (để tô màu Admin khác SV)
    private String authorAvatar;

    private String content;
    private LocalDateTime createdAt;
    
    // Danh sách file đính kèm trong câu trả lời (nếu có)
    private java.util.List<AttachmentDTO> attachments;
}