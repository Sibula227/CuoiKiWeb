package com.hcmute.qaute.dto;

import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private String title;
    private String body;
    private String type; // NEW_ANSWER, SYSTEM...
    private String referenceId; // Link tới đâu khi click vào
    private boolean isRead;
    private String timeAgo; // "5 phút trước"
}