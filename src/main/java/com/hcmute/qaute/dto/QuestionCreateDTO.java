package com.hcmute.qaute.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionCreateDTO {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    private Integer departmentId; // ID phòng ban sinh viên chọn
}