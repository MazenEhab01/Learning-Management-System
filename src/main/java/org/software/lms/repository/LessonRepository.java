package org.software.lms.repository;
import org.software.lms.model.Lesson;
import org.software.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourse_Id(Long courseId);
    boolean existsById(Long lessonId);
}