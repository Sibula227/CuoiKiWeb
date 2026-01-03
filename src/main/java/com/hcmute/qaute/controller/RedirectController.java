package com.hcmute.qaute.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    // 1. Nếu gõ /student -> Tự động chuyển về /student/dashboard
    @GetMapping("/student")
    public String redirectStudent() {
        return "redirect:/student/dashboard";
    }

    // 2. Nếu gõ /admin -> Tự động chuyển về /admin/dashboard
    @GetMapping("/admin")
    public String redirectAdmin() {
        return "redirect:/admin/dashboard";
    }
    
    // (Optional) Xử lý luôn trường hợp user lỡ tay gõ thêm dấu gạch chéo
    // Ví dụ: /student/ -> vẫn về dashboard
    @GetMapping("/student/")
    public String redirectStudentSlash() {
        return "redirect:/student/dashboard";
    }

    @GetMapping("/admin/")
    public String redirectAdminSlash() {
        return "redirect:/admin/dashboard";
    }
}