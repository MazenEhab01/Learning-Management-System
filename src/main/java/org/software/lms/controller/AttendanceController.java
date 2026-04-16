package org.software.lms.controller;

import org.software.lms.model.User;
import org.software.lms.service.AttendanceService;
import org.software.lms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/lessons/{lessonId}/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final UserService userService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService, UserService userService) {
        this.attendanceService = attendanceService;
        this.userService = userService;
    }

    @PostMapping("/generate-otp")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<String> generateOTP(
            @PathVariable Long courseId,
            @PathVariable Long lessonId) {
        String otp = attendanceService.generateOTP(courseId, lessonId);
        return ResponseEntity.ok(otp);
    }

    @PostMapping("/mark")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<LessonAttendance> markAttendance(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestParam String otp) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Long studentId = userService.getUserByEmail(userEmail).getId();

        LessonAttendance attendance = attendanceService.markAttendance(courseId, lessonId, studentId, otp);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<LessonAttendance>> getAttendanceForLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId) {
        List<LessonAttendance> attendanceList = attendanceService.getLessonAttendance(courseId, lessonId);
        return ResponseEntity.ok(attendanceList);
    }

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    public ResponseEntity<List<LessonAttendance>> getStudentAttendance(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @PathVariable Long studentId) {
        // Security: If Student, they can only view their own attendance
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.getUserByEmail(userEmail);
        if (user.getRole().name().equals("STUDENT") && !user.getId().equals(studentId)) {
            throw new org.springframework.security.access.AccessDeniedException("Cannot view other students' attendance");
        }

        List<LessonAttendance> attendanceList = attendanceService.getStudentAttendance(courseId, lessonId, studentId);
        return ResponseEntity.ok(attendanceList);
    }

    @GetMapping("/students/{studentId}/exists")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'STUDENT', 'ADMIN')")
    public ResponseEntity<Boolean> hasAttendance(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @PathVariable Long studentId) {
        // Security check for students
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.getUserByEmail(userEmail);
        if (user.getRole().name().equals("STUDENT") && !user.getId().equals(studentId)) {
            throw new org.springframework.security.access.AccessDeniedException("Access denied");
        }

        boolean hasAttendance = attendanceService.hasAttendance(courseId, lessonId, studentId);
        return ResponseEntity.ok(hasAttendance);
    }
}
