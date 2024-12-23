package org.software.lms.repository;

import org.software.lms.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId); // All notifications
    List<Notification> findByUserIdAndUnread(Long userId); //All unread notifications
}
