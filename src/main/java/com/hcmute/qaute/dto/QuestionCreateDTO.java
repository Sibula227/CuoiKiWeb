package com.hcmute.qaute.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // <-- Nhớ import cái này
import lombok.Data;

@Data
public class QuestionCreateDTO {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    // SỬA: Thêm @NotNull để bắt buộc chọn phòng ban
    @NotNull(message = "Vui lòng chọn đơn vị tiếp nhận") 
    private Integer departmentId; 
    
    @NotBlank(message = "Vui lòng chọn Khoa")
    private String faculty;

    @NotBlank(message = "Vui lòng chọn Khóa")
    private String cohort;
}