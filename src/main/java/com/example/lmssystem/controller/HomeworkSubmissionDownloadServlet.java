package com.example.lmssystem.controller;

import com.example.lmssystem.entity.User;
import com.example.lmssystem.repository.HomeworkSubmissionViewRepository;
import com.example.lmssystem.repository.HomeworkSubmissionViewRepository.HomeworkSubmissionFile;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

@WebServlet("/homework/submissions/download")
public class HomeworkSubmissionDownloadServlet extends HttpServlet {

    private final HomeworkSubmissionViewRepository repository = new HomeworkSubmissionViewRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User loginUser = getLoginUser(req);
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!loginUser.isProfessor()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "교수만 제출 파일을 다운로드할 수 있습니다.");
            return;
        }

        Long submissionId = parseSubmissionId(req);
        if (submissionId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "submissionId 파라미터가 필요합니다.");
            return;
        }

        try {
            HomeworkSubmissionFile submissionFile = repository.findSubmissionFile(submissionId);
            if (submissionFile == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "제출 파일을 찾을 수 없습니다.");
                return;
            }
            if (!loginUser.getId().equals(submissionFile.getProfessorId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "담당 강의의 제출 파일만 다운로드할 수 있습니다.");
                return;
            }

            Path filePath = Paths.get(submissionFile.getFilePath());
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "저장된 파일이 존재하지 않습니다.");
                return;
            }

            String fileName = filePath.getFileName().toString();
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replace("+", "%20");
            String contentType = Files.probeContentType(filePath);
            resp.setContentType(contentType == null ? "application/octet-stream" : contentType);
            resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
            resp.setContentLengthLong(Files.size(filePath));

            try (ServletOutputStream outputStream = resp.getOutputStream()) {
                Files.copy(filePath, outputStream);
            }
        } catch (SQLException e) {
            throw new ServletException("제출 파일 조회 중 오류가 발생했습니다.", e);
        }
    }

    private User getLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    private Long parseSubmissionId(HttpServletRequest req) {
        String submissionIdParam = req.getParameter("submissionId");
        try {
            return submissionIdParam == null || submissionIdParam.isBlank()
                    ? null
                    : Long.parseLong(submissionIdParam);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
