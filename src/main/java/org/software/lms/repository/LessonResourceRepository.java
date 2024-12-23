package org.software.lms.repository;

import org.software.lms.model.Lesson;
import org.software.lms.model.LessonResource;
import org.software.lms.model.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonResourceRepository extends JpaRepository<LessonResource, Long> {
    List<LessonResource> findByLessonId(Long lessonId);
}

