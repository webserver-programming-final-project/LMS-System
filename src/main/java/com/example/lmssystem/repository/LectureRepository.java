package com.example.lmssystem.repository;

import com.example.lmssystem.entity.Lecture;
import com.example.lmssystem.entity.TimeTable;
import com.example.lmssystem.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LectureRepository {

    public void save(Lecture lecture, List<TimeTable> timeTables) throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            String classSQL = "INSERT INTO classes (title, description, classroom, professor_id) VALUES (?, ?, ?, ?)";
            long newClassId;
            try (PreparedStatement pstmt = conn.prepareStatement(classSQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, lecture.getTitle());
                pstmt.setString(2, lecture.getDescription());
                pstmt.setString(3, lecture.getClassroom());
                pstmt.setLong(4, lecture.getProfessorId());
                pstmt.executeUpdate();
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    keys.next();
                    newClassId = keys.getLong(1);
                }
            }

            String timeSQL = "INSERT INTO time_table (class_id, slot_id, days) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(timeSQL)) {
                for (TimeTable tt : timeTables) {
                    pstmt.setLong(1, newClassId);
                    pstmt.setLong(2, tt.getSlotId());
                    pstmt.setString(3, tt.getDays());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();

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

    public boolean hasTimeConflict(String day, Long slotId, Long excludeClassId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM time_table WHERE days = ? AND slot_id = ?" +
                     (excludeClassId != null ? " AND class_id != ?" : "");
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, day);
            pstmt.setLong(2, slotId);
            if (excludeClassId != null) pstmt.setLong(3, excludeClassId);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public List<Lecture> findAll() throws SQLException {
        String sql = "SELECT c.id, c.title, c.description, c.classroom, c.professor_id, " +
                     "u.name AS professor_name, COUNT(h.id) AS homework_count " +
                     "FROM classes c " +
                     "JOIN lms_system.user u ON c.professor_id = u.id " +
                     "LEFT JOIN homeworks h ON c.id = h.class_id " +
                     "GROUP BY c.id, c.title, c.description, c.classroom, c.professor_id, u.name " +
                     "ORDER BY c.id DESC";
        List<Lecture> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Lecture lecture = new Lecture();
                lecture.setId(rs.getLong("id"));
                lecture.setTitle(rs.getString("title"));
                lecture.setDescription(rs.getString("description"));
                lecture.setClassroom(rs.getString("classroom"));
                lecture.setProfessorId(rs.getLong("professor_id"));
                lecture.setProfessorName(rs.getString("professor_name"));
                lecture.setHomeworkCount(rs.getInt("homework_count"));
                list.add(lecture);
            }
        }
        return list;
    }

    public Lecture findById(Long id) throws SQLException {
        String sql = "SELECT c.id, c.title, c.description, c.classroom, c.professor_id, " +
                     "u.name AS professor_name, COUNT(h.id) AS homework_count " +
                     "FROM classes c " +
                     "JOIN lms_system.user u ON c.professor_id = u.id " +
                     "LEFT JOIN homeworks h ON c.id = h.class_id " +
                     "WHERE c.id = ? " +
                     "GROUP BY c.id, c.title, c.description, c.classroom, c.professor_id, u.name";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Lecture lecture = new Lecture();
                    lecture.setId(rs.getLong("id"));
                    lecture.setTitle(rs.getString("title"));
                    lecture.setDescription(rs.getString("description"));
                    lecture.setClassroom(rs.getString("classroom"));
                    lecture.setProfessorId(rs.getLong("professor_id"));
                    lecture.setProfessorName(rs.getString("professor_name"));
                    lecture.setHomeworkCount(rs.getInt("homework_count"));
                    return lecture;
                }
            }
        }
        return null;
    }

    public List<TimeTable> findTimeTableByClassId(Long classId) throws SQLException {
        String sql = "SELECT tt.id, tt.class_id, tt.slot_id, tt.days, " +
                     "ts.slot_no, ts.start_time, ts.end_time " +
                     "FROM time_table tt " +
                     "JOIN time_slots ts ON tt.slot_id = ts.id " +
                     "WHERE tt.class_id = ? ORDER BY tt.days, ts.slot_no";
        List<TimeTable> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, classId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TimeTable tt = new TimeTable();
                    tt.setId(rs.getLong("id"));
                    tt.setClassId(rs.getLong("class_id"));
                    tt.setSlotId(rs.getLong("slot_id"));
                    tt.setDays(rs.getString("days"));
                    tt.setSlotNo(rs.getInt("slot_no"));
                    tt.setStartTime(rs.getString("start_time"));
                    tt.setEndTime(rs.getString("end_time"));
                    list.add(tt);
                }
            }
        }
        return list;
    }

    public List<Lecture> findByProfessorId(Long professorId) throws SQLException {
        String sql = "SELECT c.id, c.title, c.classroom, c.professor_id " +
                     "FROM classes c WHERE c.professor_id = ?";
        List<Lecture> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, professorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Lecture lecture = new Lecture();
                    lecture.setId(rs.getLong("id"));
                    lecture.setTitle(rs.getString("title"));
                    lecture.setClassroom(rs.getString("classroom"));
                    lecture.setProfessorId(rs.getLong("professor_id"));
                    list.add(lecture);
                }
            }
        }
        return list;
    }

    public List<Lecture> findByStudentId(Long userId) throws SQLException {
        String sql = "SELECT c.id, c.title, c.classroom, c.professor_id " +
                     "FROM classes c " +
                     "JOIN students s ON c.id = s.class_id " +
                     "WHERE s.user_id = ?";
        List<Lecture> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Lecture lecture = new Lecture();
                    lecture.setId(rs.getLong("id"));
                    lecture.setTitle(rs.getString("title"));
                    lecture.setClassroom(rs.getString("classroom"));
                    lecture.setProfessorId(rs.getLong("professor_id"));
                    list.add(lecture);
                }
            }
        }
        return list;
    }
}
