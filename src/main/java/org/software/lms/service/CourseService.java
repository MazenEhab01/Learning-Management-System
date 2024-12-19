package org.software.lms.service;

import org.software.lms.model.Course;
import org.software.lms.model.Lesson;
import org.software.lms.model.Role;
import org.software.lms.model.User;
import org.software.lms.repository.CourseRepository;
import org.software.lms.repository.LessonRepository;
import org.software.lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private  UserRepository userRepository ;
    @Autowired
    private LessonRepository lessonRepository ;
    @Autowired
    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course updateCourse(Long id, Course updatedCourse) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setTitle(updatedCourse.getTitle());
                    course.setDescription(updatedCourse.getDescription());
                    course.setDuration(updatedCourse.getDuration());
                    course.setLessons(updatedCourse.getLessons());
                    course.setInstructors(updatedCourse.getInstructors());
                    course.updateTimestamp();
                    return courseRepository.save(course);
                }).orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> findCoursesByTitle(String title) {
        return courseRepository.findByTitle(title);
    }

    public List<Course> findCoursesByInstructorId(Long instructorId) {
        return courseRepository.findByInstructors_Id(instructorId);
    }

    public List<Course> findCoursesByCreatedAtAfter(Date createdAt) {
        return courseRepository.findByCreatedAtAfter(createdAt);
    }

    public Course addInstructorsToCourse(Long courseId, List<Long> instructorIds) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        List<User> instructors = userRepository.findAllById(instructorIds);

        if (instructors.isEmpty()) {
            throw new RuntimeException("No instructors found with provided IDs");
        }
        for (User user : instructors) {
            if (user.getRole() == Role.INSTRUCTOR) course.addInstructor(user);

        }
        return courseRepository.save(course);
    }

    public Course addStudentsToCourse(Long courseId, List<Long> studentsIds) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        List<User> students = userRepository.findAllById(studentsIds);

        if (students.isEmpty()) {
            throw new RuntimeException("No instructors found with provided IDs");
        }

        for (User user : students) {
            if (user.getRole() == Role.STUDENT) course.addEnrolledStudent(user);
        }

        return courseRepository.save(course);
    }

    public Course addLessonsToCourse(Long courseId, List<Long> lessonIds) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        List<Lesson> lesson = lessonRepository.findAllById(lessonIds);

        if (lesson.isEmpty()) {
            throw new RuntimeException("No instructors found with provided IDs");
        }
        course.getLessons().addAll(lesson);

        return courseRepository.save(course);
    }

}
