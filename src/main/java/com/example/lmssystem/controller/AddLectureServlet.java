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

@WebServlet("/lectures/add")
public class AddLectureServlet extends HttpServlet {

    private final LectureService lectureService = new LectureService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/lectures/addLecture.jsp")
           .forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String title       = req.getParameter("title");
        String description = req.getParameter("description");
        String classroom   = req.getParameter("classroom");

        String[] days  = req.getParameterValues("days");   
        String[] slots = req.getParameterValues("slots"); 

        HttpSession session = req.getSession(false);
        User loginUser = (User) session.getAttribute("user");

        
        Lecture lecture = new Lecture();
        lecture.setTitle(title);
        lecture.setDescription(description);
        lecture.setClassroom(classroom);
        lecture.setProfessorId(loginUser.getId());

        
        List<TimeTable> timeTables = new ArrayList<>();
        if (days != null && slots != null && days.length == slots.length) {
            for (int i = 0; i < days.length; i++) {
                if (!days[i].isEmpty() && !slots[i].isEmpty()) {
                    TimeTable tt = new TimeTable();
                    tt.setDays(days[i]);
                    tt.setSlotId(Long.parseLong(slots[i]));
                    timeTables.add(tt);
                }
            }
        }

        try {
            lectureService.addLecture(lecture, timeTables);
            resp.sendRedirect(req.getContextPath() + "/lectures");
        } catch (SQLException e) {
            req.setAttribute("error", "강의 등록 중 오류가 발생했습니다: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/lectures/addLecture.jsp")
               .forward(req, resp);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/lectures/addLecture.jsp")
               .forward(req, resp);
        }
    }
}
