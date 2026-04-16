package org.software.lms.controller;

import org.software.lms.model.Submission;
import org.software.lms.dto.SubmissionResponse;
import org.software.lms.service.AssignmentService;
import org.software.lms.service.FileStorageService;
import org.software.lms.service.FileStorageService.FileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.software.lms.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/assignments")
public class SubmissionController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserService userService;

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "application/pdf",
            "text/plain"
    );

    private SubmissionResponse convertToDTO(Submission submission) {
        SubmissionResponse response = new SubmissionResponse();
        response.setId(submission.getId());
        response.setStudentId(submission.getStudentId());
        response.setFilePath(submission.getFilePath());
        response.setGrade(submission.getGrade());
        response.setFeedback(submission.getFeedback());
        response.setSubmittedAt(submission.getSubmittedAt());

        SubmissionResponse.AssignmentInfo assignmentInfo = new SubmissionResponse.AssignmentInfo();
        assignmentInfo.setId(submission.getAssignment().getId());
        assignmentInfo.setTitle(submission.getAssignment().getTitle());
        assignmentInfo.setDescription(submission.getAssignment().getDescription());
        assignmentInfo.setDueDate(submission.getAssignment().getDueDate());

        response.setAssignment(assignmentInfo);
        return response;
    }

    @PostMapping("/{assignmentId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionResponse> submitAssignment(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file) {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Long studentId = userService.getUserByEmail(userEmail).getId();

        if (!file.getContentType().equals("application/pdf") &&
                !file.getContentType().equals("text/plain")) {
            throw new IllegalArgumentException("Invalid file type. Only PDF and TXT files are allowed.");
        }

        String storedFileName = fileStorageService.storeFile(file, courseId, assignmentId, studentId);
        Submission submission = assignmentService.submitAssignment(courseId, assignmentId, studentId, storedFileName);

        return ResponseEntity.ok(convertToDTO(submission));
    }

    @GetMapping("/{assignmentId}/submissions/{submissionId}/view")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<?> viewSubmission(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId) throws IOException {

        Submission submission = assignmentService.getSubmission(submissionId);
        // ... validation code remains the same ...

        FileResponse fileResponse = fileStorageService.viewFile(submission.getFilePath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileResponse.getFileType()));
        headers.setContentDisposition(ContentDisposition.inline()
                .filename(fileResponse.getFileName())
                .build());

        return new ResponseEntity<>(
                fileResponse.getContentAsBytes(),
                headers,
                HttpStatus.OK
        );
    }

}