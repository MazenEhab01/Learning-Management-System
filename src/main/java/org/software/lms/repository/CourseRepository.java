package org.software.lms.repository;

import org.software.lms.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTitle(String title);
    List<Course> findByInstructors_Id(Long instructorId);
    List<Course> findByCreatedAtAfter(java.util.Date createdAt);
    Optional<Course> findById(Long id);
}