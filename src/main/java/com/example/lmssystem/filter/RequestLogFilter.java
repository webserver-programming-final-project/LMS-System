package com.example.lmssystem.filter;

import com.example.lmssystem.entity.AccessLog;
import com.example.lmssystem.entity.User;
import com.example.lmssystem.repository.LogRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

public class RequestLogFilter implements Filter {
    private static final Set<String> LOG_PATHS = Set.of(
            "/login",
            "/register",
            "/logout",
            "/submitHomework.jsp",
            "/processSubmitHomework.jsp"
    );
    private final LogRepository logRepository = new LogRepository();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (LOG_PATHS.contains(path)) {
            HttpSession session = req.getSession(false);
            Long userId = null;
            if (session != null) {
                User loginUser = (User) session.getAttribute("user");
                if (loginUser != null) userId = loginUser.getId();
            }
            AccessLog log = new AccessLog();
            log.setUserId(userId);
            log.setMethod(req.getMethod());
            log.setPath(path);
            log.setIp(req.getRemoteAddr());
            try {
                logRepository.insert(log);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        filterChain.doFilter(req, resp);
    }
}
