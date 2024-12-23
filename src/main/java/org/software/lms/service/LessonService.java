package org.software.lms.service;

import jakarta.transaction.Transactional;
import org.software.lms.exception.ResourceNotFoundException;
import org.software.lms.model.Lesson;
import org.software.lms.model.LessonResource;
import org.software.lms.model.ResourceType;
import org.software.lms.repository.LessonRepository;
import org.software.lms.repository.LessonResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@Transactional
public class LessonService {
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonResourceRepository lessonResourceRepository;

    @Value("${file.upload.directory}")
    private String uploadDirectory;

    public Lesson createLesson(Lesson lesson) {
        if (lesson.getCourse() == null) {
            throw new IllegalArgumentException("Lesson must be associated with a course");
        }
        lesson.setCreatedAt(new Date());
        lesson.setUpdatedAt(new Date());
        return lessonRepository.save(lesson);
    }

    public Lesson updateLesson(Long lessonId, Lesson lessonDetails) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        lesson.setTitle(lessonDetails.getTitle());
        lesson.setDescription(lessonDetails.getDescription());
        lesson.setUpdatedAt(new Date());

        return lessonRepository.save(lesson);
    }

    public String generateOTP(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        // Generate a 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        lesson.setCurrentOTP(otp);
        lesson.setOtpGeneratedAt(new Date());
        lessonRepository.save(lesson);

        return otp;
    }

    public boolean validateOTP(Long lessonId, String otp) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        if (lesson.getCurrentOTP() == null || lesson.getOtpGeneratedAt() == null) {
            return false;
        }

        // Check if OTP is expired (30 minutes validity)
        boolean isExpired = new Date().getTime() - lesson.getOtpGeneratedAt().getTime() > 30 * 60 * 1000;
        return !isExpired && lesson.getCurrentOTP().equals(otp);
    }

    public LessonResource uploadMediaFile(Long lessonId, MultipartFile file) throws IOException {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(fileName);

        // Save file to disk
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Determine resource type based on file extension
        ResourceType resourceType = determineResourceType(originalFilename);

        // Create and save media file entity
        LessonResource lessonResource = new LessonResource();
        lessonResource.setFileName(fileName);
        lessonResource.setType(resourceType);
        lessonResource.setFileUrl(filePath.toString());
        lessonResource.setFileSize(file.getSize());
        lessonResource.setLesson(lesson);
        lessonResource.setUploadedAt(new Date());

        return lessonResourceRepository.save(lessonResource);
    }

    private ResourceType determineResourceType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "pdf" -> ResourceType.PDF;
            case "mp4", "avi", "mov" -> ResourceType.VIDEO;
            case "mp3", "wav" -> ResourceType.AUDIO;
            case "jpg", "jpeg", "png", "gif" -> ResourceType.IMAGE;
            case "doc", "docx", "txt" -> ResourceType.DOCUMENT;
            default -> ResourceType.OTHER;
        };
    }

    public List<LessonResource> getLessonResources(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
        return lesson.getLessonResources();
    }

    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
        lessonRepository.delete(lesson);
    }
}