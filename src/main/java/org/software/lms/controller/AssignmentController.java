package org.software.lms.controller;

import org.software.lms.dto.GradeSubmissionRequest;
import org.software.lms.model.Assignment;
import org.software.lms.model.Submission;
import org.software.lms.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    // Configure this in application.properties
    private static final String UPLOAD_DIR = "uploads";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(@RequestBody Assignment assignment) {
        Assignment createdAssignment = assignmentService.createAssignment(assignment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssignment);
    }

    // Submit Assignment
    @PostMapping("/{assignmentId}/submit")
    public ResponseEntity<Submission> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file,
            @RequestParam Long studentId) {

        boolean studentExists = assignmentService.checkIfStudentExists(studentId);
        if (!studentExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null); // or return a meaningful error message
        }
        if (!assignmentService.checkIfAssignmentExists(assignmentId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Assignment not found
        }

        // Store file and create submission object
        String filePath = saveFile(file);  // Implement the file saving logic
        Submission submission = new Submission();
        submission.setAssignmentId(assignmentId);
        submission.setStudentId(studentId);
        submission.setFilePath(filePath);

        Submission submittedAssignment = assignmentService.submitAssignment(submission);
        return ResponseEntity.status(HttpStatus.CREATED).body(submittedAssignment);
    }

    public String saveFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get("uploads", fileName);

        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
        return filePath.toString();
    }




    @GetMapping("/{assignmentId}/submissions")
    public ResponseEntity<List<Submission>> getSubmissionsForAssignment(@PathVariable Long assignmentId) {
        List<Submission> submissions = assignmentService.getSubmissionsByAssignment(assignmentId);
        return ResponseEntity.ok(submissions);
    }

    @PostMapping("/{submissionId}/grade")
    public ResponseEntity<Submission> gradeAssignment(
            @PathVariable Long submissionId,
            @RequestBody GradeSubmissionRequest request) {

        Submission gradedSubmission = assignmentService.gradeAssignment(
                submissionId,
                request.getGrade(),
                request.getFeedback()
        );
        return ResponseEntity.ok(gradedSubmission);
    }
}