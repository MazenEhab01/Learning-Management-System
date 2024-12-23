package org.software.lms.service;

import jakarta.transaction.Transactional;
import org.software.lms.exception.ResourceNotFoundException;
import org.software.lms.exception.InvalidOTPException;
import org.software.lms.exception.OTPExpiredException;
import org.software.lms.model.Lesson;
import org.software.lms.model.LessonAttendance;
import org.software.lms.model.User;
import org.software.lms.repository.LessonAttendanceRepository;
import org.software.lms.repository.LessonRepository;
import org.software.lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AttendanceService {
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonAttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    public LessonAttendance markAttendance(Long lessonId, Long studentId, String otp) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Check if student is enrolled in the course
        if (!lesson.getCourse().getStudentEnrolledCourses().contains(student)) {
            throw new IllegalStateException("Student is not enrolled in this course");
        }

        // Check for existing attendance
        Optional<LessonAttendance> existingAttendance = attendanceRepository
                .findByLessonAndStudent(lesson, student);

        if (existingAttendance.isPresent() && existingAttendance.get().getPresent()) {
            throw new IllegalStateException("Attendance already marked for this lesson");
        }

        // Verify OTP
        if (!lesson.getCurrentOTP().equals(otp)) {
            throw new InvalidOTPException("Invalid OTP provided");
        }

        // Check if OTP is expired (30 minutes validity)
        if (new Date().getTime() - lesson.getOtpGeneratedAt().getTime() > 30 * 60 * 1000) {
            throw new OTPExpiredException("OTP has expired");
        }

        // Create or update attendance record
        LessonAttendance attendance = existingAttendance.orElse(new LessonAttendance());
        attendance.setLesson(lesson);
        attendance.setStudent(student);
        attendance.setAttendanceDate(new Date());
        attendance.setOtpUsed(otp);
        attendance.setPresent(true);

        return attendanceRepository.save(attendance);
    }

    public List<LessonAttendance> getLessonAttendance(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
        return attendanceRepository.findByLesson(lesson);
    }

    public List<LessonAttendance> getStudentAttendance(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return attendanceRepository.findByStudent(student);
    }

    public boolean hasAttendance(Long lessonId, Long studentId) {
        return attendanceRepository.existsByLessonIdAndStudentIdAndPresent(lessonId, studentId, true);
    }
}