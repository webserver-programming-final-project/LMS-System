package com.example.lmssystem.controller;

import com.example.lmssystem.entity.User;
import com.example.lmssystem.repository.EnrollRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/enroll")
public class EnrollServlet extends HttpServlet {

    private final EnrollRepository enrollRepository = new EnrollRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        User loginUser = (User) session.getAttribute("user");

        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String classIdParam = req.getParameter("classId");

        try {
            Long classId = Long.parseLong(classIdParam);
            boolean alreadyEnrolled = enrollRepository.isEnrolled(loginUser.getId(), classId);
            if (alreadyEnrolled) {
                req.getSession().setAttribute("enrollMsg", "이미 수강 신청한 강의입니다.");
                resp.sendRedirect(req.getContextPath() + "/lectures");
                return;
            }

            enrollRepository.enroll(loginUser.getId(), classId);
            req.getSession().setAttribute("enrollMsg", "수강 신청이 완료되었습니다!");
            resp.sendRedirect(req.getContextPath() + "/lectures");

        } catch (SQLException e) {
            req.getSession().setAttribute("enrollMsg", "수강 신청 중 오류가 발생했습니다: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/lectures");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 강의 ID입니다.");
        }
    }
}
