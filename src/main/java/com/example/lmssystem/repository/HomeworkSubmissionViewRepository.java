package com.example.lmssystem.repository;

import com.example.lmssystem.entity.HomeworkSubmission;
import com.example.lmssystem.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HomeworkSubmissionViewRepository {

    public boolean isProfessorOfHomework(Long professorId, Long homeworkId) throws SQLException {
        String sql = "SELECT COUNT(*) " +
                     "FROM homeworks h " +
                     "JOIN classes c ON h.class_id = c.id " +
                     "WHERE h.id = ? AND c.professor_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, homeworkId);
            pstmt.setLong(2, professorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public String findHomeworkTitle(Long homeworkId) throws SQLException {
        String sql = "SELECT title FROM homeworks WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, homeworkId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString("title") : null;
            }
        }
    }

    public List<HomeworkSubmission> findSubmissionsByHomeworkId(Long homeworkId) throws SQLException {
        String sql = "SELECT sub.id AS submission_id, sub.homework_id, h.title AS homework_title, " +
                     "s.id AS student_id, u.name AS student_name, u.email AS student_email, " +
                     "sub.submitted_at, f.id AS file_id, f.path AS file_path, f.uploaded_at " +
                     "FROM submissions sub " +
                     "JOIN homeworks h ON sub.homework_id = h.id " +
                     "JOIN students s ON sub.student_id = s.id " +
                     "JOIN lms_system.user u ON s.user_id = u.id " +
                     "LEFT JOIN submission_attachments sa ON sa.submission_id = sub.id " +
                     "LEFT JOIN files f ON sa.file_id = f.id " +
                     "WHERE sub.homework_id = ? " +
                     "ORDER BY sub.submitted_at DESC, sub.id DESC";
        List<HomeworkSubmission> submissions = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, homeworkId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HomeworkSubmission submission = new HomeworkSubmission();
                    submission.setSubmissionId(rs.getLong("submission_id"));
                    submission.setHomeworkId(rs.getLong("homework_id"));
                    submission.setHomeworkTitle(rs.getString("homework_title"));
                    submission.setStudentId(rs.getLong("student_id"));
                    submission.setStudentName(rs.getString("student_name"));
                    submission.setStudentEmail(rs.getString("student_email"));
                    submission.setSubmittedAt(String.valueOf(rs.getTimestamp("submitted_at")));
                    long fileId = rs.getLong("file_id");
                    submission.setFileId(rs.wasNull() ? null : fileId);
                    submission.setFilePath(rs.getString("file_path"));
                    submission.setUploadedAt(String.valueOf(rs.getTimestamp("uploaded_at")));
                    submissions.add(submission);
                }
            }
        }
        return submissions;
    }

    public HomeworkSubmissionFile findSubmissionFile(Long submissionId) throws SQLException {
        String sql = "SELECT sub.id AS submission_id, sub.homework_id, c.professor_id, f.id AS file_id, f.path AS file_path " +
                     "FROM submissions sub " +
                     "JOIN homeworks h ON sub.homework_id = h.id " +
                     "JOIN classes c ON h.class_id = c.id " +
                     "JOIN submission_attachments sa ON sa.submission_id = sub.id " +
                     "JOIN files f ON sa.file_id = f.id " +
                     "WHERE sub.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, submissionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    HomeworkSubmissionFile file = new HomeworkSubmissionFile();
                    file.setSubmissionId(rs.getLong("submission_id"));
                    file.setHomeworkId(rs.getLong("homework_id"));
                    file.setProfessorId(rs.getLong("professor_id"));
                    file.setFileId(rs.getLong("file_id"));
                    file.setFilePath(rs.getString("file_path"));
                    return file;
                }
            }
        }
        return null;
    }

    public static class HomeworkSubmissionFile {
        private Long submissionId;
        private Long homeworkId;
        private Long professorId;
        private Long fileId;
        private String filePath;

        public Long getSubmissionId() { return submissionId; }
        public void setSubmissionId(Long submissionId) { this.submissionId = submissionId; }

        public Long getHomeworkId() { return homeworkId; }
        public void setHomeworkId(Long homeworkId) { this.homeworkId = homeworkId; }

        public Long getProfessorId() { return professorId; }
        public void setProfessorId(Long professorId) { this.professorId = professorId; }

        public Long getFileId() { return fileId; }
        public void setFileId(Long fileId) { this.fileId = fileId; }

        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
    }
}
