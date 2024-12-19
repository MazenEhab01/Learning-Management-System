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
import java.util.Base64;
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

        // Populate fileContent for each submission
        submissions.forEach(submission -> {
            String filePath = submission.getFilePath();
            try {
                Path path = Paths.get(filePath);
                byte[] fileBytes = Files.readAllBytes(path);
                // Convert binary content to Base64 string
                String base64Content = Base64.getEncoder().encodeToString(fileBytes);
                submission.setFileContent(base64Content.getBytes()); // Store as bytes or update your model to use String
            } catch (IOException e) {
                throw new RuntimeException("Failed to read file: " + filePath, e);
            }
        });

        return ResponseEntity.ok(submissions);
    }


    @GetMapping("/{assignmentId}/submissions/{submissionId}/download")
    public ResponseEntity<byte[]> downloadSubmissionFile(@PathVariable Long assignmentId, @PathVariable Long submissionId) {
        Submission submission = assignmentService.getSubmissionById(submissionId);

        // Check if the submission belongs to the assignment
        if (!submission.getAssignmentId().equals(assignmentId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null); // or throw a custom exception
        }

        String filePath = submission.getFilePath();
        try {
            Path path = Paths.get(filePath);
            byte[] fileBytes = Files.readAllBytes(path);

            // Determine the file name
            String fileName = path.getFileName().toString();

            // Set response headers for file download
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }
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
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<String> deleteAssignment(@PathVariable Long assignmentId) {
        // Check if the assignment exists
        if (!assignmentService.checkIfAssignmentExists(assignmentId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Assignment not found with id: " + assignmentId);
        }

        // Call the service to delete the assignment
        assignmentService.deleteAssignment(assignmentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Assignment with id " + assignmentId + " has been deleted successfully.");
    }

}