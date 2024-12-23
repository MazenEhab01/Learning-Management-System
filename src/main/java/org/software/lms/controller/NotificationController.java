package org.software.lms.controller;

import org.software.lms.model.*;
import org.software.lms.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestParam Long userId,
            @RequestParam Long courseId,
            @RequestParam String title,
            @RequestParam String message) {
        Notification notification = notificationService.createNotification(userId, courseId, title, message);
        return ResponseEntity.ok(notification);
    }

    // Get all notifications for a user
    @GetMapping("/{userId}/all")
    public ResponseEntity<List<Notification>> findByUserId(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.findByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    // Get unread notifications for a user
    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<Notification>> findByUserIdAndIsReadFalse(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.findByUserIdAndIsReadFalse(userId);
        return ResponseEntity.ok(notifications);
    }

    // Mark a notification as read
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }
}
