package com.example.lmssystem.controller;

import com.example.lmssystem.entity.Homework;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/homework")
public class HomeworkServlet extends HttpServlet {

    // 서블릿 내부에서 직접 DB(MySQL)에 연결합니다.
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8", "root", "1234");
    }

    // 1. 과제 현황 조회 파트 (GET - DB에서 목록 가져오기)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Homework> homeworkList = new ArrayList<>();
        String sql = "SELECT * FROM homeworks";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Homework homework = new Homework(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("due_date")
                );
                homeworkList.add(homework);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.setAttribute("homeworkList", homeworkList);
        request.getRequestDispatcher("/homework_list.jsp").forward(request, response);
    }

    // 2. 과제 등록 파트 (POST - DB에 새 과제 저장하기)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String dueDate = request.getParameter("dueDate");

        String sql = "INSERT INTO homeworks (title, content, due_date) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, dueDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/homework");
    }
}