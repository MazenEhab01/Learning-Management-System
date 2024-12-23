package org.software.lms.repository;

import org.software.lms.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourseId(Long courseId);
    boolean existsByCourseIdAndTitle(Long courseId, String title);
    Optional<Assignment> findByCourseIdAndId(Long courseId, Long assignmentId);
}