package com.hcmute.qaute.dto;

import lombok.Data;

@Data
public class AttachmentDTO {
    private Long id;
    private String filename;
    private String contentType; // image/png
    private Long fileSize;
    private String downloadUrl; // Đường dẫn tải file
}