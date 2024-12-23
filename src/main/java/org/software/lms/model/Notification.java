package org.software.lms.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = true)
    private Course course;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Boolean isRead;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(User user, Course course, String title, String message) {
        this.user = user;
        this.course = course;
        this.title = title;
        this.message = message;
        isRead = false;
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Course getCourse() { return course; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public Boolean IsRead() { return isRead; }
    public void markAsRead() { isRead = true; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
