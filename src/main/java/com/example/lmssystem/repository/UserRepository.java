package com.example.lmssystem.repository;

import com.example.lmssystem.entity.User;
import com.example.lmssystem.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    public User findUserById(Long id) {
        final String sql = "select * from lms_system.user where id = ?";
        try (
                Connection conn =  DBUtil.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql);){
            preparedStatement.setLong(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setEmail(resultSet.getString("email"));
                    user.setPassword(resultSet.getString("password"));
                    user.setId(resultSet.getLong("id"));
                    user.setName(resultSet.getString("name"));
                    user.setCreatedAt(resultSet.getTimestamp("created_at"));
                    return user;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public User findUserByEmail(String email) {
        final String sql = "select * from lms_system.user where email = ?";
        try (
                Connection conn =  DBUtil.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql);){
            preparedStatement.setString(1, email);

            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setEmail(resultSet.getString("email"));
                    user.setPassword(resultSet.getString("password"));
                    user.setId(resultSet.getLong("id"));
                    user.setName(resultSet.getString("name"));
                    user.setCreatedAt(resultSet.getTimestamp("created_at"));
                    return user;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public User addUser(User user) {
        final String sql = "insert into lms_system.user (email,password,name,is_professor) values (?,?,?,?)";
        try(
                Connection conn = DBUtil.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ) {
            preparedStatement.setString(1,user.getEmail());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setBoolean(4,user.isProfessor());

            int affected = preparedStatement.executeUpdate();
            if (affected == 0) return null;
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return findUserById(resultSet.getLong(1));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
