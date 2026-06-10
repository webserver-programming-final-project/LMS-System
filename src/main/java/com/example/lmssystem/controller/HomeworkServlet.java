package com.example.lmssystem.controller;

import com.example.lmssystem.entity.Homework;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/homework")
public class HomeworkServlet extends HttpServlet {
    // 다른 파일과의 경로 꼬임 방지를 위해 서블릿 내부에서 임시로 데이터를 바로 처리하도록 구조를 안정화합니다.
    private static final java.util.List<Homework> homeworkList = new java.util.ArrayList<>();
    private static Long sequence = 0L;

    // 1. 과제 현황 조회 파트 (화면에 목록을 보여줌)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 화면(JSP)으로 데이터를 넘겨주기 위해 세팅
        request.setAttribute("homeworkList", homeworkList);

        // 과제 현황 페이지(JSP)로 화면을 이동시킴
        request.getRequestDispatcher("/homework_list.jsp").forward(request, response);
    }

    // 2. 과제 등록 파트 (화면에서 보낸 데이터를 저장)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 한글 깨짐 방지 설정
        request.setCharacterEncoding("UTF-8");

        // 사용자가 입력한 값들을 끄집어냄
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String dueDate = request.getParameter("dueDate");

        // 바구니(Entity)에 데이터를 담고 리스트에 추가
        synchronized (homeworkList) {
            Homework homework = new Homework(++sequence, title, content, dueDate);
            homeworkList.add(homework);
        }

        // 등록이 끝나면 다시 과제 현황 목록 페이지로 새로고침(이동) 시킴
        response.sendRedirect(request.getContextPath() + "/homework");
    }
}