package com.hcmute.qaute.service;

import com.hcmute.qaute.entity.Notification;
import com.hcmute.qaute.entity.User;
import java.util.List;

public interface NotificationService {
    void notifyUser(User user, String title, String body, String type, String referenceId);

    List<Notification> getUnreadNotifications(User user);

    void markAsRead(Long notificationId);

    void markAllAsRead(User user);
}
