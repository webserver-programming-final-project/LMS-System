package com.example.lmssystem.controller;

import com.example.lmssystem.entity.Homework;
import com.example.lmssystem.entity.User;
import com.example.lmssystem.service.HomeworkService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/homework")
public class HomeworkServlet extends HttpServlet {

    private final HomeworkService homeworkService = new HomeworkService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long classId = parseClassId(request);
        if (classId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "classId 파라미터가 필요합니다.");
            return;
        }

        User loginUser = getLoginUser(request);
        try {
            List<Homework> homeworkList = homeworkService.getHomeworksByClass(loginUser, classId);
            boolean canAddHomework = homeworkService.canAddHomework(loginUser, classId);

            request.setAttribute("homeworkList", homeworkList);
            request.setAttribute("classId", classId);
            request.setAttribute("canAddHomework", canAddHomework);
            request.getRequestDispatcher("/homework_list.jsp").forward(request, response);
        } catch (SecurityException e) {
            redirectHomeWithMessage(request, response, e.getMessage());
        } catch (SQLException e) {
            request.setAttribute("error", "과제 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            request.setAttribute("classId", classId);
            request.getRequestDispatcher("/homework_list.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Long classId = parseClassId(request);
        if (classId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "classId 파라미터가 필요합니다.");
            return;
        }

        Homework homework = new Homework();
        homework.setClassId(classId);
        homework.setTitle(request.getParameter("title"));
        homework.setDescription(request.getParameter("description"));
        homework.setStartFrom(request.getParameter("startFrom"));
        homework.setEndTo(request.getParameter("endTo"));

        try {
            homeworkService.addHomework(getLoginUser(request), homework);
            response.sendRedirect(request.getContextPath() + "/homework?classId=" + classId);
        } catch (SecurityException e) {
            redirectHomeWithMessage(request, response, e.getMessage());
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "과제 등록 중 오류가 발생했습니다: " + e.getMessage());
            doGet(request, response);
        }
    }

    private Long parseClassId(HttpServletRequest request) {
        String classIdParam = request.getParameter("classId");
        if (classIdParam == null || classIdParam.isBlank()) {
            classIdParam = request.getParameter("lectureId");
        }
        try {
            return classIdParam == null || classIdParam.isBlank() ? null : Long.parseLong(classIdParam);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private User getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    private void redirectHomeWithMessage(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        request.getSession().setAttribute("error", message);
        response.sendRedirect(request.getContextPath() + "/lectures");
    }

}
