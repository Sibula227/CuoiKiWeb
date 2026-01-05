package com.hcmute.qaute.service.impl;

import com.hcmute.qaute.entity.Notification;
import com.hcmute.qaute.entity.User;
import com.hcmute.qaute.repository.NotificationRepository;
import com.hcmute.qaute.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void notifyUser(User user, String title, String body, String type, String referenceId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getUnreadNotifications(User user) {
        // Giả sử có query findByUserIdAndIsReadFalseOrderByCreatedAtDesc
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    @Override
    public void markAllAsRead(User user) {
        List<Notification> unreads = getUnreadNotifications(user);
        for (Notification n : unreads) {
            n.setIsRead(true);
        }
        notificationRepository.saveAll(unreads);
    }
}
