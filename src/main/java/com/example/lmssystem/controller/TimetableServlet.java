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
import java.time.LocalTime;
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
            int[] hours = {9, 10, 11, 12, 13, 14, 15, 16, 17};
            Map<String, Map<Integer, List<Lecture>>> grid = new LinkedHashMap<>();
            for (String day : days) {
                Map<Integer, List<Lecture>> dayGrid = new HashMap<>();
                for (int hour : hours) {
                    dayGrid.put(hour, new ArrayList<>());
                }
                grid.put(day, dayGrid);
            }

            for (Lecture lec : myLectures) {
                List<TimeTable> tts = lectureService.getTimeTableByClassId(lec.getId());
                for (TimeTable tt : tts) {
                    if (grid.containsKey(tt.getDays())) {
                        addLectureToHourlyGrid(grid.get(tt.getDays()), tt, lec, hours);
                    }
                }
            }

            req.setAttribute("grid", grid);
            req.setAttribute("myLectures", myLectures);
            req.setAttribute("hours", hours);
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

    private void addLectureToHourlyGrid(Map<Integer, List<Lecture>> dayGrid, TimeTable timeTable,
                                        Lecture lecture, int[] hours) {
        LocalTime lectureStart = LocalTime.parse(timeTable.getStartTime());
        LocalTime lectureEnd = LocalTime.parse(timeTable.getEndTime());

        for (int hour : hours) {
            LocalTime hourStart = LocalTime.of(hour, 0);
            LocalTime hourEnd = hourStart.plusHours(1);
            if (lectureStart.isBefore(hourEnd) && lectureEnd.isAfter(hourStart)) {
                dayGrid.get(hour).add(lecture);
            }
        }
    }
}
