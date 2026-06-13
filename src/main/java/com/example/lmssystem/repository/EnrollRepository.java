package com.example.lmssystem.repository;

import com.example.lmssystem.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnrollRepository {

    public void enroll(Long userId, Long classId) throws SQLException {
        String sql = "INSERT INTO students (user_id, class_id) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setLong(2, classId);
            pstmt.executeUpdate();
        }
    }
    public boolean isEnrolled(Long userId, Long classId) throws SQLException {
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

    public String findScheduleConflict(Long userId, Long classId) throws SQLException {
        String sql = "SELECT ec.title AS enrolled_title, target_tt.days, " +
                     "ets.slot_no AS enrolled_slot_no, ets.start_time AS enrolled_start_time, ets.end_time AS enrolled_end_time, " +
                     "tts.slot_no AS target_slot_no, tts.start_time AS target_start_time, tts.end_time AS target_end_time " +
                     "FROM time_table target_tt " +
                     "JOIN time_slots tts ON target_tt.slot_id = tts.id " +
                     "JOIN students s ON s.user_id = ? " +
                     "JOIN time_table enrolled_tt ON enrolled_tt.class_id = s.class_id " +
                     "JOIN classes ec ON ec.id = s.class_id " +
                     "JOIN time_slots ets ON enrolled_tt.slot_id = ets.id " +
                     "WHERE target_tt.class_id = ? " +
                     "AND target_tt.days = enrolled_tt.days " +
                     "AND tts.start_time < ets.end_time " +
                     "AND tts.end_time > ets.start_time " +
                     "LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setLong(2, classId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("days") + "요일 "
                            + rs.getInt("target_slot_no") + "교시가 이미 수강 중인 강의 '"
                            + rs.getString("enrolled_title") + "' "
                            + rs.getInt("enrolled_slot_no") + "교시와 겹칩니다.";
                }
            }
        }
        return null;
    }
}
