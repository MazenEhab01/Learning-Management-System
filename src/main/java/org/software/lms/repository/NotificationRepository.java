package org.software.lms.repository;

import org.software.lms.model.Notification;
import org.springframework.data.jpa.repository.*;
import jakarta.transaction.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId")
    List<Notification> findByUserId(Long userId); // All notifications

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    List<Notification> findByUserIdAndIsReadFalse(Long userId); //All unread notifications
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId")
    void markAsRead(Long notificationId);
}
