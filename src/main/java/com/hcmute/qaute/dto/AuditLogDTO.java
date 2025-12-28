package com.hcmute.qaute.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLogDTO {
    private Long id;
    private String userName; // Tên người thực hiện
    private String action;   // LOGIN, DELETE...
    private String objectType;
    private String details;
    private String ipAddress;
    private LocalDateTime createdAt;
}