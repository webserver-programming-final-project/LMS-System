package com.example.lmssystem.filter;

import com.example.lmssystem.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

public class AuthFilter implements Filter {
    private static final Set<String> PROFESSOR_PATHS = Set.of();
    private static final Set<String> STUDENT_PATHS = Set.of();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("test");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        HttpSession session = req.getSession(false);
        User loginUser = session == null ? null : (User) session.getAttribute("user");
        if (loginUser == null) {
            req.setAttribute("error", "로그인이 필요합니다.");
            req.getSession().setAttribute("redirectPath",path);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }
        boolean isProfessor = loginUser.isProfessor();
        if (isProfessorPath(path) && !isProfessor) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        if (isStudentPath(path) && isProfessor) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        filterChain.doFilter(req, resp);
    }
    private boolean isProfessorPath(String path) {
        return PROFESSOR_PATHS.contains(path);
    }
    private boolean isStudentPath(String path) {
        return STUDENT_PATHS.contains(path);
    }
}
