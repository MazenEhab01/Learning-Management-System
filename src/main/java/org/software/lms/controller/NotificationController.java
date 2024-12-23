package org.software.lms.controller;

import org.software.lms.model.Notification;
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
        @RequestParam String title,
        @RequestParam String message,
        @RequestParam Long userId) {
        Notification notification = notificationService.createNotification(title, message, userId);
        return ResponseEntity.ok(notification);
    }

    // Endpoint for all notifications
     @GetMapping("/{userId}/all")
     public ResponseEntity<List<Notification>> getAllNotifications(@RequestParam Long userId) {
         List<Notification> notifications = notificationService.getAllNotifications(userId);
         return ResponseEntity.ok(notifications);
     }
 
    // Endpoint for unread notifications
    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }
}
