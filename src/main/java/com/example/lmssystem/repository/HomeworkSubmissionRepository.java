package com.example.lmssystem.repository;

import com.example.lmssystem.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class HomeworkSubmissionRepository {

    public HomeworkSubmissionContext findSubmissionContext(Long homeworkId, Long userId) throws SQLException {
        String sql = "SELECT h.class_id, h.end_to, s.id AS student_id " +
                     "FROM homeworks h " +
                     "LEFT JOIN students s ON s.class_id = h.class_id AND s.user_id = ? " +
                     "WHERE h.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setLong(2, homeworkId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    HomeworkSubmissionContext context = new HomeworkSubmissionContext();
                    context.setClassId(rs.getLong("class_id"));
                    context.setEndTo(rs.getTimestamp("end_to"));
                    long studentId = rs.getLong("student_id");
                    context.setStudentId(rs.wasNull() ? null : studentId);
                    return context;
                }
            }
        }
        return null;
    }

    public Long saveSubmission(Long homeworkId, Long studentId, String savedPath) throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            long fileId = insertFile(conn, savedPath);
            Long submissionId = findSubmissionId(conn, homeworkId, studentId);
            if (submissionId == null) {
                submissionId = insertSubmission(conn, homeworkId, studentId);
            } else {
                updateSubmittedAt(conn, submissionId);
            }

            replaceAttachment(conn, submissionId, fileId);
            conn.commit();
            return submissionId;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private long insertFile(Connection conn, String savedPath) throws SQLException {
        String sql = "INSERT INTO files (path) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, savedPath);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    private Long findSubmissionId(Connection conn, Long homeworkId, Long studentId) throws SQLException {
        String sql = "SELECT id FROM submissions WHERE homework_id = ? AND student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, homeworkId);
            pstmt.setLong(2, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getLong("id") : null;
            }
        }
    }

    private long insertSubmission(Connection conn, Long homeworkId, Long studentId) throws SQLException {
        String sql = "INSERT INTO submissions (homework_id, student_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, homeworkId);
            pstmt.setLong(2, studentId);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    private void updateSubmittedAt(Connection conn, Long submissionId) throws SQLException {
        String sql = "UPDATE submissions SET submitted_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, submissionId);
            pstmt.executeUpdate();
        }
    }

    private void replaceAttachment(Connection conn, Long submissionId, Long fileId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM submission_attachments WHERE submission_id = ?")) {
            pstmt.setLong(1, submissionId);
            pstmt.executeUpdate();
        }

        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO submission_attachments (submission_id, file_id) VALUES (?, ?)")) {
            pstmt.setLong(1, submissionId);
            pstmt.setLong(2, fileId);
            pstmt.executeUpdate();
        }
    }

    public static class HomeworkSubmissionContext {
        private Long classId;
        private Long studentId;
        private Timestamp endTo;

        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }

        public Timestamp getEndTo() { return endTo; }
        public void setEndTo(Timestamp endTo) { this.endTo = endTo; }
    }
}
