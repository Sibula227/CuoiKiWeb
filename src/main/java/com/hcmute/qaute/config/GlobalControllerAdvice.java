package com.hcmute.qaute.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalControllerAdvice {

    @org.springframework.beans.factory.annotation.Autowired
    private com.hcmute.qaute.repository.NotificationRepository notificationRepository;

    @Autowired
    private com.hcmute.qaute.repository.UserRepository userRepository;

    @ModelAttribute
    public void addGlobalAttributes(HttpServletRequest request, Model model, java.security.Principal principal) {
        // Lấy đường dẫn hiện tại (ví dụ: /student/dashboard)
        String requestURI = request.getRequestURI();

        // Gửi xuống HTML với tên biến là 'currentUri'
        model.addAttribute("currentUri", requestURI);

        // --- NOTIFICATION COUNT ---
        if (principal != null) {
            String username = principal.getName();
            userRepository.findByUsername(username).ifPresent(user -> {
                long count = notificationRepository.countByUserIdAndIsReadFalse(user.getId());
                model.addAttribute("unreadNotificationCount", count);

                // Lấy 5 thông báo mới nhất để hiện popup
                model.addAttribute("latestNotifications", notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(user.getId()).stream().limit(5).toList());
            });
        }
        // --------------------------
    }
}