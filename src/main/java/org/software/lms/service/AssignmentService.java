package org.software.lms.service;


import org.software.lms.exception.ResourceNotFoundException;
import org.software.lms.model.Assignment;
import org.software.lms.model.Submission;
import org.software.lms.model.User;
import org.software.lms.repository.AssignmentRepository;
import org.software.lms.repository.UserRepository;
import org.software.lms.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {
    Assignment assignment;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @PreAuthorize("hasRole('ADMIN') OR hasRole('INSTRUCTOR')")
    public Assignment createAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }
    @PreAuthorize("hasRole('STUDENT') ")
    public Submission submitAssignment(Submission submission) {
        return submissionRepository.save(submission);
    }
    @PreAuthorize("hasRole('INSTRUCTOR') ")
    public Submission gradeAssignment(Long submissionId, Double grade, String feedback) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        submission.setGrade(grade);
        submission.setFeedback(feedback);
        return submissionRepository.save(submission);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') ")
    public List<Submission> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') ")
    public List<Submission> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }
    public boolean checkIfStudentExists(Long studentId) {
        // Replace this with the actual logic to check if a student exists
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + studentId));

        if (user!=null)return true;
        return false;
    }
    // Check if the assignment exists
    public boolean checkIfAssignmentExists(Long assignmentId) {
        return assignmentRepository.existsById(assignmentId);
    }

    @PreAuthorize("hasRole('ADMIN') OR hasRole('INSTRUCTOR')")
    public void deleteAssignment(Long assignmentId) {
        // Check if the assignment exists
        if (!assignmentRepository.existsById(assignmentId)) {
            throw new ResourceNotFoundException("Assignment not found with id: " + assignmentId);
        }

        // Delete the assignment
        assignmentRepository.deleteById(assignmentId);
    }

    public Submission getSubmissionById(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));
    }

}
