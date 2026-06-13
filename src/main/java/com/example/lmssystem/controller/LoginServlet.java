package com.example.lmssystem.controller;

import com.example.lmssystem.entity.User;
import com.example.lmssystem.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserService userService = new UserService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath()+"/home");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        try {
            User user = userService.login(email, password);
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            String redirectPath = (String) session.getAttribute("redirectPath");
            if (redirectPath == null) redirectPath = "/lectures";
            resp.sendRedirect(req.getContextPath() + redirectPath);
        } catch (IllegalArgumentException e) {
            HttpSession session = req.getSession();
            session.setAttribute("error", e.getMessage());
            session.setAttribute("email", email);
            doGet(req, resp);
        }
    }
}
