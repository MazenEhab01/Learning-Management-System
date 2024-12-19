package org.software.lms.dto;

public class GradeSubmissionRequest {
    private Double grade;
    private String feedback;

    // Getters and Setters
    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}