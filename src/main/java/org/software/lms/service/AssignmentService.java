package org.software.lms.service;

import org.software.lms.exception.ResourceNotFoundException;
import org.software.lms.model.Assignment;
import org.software.lms.model.Course;
import org.software.lms.model.Submission;
import org.software.lms.repository.AssignmentRepository;
import org.software.lms.repository.CourseRepository;
import org.software.lms.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    public Assignment createAssignment(Long courseId, Assignment assignment) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        assignment.setCourse(course);
        return assignmentRepository.save(assignment);
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'STUDENT')")
    public Assignment getAssignment(Long courseId, Long assignmentId) {
        return assignmentRepository.findByCourseIdAndId(courseId, assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    public Assignment updateAssignment(Long courseId, Long assignmentId, Assignment assignmentDetails) {
        Assignment assignment = getAssignment(courseId, assignmentId);

        assignment.setTitle(assignmentDetails.getTitle());
        assignment.setDescription(assignmentDetails.getDescription());
        assignment.setDueDate(assignmentDetails.getDueDate());

        return assignmentRepository.save(assignment);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    public void deleteAssignment(Long courseId, Long assignmentId) {
        Assignment assignment = getAssignment(courseId, assignmentId);
        assignmentRepository.delete(assignment);
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'STUDENT')")
    public List<Assignment> getAssignmentsByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found");
        }
        return assignmentRepository.findByCourseId(courseId);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public Submission submitAssignment(Long courseId, Long assignmentId, Long studentId, String filePath) {
        Assignment assignment = getAssignment(courseId, assignmentId);

        // Verify student is enrolled in the course
        Course course = assignment.getCourse();
        boolean isEnrolled = course.getStudentEnrolledCourses().stream()
                .anyMatch(student -> student.getId().equals(studentId));

        if (!isEnrolled) {
            throw new IllegalStateException("Student is not enrolled in this course");
        }

        if (assignment.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Assignment submission deadline has passed");
        }

        if (submissionRepository.existsByAssignmentIdAndStudentId(assignmentId, studentId)) {
            throw new IllegalStateException("You have already submitted this assignment");
        }

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudentId(studentId);
        submission.setFilePath(filePath);

        return submissionRepository.save(submission);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    public Submission gradeSubmission(Long courseId, Long submissionId, Double grade, String feedback) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        // Verify submission belongs to the course
        if (!submission.getAssignment().getCourse().getId().equals(courseId)) {
            throw new IllegalStateException("Submission does not belong to this course");
        }

        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100");
        }

        submission.setGrade(grade);
        submission.setFeedback(feedback);
        return submissionRepository.save(submission);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public Submission updateSubmission(Long courseId, Long assignmentId, Long studentId, String newFilePath) {
        Submission submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        Assignment assignment = submission.getAssignment();
        if (assignment.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Assignment submission deadline has passed");
        }

        submission.setFilePath(newFilePath);
        submission.setSubmittedAt(LocalDateTime.now());
        return submissionRepository.save(submission);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    public List<Submission> getSubmissionsByAssignment(Long courseId, Long assignmentId) {
        Assignment assignment = getAssignment(courseId, assignmentId);
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'STUDENT')")
     public List<Submission> getStudentSubmissions(Long courseId, Long studentId) {
        if (!courseRepository.existsById(courseId)) {
           throw new ResourceNotFoundException("Course not found");
        }
        return submissionRepository.findByAssignmentCourseIdAndStudentId(courseId, studentId);
    }
}