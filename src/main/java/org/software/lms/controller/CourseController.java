package org.software.lms.controller;

import org.software.lms.model.*;
import org.software.lms.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final NotificationController notifControl;

    public CourseController(CourseService courseService, NotificationController notifControl) {
        this.notifControl = notifControl;
        this.courseService = courseService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public Course createCourse(@RequestBody Course course) {
        return courseService.createCourse(course);
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public Course updateCourse(@PathVariable Long CourseId, @RequestBody Course updatedCourse) {
        Course course = courseService.updateCourse(CourseId, updatedCourse);
        // List<User> enrolledStudents = findAllStudentsEnrolledInCourse();
        // for (User stud : enrolledStudents) {
        //     Long StudId = stud.getId();
        //     String title = "New Instructor";
        //     String message = "Instructor x has been added to this course";
        //     notifControl.createNotification(StudId, CourseId, title, message);
        // }
        return course;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public void deleteCourse(@PathVariable Long id) {
        // List<User> enrolledStudents = findAllStudentsEnrolledInCourse(id);
        // for (User stud : enrolledStudents) {
        //     Long StudId = stud.getId();
        //     String title = "New Instructor";
        //     String message = "Instructor x has been added to this course";
        //     notifControl.createNotification(StudId, id, title, message);
        // }
        courseService.deleteCourse(id);
    }

    @GetMapping("/search/by-title/{title}")
    public List<Course> findCoursesByTitle(@PathVariable String title) {
        return courseService.findCoursesByTitle(title);
    }
    @GetMapping("/search/by-instructor/{instructorId}")
    public List<Course> findCoursesByInstructorId(@PathVariable Long instructorId) {
        return courseService.findCoursesByInstructorId(instructorId);
    }
    @GetMapping("/search/by-created-date/{createdAt}")
    public List<Course> findCoursesByCreatedAtAfter(@PathVariable java.util.Date createdAt) {
        return courseService.findCoursesByCreatedAtAfter(createdAt);
    }
    @GetMapping("/{id}/studentEnrolled")
    public List<User> findStudentEnrolledInCourse(@PathVariable Long id) {
        return courseService.findStudentEnrolledInCourse(id);
    }
    @PostMapping("/{id}/instructors")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<Course> addInstructorsToCourse(@PathVariable Long CourseId, @RequestBody List<Long> instructorIds) {
        Course course = courseService.addInstructorsToCourse(CourseId, instructorIds);
        // List<User> enrolledStudents = findAllStudentsEnrolledInCourse();
        // for (User stud : enrolledStudents) {
        //     Long StudId = stud.getId();
        //     String title = "New Instructor";
        //     String message = "Instructor x has been added to this course";
        //     notifControl.createNotification(StudId, CourseId, title, message);
        // }
        return ResponseEntity.ok(course);
    }


    @PostMapping("/{id}/students")
    public ResponseEntity<Course> addStudentsToCourse(@PathVariable Long id, @RequestBody List<Long> studentIds) {
        Course course = courseService.addStudentsToCourse(id, studentIds);
        Long courseId = course.getId();
        String message = "Enrollment successful";
        for (Long studentId: studentIds) {
            notifControl.createNotification(studentId, courseId, "Enrollment Confirmation", message);
        }
        return ResponseEntity.ok(course);
    }


    @PostMapping("/{id}/lessons")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<Course> addLessonsToCourse(@PathVariable Long id, @RequestBody List<Long> lessonIds) {
        Course course = courseService.addLessonsToCourse(id, lessonIds);
        // List<User> enrolledStudents = findAllStudentsEnrolledInCourse();
        // for (User stud : enrolledStudents) {
        //     Long StudId = stud.getId();
        //     String title = "New Instructor";
        //     String message = "Instructor x has been added to this course";
        //     notifControl.createNotification(StudId, CourseId, title, message);
        // }
        return ResponseEntity.ok(course);
    }
    @PutMapping("/{id}/lessons")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<Course> updateLessonsOfCourse(@PathVariable Long CourseId, @RequestBody List<Long> lessonIds) {
        Course course = courseService.updateLessonsOfCourse(CourseId, lessonIds);
        // List<User> enrolledStudents = findAllStudentsEnrolledInCourse();
        // for (User stud : enrolledStudents) {
        //     Long StudId = stud.getId();
        //     String title = "New Instructor";
        //     String message = "Instructor x has been added to this course";
        //     notifControl.createNotification(StudId, CourseId, title, message);
        // }
        return ResponseEntity.ok(course);
    }
    @DeleteMapping("/{id}/instructors/{instructorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<Void> deleteInstructorFromCourse(@PathVariable Long id, @PathVariable Long instructorId) {
        courseService.deleteInstructorFromCourse(id, instructorId);
        // List<User> enrolledStudents = findAllStudentsEnrolledInCourse();
        // for (User stud : enrolledStudents) {
        //     Long StudId = stud.getId();
        //     String title = "New Instructor";
        //     String message = "Instructor x has been added to this course";
        //     notifControl.createNotification(StudId, CourseId, title, message);
        // }
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}/students/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<Void> deleteStudentFromCourse(@PathVariable Long id, @PathVariable Long studentId) {
        courseService.deleteStudentFromCourse(id, studentId);
        notifControl.createNotification(studentId, id, "Removal", "You have been removed");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<Void> deleteLessonFromCourse(@PathVariable Long CourseId, @PathVariable Long lessonId) {
        courseService.deleteLessonFromCourse(CourseId, lessonId);
        // List<User> enrolledStudents = findAllStudentsEnrolledInCourse();
        // for (User stud : enrolledStudents) {
        //     Long StudId = stud.getId();
        //     String title = "New Instructor";
        //     String message = "Instructor x has been added to this course";
        //     notifControl.createNotification(CourseId, CourseId, title, message);
        // }
        return ResponseEntity.noContent().build();
    }
}