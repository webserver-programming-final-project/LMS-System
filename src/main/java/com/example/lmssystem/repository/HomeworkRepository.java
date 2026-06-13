package com.example.lmssystem.repository;

import com.example.lmssystem.entity.Homework;
import com.example.lmssystem.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HomeworkRepository {

    public List<Homework> findByClassId(Long classId) throws SQLException {
        String sql = "SELECT h.id, h.class_id, h.title, h.description, h.created_at, h.start_from, h.end_to, " +
                     "c.title AS class_title, u.name AS professor_name " +
                     "FROM homeworks h " +
                     "JOIN classes c ON h.class_id = c.id " +
                     "JOIN lms_system.user u ON c.professor_id = u.id " +
                     "WHERE h.class_id = ? " +
                     "ORDER BY h.end_to ASC, h.id DESC";
        List<Homework> homeworks = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, classId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    homeworks.add(mapHomework(rs));
                }
            }
        }
        return homeworks;
    }

    public void save(Homework homework) throws SQLException {
        String sql = "INSERT INTO homeworks (class_id, title, description, start_from, end_to) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, homework.getClassId());
            pstmt.setString(2, homework.getTitle());
            pstmt.setString(3, homework.getDescription());
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.parse(homework.getStartFrom())));
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.parse(homework.getEndTo())));
            pstmt.executeUpdate();
        }
    }

    public boolean isProfessorOfClass(Long userId, Long classId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM classes WHERE id = ? AND professor_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, classId);
            pstmt.setLong(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public boolean isStudentOfClass(Long userId, Long classId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE user_id = ? AND class_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setLong(2, classId);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private Homework mapHomework(ResultSet rs) throws SQLException {
        Homework homework = new Homework();
        homework.setId(rs.getLong("id"));
        homework.setClassId(rs.getLong("class_id"));
        homework.setTitle(rs.getString("title"));
        homework.setDescription(rs.getString("description"));
        homework.setCreatedAt(String.valueOf(rs.getTimestamp("created_at")));
        homework.setStartFrom(String.valueOf(rs.getTimestamp("start_from")));
        homework.setEndTo(String.valueOf(rs.getTimestamp("end_to")));
        homework.setClassTitle(rs.getString("class_title"));
        homework.setProfessorName(rs.getString("professor_name"));
        return homework;
    }
}
