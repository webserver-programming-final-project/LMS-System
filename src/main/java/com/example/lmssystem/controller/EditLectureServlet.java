package com.example.lmssystem.controller;

import com.example.lmssystem.entity.Lecture;
import com.example.lmssystem.entity.TimeTable;
import com.example.lmssystem.entity.User;
import com.example.lmssystem.service.LectureService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/lectures/edit")
public class EditLectureServlet extends HttpServlet {

    private final LectureService lectureService = new LectureService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User loginUser = getLoginUser(req);
        if (loginUser == null || !loginUser.isProfessor()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Long lectureId = parseLectureId(req);
        if (lectureId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 강의 ID입니다.");
            return;
        }

        try {
            Lecture lecture = lectureService.getLectureById(lectureId);
            if (lecture == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "존재하지 않는 강의입니다.");
                return;
            }
            if (!loginUser.getId().equals(lecture.getProfessorId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            req.setAttribute("lecture", lecture);
            req.setAttribute("timeTables", lectureService.getTimeTableByClassId(lectureId));
            req.setAttribute("timeSlots", lectureService.getAllTimeSlots());
            req.getRequestDispatcher("/WEB-INF/views/lectures/editLecture.jsp")
               .forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "강의 조회 중 오류가 발생했습니다: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/lectures/editLecture.jsp")
               .forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        User loginUser = getLoginUser(req);
        if (loginUser == null || !loginUser.isProfessor()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Long lectureId = parseLectureId(req);
        if (lectureId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 강의 ID입니다.");
            return;
        }

        Lecture lecture = new Lecture();
        lecture.setId(lectureId);
        lecture.setTitle(req.getParameter("title"));
        lecture.setDescription(req.getParameter("description"));
        lecture.setClassroom(req.getParameter("classroom"));
        lecture.setProfessorId(loginUser.getId());

        List<TimeTable> timeTables = parseTimeTables(req);

        try {
            lectureService.updateLecture(lecture, timeTables);
            resp.sendRedirect(req.getContextPath() + "/lectures");
        } catch (SecurityException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (SQLException e) {
            forwardEditWithError(req, resp, lectureId, "강의 수정 중 오류가 발생했습니다: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            forwardEditWithError(req, resp, lectureId, e.getMessage());
        }
    }

    private void forwardEditWithError(HttpServletRequest req, HttpServletResponse resp, Long lectureId, String error)
            throws ServletException, IOException {
        try {
            req.setAttribute("lecture", lectureService.getLectureById(lectureId));
            req.setAttribute("timeTables", lectureService.getTimeTableByClassId(lectureId));
            req.setAttribute("timeSlots", lectureService.getAllTimeSlots());
        } catch (SQLException ignored) {
            // Keep the original validation/update error visible.
        }
        req.setAttribute("error", error);
        req.getRequestDispatcher("/WEB-INF/views/lectures/editLecture.jsp")
           .forward(req, resp);
    }

    private User getLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    private Long parseLectureId(HttpServletRequest req) {
        String idParam = req.getParameter("id");
        try {
            return idParam == null || idParam.isBlank() ? null : Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private List<TimeTable> parseTimeTables(HttpServletRequest req) {
        String[] days = req.getParameterValues("days");
        String[] slots = req.getParameterValues("slots");
        List<TimeTable> timeTables = new ArrayList<>();
        if (days == null || slots == null || days.length != slots.length) {
            return timeTables;
        }

        for (int i = 0; i < days.length; i++) {
            if (!days[i].isEmpty() && !slots[i].isEmpty()) {
                TimeTable tt = new TimeTable();
                tt.setDays(days[i]);
                tt.setSlotId(Long.parseLong(slots[i]));
                timeTables.add(tt);
            }
        }
        return timeTables;
    }
}
