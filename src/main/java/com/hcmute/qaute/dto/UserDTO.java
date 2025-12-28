package com.hcmute.qaute.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String studentIdCode; // MSSV
    private String phone;
    private String avatarUrl;
    private String roleName; // Chỉ cần tên role (STUDENT, ADMIN...)
    private boolean isActive;
    private LocalDateTime createdAt;
}