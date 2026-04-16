package org.software.lms.controller;

import jakarta.validation.Valid;
import org.software.lms.dto.*;
import org.software.lms.exception.ResourceNotFoundException;
import org.software.lms.model.Question;
import org.software.lms.model.Quiz;
import org.software.lms.model.QuizAttempt;
import org.software.lms.model.User;
import org.software.lms.service.QuizService;
import org.software.lms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses/{courseId}/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<QuizDTO> createQuiz(
            @Valid @RequestBody QuizDTO quizDTO,
            @PathVariable Long courseId) {
        QuizDTO createdQuiz = quizService.createQuiz(quizDTO, courseId);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    @GetMapping("/{quizId}/start")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<QuestionDTO>> startQuiz(
            @PathVariable Long courseId,
            @PathVariable Long quizId) {
        List<QuestionDTO> questions = quizService.generateRandomQuestions(courseId, quizId);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/{quizId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> submitQuiz(
            @PathVariable Long courseId,
            @PathVariable Long quizId,
            @Valid @RequestBody List<QuestionAnswerDTO> answers) {

        // Service now handles studentId retrieval from SecurityContext
        String feedback = quizService.submitQuizAttempt(courseId, quizId, answers, null);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/{quizId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<QuizDTO> getQuizById(
            @PathVariable Long courseId,
            @PathVariable Long quizId) {
        QuizDTO quizDTO = quizService.getQuizById(courseId, quizId);
        return ResponseEntity.ok(quizDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<QuizDTO>> getQuizzesByCourse(@PathVariable Long courseId) {
        List<QuizDTO> quizzes = quizService.getQuizzesByCourse(courseId);
        return ResponseEntity.ok(quizzes);
    }

    @DeleteMapping("/{quizId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Void> deleteQuiz(
            @PathVariable Long courseId,
            @PathVariable Long quizId) {
        quizService.deleteQuiz(courseId, quizId);
        return ResponseEntity.noContent().build();
    }
}

