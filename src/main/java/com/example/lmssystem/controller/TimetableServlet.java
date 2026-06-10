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
import java.util.*;

@WebServlet("/timetable")
public class TimetableServlet extends HttpServlet {

    private final LectureService lectureService = new LectureService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User loginUser = (User) session.getAttribute("user");

        try {
            List<Lecture> myLectures;
            if (loginUser.isProfessor()) {
                myLectures = lectureService.getLecturesByProfessor(loginUser.getId());
            } else {
                myLectures = lectureService.getLecturesByStudent(loginUser.getId());
            }

            String[] days = {"월", "화", "수", "목", "금"};
            Map<String, Map<Integer, Lecture>> grid = new LinkedHashMap<>();
            for (String day : days) {
                grid.put(day, new HashMap<>());
            }

            for (Lecture lec : myLectures) {
                List<TimeTable> tts = lectureService.getTimeTableByClassId(lec.getId());
                for (TimeTable tt : tts) {
                    if (grid.containsKey(tt.getDays())) {
                        grid.get(tt.getDays()).put(tt.getSlotNo(), lec);
                    }
                }
            }

            req.setAttribute("grid", grid);
            req.setAttribute("myLectures", myLectures);
            req.getRequestDispatcher("/WEB-INF/views/lectures/timetable.jsp")
               .forward(req, resp);

        } catch (SQLException e) {
            req.setAttribute("error", "시간표 조회 중 오류가 발생했습니다: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/lectures/timetable.jsp")
               .forward(req, resp);
        } finally {
            System.out.println("시간표 조회 완료 - user: " + loginUser.getId());
        }
    }
}
