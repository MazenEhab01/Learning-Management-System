package org.software.lms.service;

import org.software.lms.model.Notification;
import org.software.lms.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(String title, String message, Long userId) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setUserId(userId);
        return notificationRepository.save(notification);
    }

    // Get unread notifications
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndUnread(userId);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
    
    // Get all notifications
    public List<Notification> getAllNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
}
