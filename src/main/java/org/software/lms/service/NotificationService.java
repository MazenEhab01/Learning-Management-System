package org.software.lms.service;

import org.software.lms.model.*;
import org.software.lms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    public Notification createNotification(Long userId, Long courseId, String title, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Notification notification = new Notification(user, course, title, message);
        return notificationRepository.save(notification);
    }

    // Get all notifications
    public List<Notification> findByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    // Get all unread notifications
    public List<Notification> findByUserIdAndIsReadFalse(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }

    // Mark notification as read
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

}
