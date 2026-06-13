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
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login",
            "/register"
    );

    private static final Set<String> PROFESSOR_PATHS = Set.of(
            "/lectures/add",
            "/lectures/edit",
            "/homework/submissions",
            "/homework/submissions/download"
    );

    private static final Set<String> STUDENT_PATHS = Set.of(
            "/enroll",
            "/homework/submit",
            "/submitHomework.jsp"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (isPublicPath(path)) {
            filterChain.doFilter(req, resp);
            return;
        }

        HttpSession session = req.getSession(false);
        User loginUser = session == null ? null : (User) session.getAttribute("user");
        if (loginUser == null) {
            req.getSession().setAttribute("error", "로그인이 필요합니다.");
            req.getSession().setAttribute("redirectPath", getRedirectPath(req, path));
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        boolean isProfessor = loginUser.isProfessor();
        if (isProfessorPath(req, path) && !isProfessor) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (isStudentPath(path) && isProfessor) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(req, resp);
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/static/")
                || path.startsWith("/WEB-INF/lib/")
                || PUBLIC_PATHS.contains(path);
    }

    private boolean isProfessorPath(HttpServletRequest req, String path) {
        if (PROFESSOR_PATHS.contains(path)) {
            return true;
        }

        return "/homework".equals(path) && "POST".equalsIgnoreCase(req.getMethod());
    }

    private boolean isStudentPath(String path) {
        return STUDENT_PATHS.contains(path);
    }

    private String getRedirectPath(HttpServletRequest req, String path) {
        String queryString = req.getQueryString();
        return queryString == null || queryString.isBlank() ? path : path + "?" + queryString;
    }
}
