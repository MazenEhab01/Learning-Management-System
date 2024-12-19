package org.software.lms.controller;

import org.software.lms.dto.CourseDto;
import org.software.lms.model.Course;
import org.software.lms.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course updatedCourse) {
        return courseService.updateCourse(id, updatedCourse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCourse(@PathVariable Long id) {
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
    @PostMapping("/{id}/instructors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> addInstructorsToCourse(@PathVariable Long id, @RequestBody List<Long> instructorIds) {
        return ResponseEntity.ok(courseService.addInstructorsToCourse(id, instructorIds));
    }

    @PutMapping("/{id}/instructors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> updateInstructorsToCourse(@PathVariable Long id, @RequestBody List<Long> instructorIds) {
        Course updatedCourse = courseService.updateInstructorsToCourse(id, instructorIds);
        return ResponseEntity.ok(updatedCourse);
    }


    @PostMapping("/{id}/students")
    public ResponseEntity<Course> addStudentsToCourse(@PathVariable Long id, @RequestBody List<Long> studentIds) {
        return ResponseEntity.ok(courseService.addStudentsToCourse(id, studentIds));
    }

    @PutMapping("/{id}/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> updateStudentsOfCourse(@PathVariable Long id, @RequestBody List<Long> studentIds) {
        return ResponseEntity.ok(courseService.updateStudentsOfCourse(id, studentIds));
    }

    @PostMapping("/{id}/lessons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> addLessonsToCourse(@PathVariable Long id, @RequestBody List<Long> lessonIds) {
        return ResponseEntity.ok(courseService.addLessonsToCourse(id, lessonIds));
    }
    @PutMapping("/{id}/lessons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> updateLessonsOfCourse(@PathVariable Long id, @RequestBody List<Long> lessonIds) {
        return ResponseEntity.ok(courseService.updateLessonsOfCourse(id, lessonIds));
    }
    @DeleteMapping("/{id}/instructors/{instructorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteInstructorFromCourse(@PathVariable Long id, @PathVariable Long instructorId) {
        courseService.deleteInstructorFromCourse(id, instructorId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}/students/{studentId}")
    public ResponseEntity<Void> deleteStudentFromCourse(@PathVariable Long id, @PathVariable Long studentId) {
        courseService.deleteStudentFromCourse(id, studentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/lessons/{lessonId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLessonFromCourse(@PathVariable Long id, @PathVariable Long lessonId) {
        courseService.deleteLessonFromCourse(id, lessonId);
        return ResponseEntity.noContent().build();
    }
}

