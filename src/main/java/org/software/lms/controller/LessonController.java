//package org.software.lms.controller;
//
//import org.software.lms.model.Lesson;
//import org.software.lms.service.LessonService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.software.lms.model.User;
//
//import java.util.*;
//
//@RestController
//@RequestMapping("/api/lessons")
//public class LessonController {
//
//    @Autowired
//    private LessonService lessonService;
//
//    @GetMapping
//    public List<Lesson> getAllLessons() {
//        return lessonService.getAllLessons();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Lesson> getLessonById(
//            @PathVariable Long id,
//            @RequestParam(required = false) String otp) {
//
//        Optional<Lesson> lessonOtp = lessonService.getLessonById(id);
//        if (lessonOtp.isPresent()) {
//            Lesson lesson = lessonOtp.get();
//
//            // Check OTP if provided
//            if (otp != null && lesson.isOtpValid() && lesson.getOtp().equals(otp)) {
//                return ResponseEntity.ok(lesson);
//            }
//
//            // Restrict access if OTP is not valid
//            if (otp == null || !lesson.isOtpValid() || !lesson.getOtp().equals(otp)) {
//                return ResponseEntity.status(403).body(null);
//            }
//        }
//        return ResponseEntity.notFound().build();
//    }
//
//
//    @PostMapping
//    public Lesson createLesson(@RequestBody Lesson lesson) {
//        return lessonService.saveLesson(lesson);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Lesson> updateLesson(@PathVariable Long id, @RequestBody Lesson lesson) {
//        if (lessonService.getLessonById(id).isPresent()) {
//            lesson.setId(id);
//            return ResponseEntity.ok(lessonService.saveLesson(lesson));
//        }
//        return ResponseEntity.notFound().build();
//    }
//
//    @PutMapping("/{id}/generate-otp")
//    public ResponseEntity<String> generateOtp(@PathVariable Long id) {
//        Optional<Lesson> lessonOpt = lessonService.getLessonById(id);
//        if (lessonOpt.isPresent()) {
//            Lesson lesson = lessonOpt.get();
//            String newOtp = UUID.randomUUID().toString().substring(0, 6); // Generate 6-character OTP
//            Date expirationTime = new Date(System.currentTimeMillis() + (15 * 60 * 1000)); // Valid for 15 minutes
//            lesson.updateOtp(newOtp, expirationTime);
//            lessonService.saveLesson(lesson);
//            return ResponseEntity.ok("OTP generated successfully: " + newOtp);
//        }
//        return ResponseEntity.notFound().build();
//    }
//
//    @PostMapping("/{lessonId}/attend")
//    public ResponseEntity<String> attendLesson(@PathVariable Long lessonId, @RequestParam String otp, @RequestBody User student) {
//        Optional<Lesson> lessonOpt = lessonService.getLessonById(lessonId);
//        if (lessonOpt.isPresent()) {
//            Lesson lesson = lessonOpt.get();
//            if (lesson.isOtpValid() && lesson.getOtp().equals(otp)) {
//                lesson.addAttendance(student);
//                lessonService.saveLesson(lesson);
//                return ResponseEntity.ok("Attendance marked successfully.");
//            }
//            return ResponseEntity.status(400).body("Invalid or expired OTP.");
//        }
//        return ResponseEntity.status(404).body("Lesson not found.");
//    }
//
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
//        if (lessonService.getLessonById(id).isPresent()) {
//            lessonService.deleteLesson(id);
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.notFound().build();
//    }
//}
