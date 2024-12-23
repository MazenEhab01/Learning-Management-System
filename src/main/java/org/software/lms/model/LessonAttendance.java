package org.software.lms.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "lesson_attendance")

public class LessonAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private Date attendanceDate;

    private String otpUsed;

    @Column(nullable = false)
    private Boolean present;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Date getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getOtpUsed() {
        return otpUsed;
    }

    public void setOtpUsed(String otpUsed) {
        this.otpUsed = otpUsed;
    }

    public Boolean getPresent() {
        return present;
    }

    public void setPresent(Boolean present) {
        present = present;
    }



    public LessonAttendance(Long id, Lesson lesson, User student, Date attendanceDate, String otpUsed, Boolean isPresent, Integer attemptCount, Date lastAttemptTime) {
        this.id = id;
        this.lesson = lesson;
        this.student = student;
        this.attendanceDate = attendanceDate;
        this.otpUsed = otpUsed;
        this.present = present;
    }

    public LessonAttendance() {
    }
}
