package org.software.lms.repository;

import org.software.lms.model.Lesson;
import org.software.lms.model.LessonAttendance;
import org.software.lms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonAttendanceRepository extends JpaRepository<LessonAttendance, Long> {
    Optional<LessonAttendance> findByLessonAndStudent(Lesson lesson, User student);
    List<LessonAttendance> findByLesson(Lesson lesson);
    List<LessonAttendance> findByStudent(User student);
    boolean existsByLessonIdAndStudentIdAndPresent(Long lessonId, Long studentId, Boolean present);
}