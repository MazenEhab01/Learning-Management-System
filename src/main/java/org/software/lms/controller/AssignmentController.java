package org.software.lms.controller;

import org.software.lms.model.Assignment;
import org.software.lms.model.Submission;
import org.software.lms.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;
    private NotificationController notifControl;

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(
            @PathVariable Long courseId,
            @RequestBody Assignment assignment) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assignmentService.createAssignment(courseId, assignment));
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<Assignment> getAssignment(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId) {
        return ResponseEntity.ok(assignmentService.getAssignment(courseId, assignmentId));
    }

    @GetMapping
    public ResponseEntity<List<Assignment>> getCourseAssignments(@PathVariable Long courseId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByCourse(courseId));
    }

    @PutMapping("/{assignmentId}")
    public ResponseEntity<Assignment> updateAssignment(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @RequestBody Assignment assignmentDetails) {
        return ResponseEntity.ok(assignmentService.updateAssignment(courseId, assignmentId, assignmentDetails));
    }

    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<Void> deleteAssignment(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId) {
        assignmentService.deleteAssignment(courseId, assignmentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{assignmentId}/submit")
    public ResponseEntity<Submission> submitAssignment(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @RequestParam Long studentId,
            @RequestParam String filePath) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assignmentService.submitAssignment(courseId, assignmentId, studentId, filePath));
    }

    @PutMapping("/{assignmentId}/submissions")
    public ResponseEntity<Submission> updateSubmission(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @RequestParam Long studentId,
            @RequestParam String filePath) {
        return ResponseEntity.ok(
                assignmentService.updateSubmission(courseId, assignmentId, studentId, filePath));
    }

    @PostMapping("/{assignmentId}/submissions/{submissionId}/grade")
    public ResponseEntity<Submission> gradeSubmission(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId,
            @RequestParam Double grade,
            @RequestParam String feedback) {
            Submission submission = assignmentService.gradeSubmission(courseId, submissionId, grade, feedback); 
            Long StudId = submission.getStudentId();
            String title = submission.getAssignment().getTitle() + " graded.";
            String message = "Your grade is " + grade; 
            notifControl.createNotification(StudId, courseId, title, message);
            return ResponseEntity.ok(submission);
    }

    @GetMapping("/{assignmentId}/submissions")
    public ResponseEntity<List<Submission>> getAssignmentSubmissions(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId) {
        return ResponseEntity.ok(assignmentService.getSubmissionsByAssignment(courseId, assignmentId));
    }

    @GetMapping("/submissions/student/{studentId}")
    public ResponseEntity<List<Submission>> getStudentSubmissions(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(assignmentService.getStudentSubmissions(courseId, studentId));
    }
}