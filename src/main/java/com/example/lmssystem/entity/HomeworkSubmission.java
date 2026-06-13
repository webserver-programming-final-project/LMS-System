package com.example.lmssystem.entity;

public class HomeworkSubmission {
    private Long submissionId;
    private Long homeworkId;
    private String homeworkTitle;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String submittedAt;
    private Long fileId;
    private String filePath;
    private String uploadedAt;

    public Long getSubmissionId() { return submissionId; }
    public void setSubmissionId(Long submissionId) { this.submissionId = submissionId; }

    public Long getHomeworkId() { return homeworkId; }
    public void setHomeworkId(Long homeworkId) { this.homeworkId = homeworkId; }

    public String getHomeworkTitle() { return homeworkTitle; }
    public void setHomeworkTitle(String homeworkTitle) { this.homeworkTitle = homeworkTitle; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }

    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(String uploadedAt) { this.uploadedAt = uploadedAt; }
}
