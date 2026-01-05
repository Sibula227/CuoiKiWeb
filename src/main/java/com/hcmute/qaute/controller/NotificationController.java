package com.hcmute.qaute.controller;

import com.hcmute.qaute.entity.Notification;
import com.hcmute.qaute.repository.NotificationRepository;
import com.hcmute.qaute.repository.AnswerRepository;
import com.hcmute.qaute.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @GetMapping("/read/{id}")
    public String readNotification(@PathVariable Long id) {
        // 1. Mark as read
        notificationService.markAsRead(id);

        // 2. Redirect based on type
        return notificationRepository.findById(id).map(n -> {
            if ("NEW_ANSWER".equals(n.getType())) {
                try {
                    Long answerId = Long.parseLong(n.getReferenceId());
                    return answerRepository.findById(answerId)
                            .map(a -> "redirect:/student/question/" + a.getQuestion().getId())
                            .orElse("redirect:/student/dashboard");
                } catch (NumberFormatException e) {
                    return "redirect:/student/dashboard";
                }
            }
            return "redirect:/";
        }).orElse("redirect:/");
    }
}
