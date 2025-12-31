package com.hcmute.qaute.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addGlobalAttributes(HttpServletRequest request, Model model) {
        // Lấy đường dẫn hiện tại (ví dụ: /student/dashboard)
        String requestURI = request.getRequestURI();
        
        // Gửi xuống HTML với tên biến là 'currentUri'
        model.addAttribute("currentUri", requestURI);
    }
}