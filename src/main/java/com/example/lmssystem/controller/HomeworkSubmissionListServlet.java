package com.example.lmssystem.controller;

import com.example.lmssystem.entity.HomeworkSubmission;
import com.example.lmssystem.entity.User;
import com.example.lmssystem.repository.HomeworkSubmissionViewRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/homework/submissions")
public class HomeworkSubmissionListServlet extends HttpServlet {

    private final HomeworkSubmissionViewRepository repository = new HomeworkSubmissionViewRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User loginUser = getLoginUser(req);
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!loginUser.isProfessor()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "교수만 과제 제출 목록을 조회할 수 있습니다.");
            return;
        }

        Long homeworkId = parseHomeworkId(req);
        if (homeworkId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "homeworkId 파라미터가 필요합니다.");
            return;
        }

        try {
            String homeworkTitle = repository.findHomeworkTitle(homeworkId);
            if (homeworkTitle == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "존재하지 않는 과제입니다.");
                return;
            }
            if (!repository.isProfessorOfHomework(loginUser.getId(), homeworkId)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "담당 강의의 과제만 조회할 수 있습니다.");
                return;
            }

            List<HomeworkSubmission> submissions = repository.findSubmissionsByHomeworkId(homeworkId);
            req.setAttribute("homeworkId", homeworkId);
            req.setAttribute("homeworkTitle", homeworkTitle);
            req.setAttribute("submissions", submissions);
            req.getRequestDispatcher("/WEB-INF/views/homeworks/submissionList.jsp")
               .forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("과제 제출 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    private User getLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    private Long parseHomeworkId(HttpServletRequest req) {
        String homeworkIdParam = req.getParameter("homeworkId");
        try {
            return homeworkIdParam == null || homeworkIdParam.isBlank()
                    ? null
                    : Long.parseLong(homeworkIdParam);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
