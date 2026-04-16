import org.software.lms.model.*;
import org.software.lms.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;

    // To track start times without persisting until submission (if preferred)
    // Or we could persist a "started" attempt.
    private final Map<String, LocalDateTime> quizStartTimes = new ConcurrentHashMap<>();

    @Autowired
    public QuizServiceImpl(QuizRepository quizRepository,
                           QuestionRepository questionRepository,
                           CourseRepository courseRepository,
                           QuizAttemptRepository quizAttemptRepository,
                           UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userRepository = userRepository;
    }

    @Override
    public QuizDTO getQuizById(Long courseId, Long quizId) {
        Quiz quiz = quizRepository.findByCourseIdAndId(courseId, quizId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Quiz not found with id: " + quizId + " in course: " + courseId));

        QuizDTO quizDTO = new QuizDTO();
        BeanUtils.copyProperties(quiz, quizDTO);
        return quizDTO;
    }

    @Override
    public QuizDTO createQuiz(QuizDTO quizDTO, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        Quiz quiz = new Quiz();
        BeanUtils.copyProperties(quizDTO, quiz);
        quiz.setCourse(course);

        Quiz savedQuiz = quizRepository.save(quiz);
        QuizDTO savedQuizDto = new QuizDTO();
        BeanUtils.copyProperties(savedQuiz, savedQuizDto);
        savedQuizDto.setCourseId(course.getId());
        return savedQuizDto;
    }

    @Override
    public List<QuestionDTO> generateRandomQuestions(Long courseId, Long quizId) {
        Quiz quiz = quizRepository.findByCourseIdAndId(courseId, quizId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Quiz not found with id: " + quizId + " in course: " + courseId));

        List<Question> questionBank = questionRepository.findByCourse(quiz.getCourse());

        if (questionBank.size() < quiz.getNumberOfQuestions()) {
            throw new IllegalStateException(
                    "Not enough questions in the bank. Required: " + quiz.getNumberOfQuestions() +
                            ", Available: " + questionBank.size());
        }

        Collections.shuffle(questionBank);

        // Track start time for this student and quiz
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        quizStartTimes.put(userEmail + "_" + quizId, LocalDateTime.now());

        return questionBank.stream()
                .limit(quiz.getNumberOfQuestions())
                .map(question -> {
                    QuestionDTO dto = new QuestionDTO();
                    dto.setId(question.getId());
                    dto.setText(question.getText());           // Changed from questionText to text
                    dto.setType(question.getType());          // Changed from questionType to type
                    dto.setOptions(question.getOptions());
                    dto.setCourseId(question.getCourse().getId());
                    // Don't include correct answer for security
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public String submitQuizAttempt(Long courseId, Long quizId,
                                    List<QuestionAnswerDTO> answers, Long studentIdParam) {
        // Security: Retrieve studentId from SecurityContext to prevent impersonation
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + userEmail));
        Long studentId = student.getId();

        // Validate quiz exists in the course
        Quiz quiz = quizRepository.findByCourseIdAndId(courseId, quizId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Quiz not found with id: " + quizId + " in course: " + courseId));

        // Validation: Ensure student is enrolled in the course
        Course course = quiz.getCourse();
        if (course.getStudentEnrolledCourses().stream().noneMatch(s -> s.getId().equals(studentId))) {
            throw new IllegalArgumentException("Student is not enrolled in this course");
        }

        // Calculate score
        int score = 0;
        LocalDateTime submissionTime = LocalDateTime.now();

        // Time Tracking: Retrieve actual start time
        LocalDateTime startTime = quizStartTimes.getOrDefault(userEmail + "_" + quizId,
                submissionTime.minus(quiz.getDuration(), ChronoUnit.MINUTES));
        quizStartTimes.remove(userEmail + "_" + quizId); // Clean up

        // Process each answer
        for (QuestionAnswerDTO answer : answers) {
            Question question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Question not found with id: " + answer.getQuestionId()));

            // Validate question belongs to this quiz's course
            if (!question.getCourse().getId().equals(courseId)) {
                throw new IllegalArgumentException(
                        "Question " + answer.getQuestionId() + " does not belong to this course");
            }

            // Compare selected answer with correct answer
            if (answer.getSelectedAnswer() != null &&
                    answer.getSelectedAnswer().equals(question.getCorrectAnswer())) {
                score++;
            }
        }

        // Create and save attempt
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setStudent(student);
        attempt.setScore(score);
        attempt.setStartTime(startTime);
        attempt.setEndTime(submissionTime);
        attempt.setTimeSpentMinutes((int) Duration.between(startTime, submissionTime).toMinutes());

        // Calculate status and feedback
        String status = score >= Math.ceil(quiz.getNumberOfQuestions() / 2.0) ? "PASSED" : "FAILED";
        attempt.setStatus(status);

        String feedback = String.format("You have %s the quiz. Your score is: %d out of %d questions.",
                status.toLowerCase(), score, quiz.getNumberOfQuestions());
        attempt.setFeedback(feedback);

        quizAttemptRepository.save(attempt);
        return feedback;
    }

    @Override
    public List<QuizDTO> getQuizzesByCourse(Long courseId) {
        // Verify course exists
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Get quizzes and convert to DTOs
        return quizRepository.findByCourseId(courseId).stream()
                .map(quiz -> {
                    QuizDTO dto = new QuizDTO();
                    BeanUtils.copyProperties(quiz, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteQuiz(Long courseId, Long quizId) {
        Quiz quiz = quizRepository.findByCourseIdAndId(courseId, quizId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Quiz not found with id: " + quizId + " in course: " + courseId));

        quizRepository.delete(quiz);
    }
}