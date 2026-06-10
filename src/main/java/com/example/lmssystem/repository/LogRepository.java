package com.example.lmssystem.repository;

import com.example.lmssystem.entity.AccessLog;
import com.example.lmssystem.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class LogRepository {
    public void insert(AccessLog log) {
        final String sql = "insert into lms_system.access_logs (user_id, method, path, ip) values (?,?,?,?)";
        try (
                Connection conn = DBUtil.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql);) {
            if (log.getUserId() == null) {
                preparedStatement.setNull(1, Types.BIGINT);
            } else {
                preparedStatement.setLong(1, log.getUserId());
            }
            preparedStatement.setString(2, log.getMethod());
            preparedStatement.setString(3, log.getPath());
            preparedStatement.setString(4, log.getIp());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
