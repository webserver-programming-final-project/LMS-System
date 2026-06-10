package com.example.lmssystem.controller;

import com.example.lmssystem.entity.Lecture;
import com.example.lmssystem.entity.TimeTable;
import com.example.lmssystem.service.LectureService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/lectures")
public class LectureServlet extends HttpServlet {

    private final LectureService lectureService = new LectureService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");

        if (idParam != null) {
            Long id = null;
            try {
                id = Long.parseLong(idParam);
                Lecture lecture = lectureService.getLectureById(id);

                if (lecture == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "존재하지 않는 강의입니다.");
                    return;
                }

                List<TimeTable> timeTables = lectureService.getTimeTableByClassId(id);
                req.setAttribute("lecture", lecture);
                req.setAttribute("timeTables", timeTables);
                req.getRequestDispatcher("/WEB-INF/views/lectures/lectureDetail.jsp")
                   .forward(req, resp);

            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 강의 ID입니다.");
            } catch (SQLException e) {
                req.setAttribute("error", "강의 조회 중 오류가 발생했습니다: " + e.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/lectures/lectureDetail.jsp")
                   .forward(req, resp);
            } finally {
                System.out.println("강의 상세 조회 완료 - id: " + idParam);
            }

        } else {
            try {
                List<Lecture> lectureList = lectureService.getAllLectures();
                req.setAttribute("lectureList", lectureList);
                req.getRequestDispatcher("/WEB-INF/views/lectures/lectureList.jsp")
                   .forward(req, resp);
            } catch (SQLException e) {
                req.setAttribute("error", "강의 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/lectures/lectureList.jsp")
                   .forward(req, resp);
            } finally {
                System.out.println("강의 목록 조회 완료");
            }
        }
    }
}
