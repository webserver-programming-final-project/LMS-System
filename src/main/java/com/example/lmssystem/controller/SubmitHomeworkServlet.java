package com.example.lmssystem.controller;

import com.example.lmssystem.entity.User;
import com.example.lmssystem.repository.HomeworkSubmissionRepository;
import com.example.lmssystem.repository.HomeworkSubmissionRepository.HomeworkSubmissionContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@WebServlet("/homework/submit")
public class SubmitHomeworkServlet extends HttpServlet {

    private static final String UPLOAD_DIR = System.getProperty("user.home") + File.separator + "lms-uploads";
    private final HomeworkSubmissionRepository submissionRepository = new HomeworkSubmissionRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        User loginUser = getLoginUser(req);
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (loginUser.isProfessor()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "교수는 과제를 제출할 수 없습니다.");
            return;
        }

        SubmissionForm form = parseForm(req);
        if (form.homeworkId == null) {
            resp.sendRedirect(req.getContextPath() + "/exceptionNoHomework.jsp");
            return;
        }
        if (form.savedPath == null) {
            resp.sendRedirect(req.getContextPath() + "/submitHomework.jsp?homeworkId=" + form.homeworkId);
            return;
        }

        try {
            HomeworkSubmissionContext context =
                    submissionRepository.findSubmissionContext(form.homeworkId, loginUser.getId());
            if (context == null) {
                deleteSavedFile(form.savedPath);
                resp.sendRedirect(req.getContextPath() + "/exceptionNoHomework.jsp");
                return;
            }
            if (context.getEndTo() != null && new Date().after(context.getEndTo())) {
                deleteSavedFile(form.savedPath);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "마감일이 지났습니다.");
                return;
            }
            if (context.getStudentId() == null) {
                deleteSavedFile(form.savedPath);
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "이 과제 제출 권한이 없습니다.");
                return;
            }

            submissionRepository.saveSubmission(form.homeworkId, context.getStudentId(), form.savedPath);
            saveStudentCookies(resp, form.studentNo, form.studentName);

            req.getSession().setAttribute("success", "과제 제출이 완료되었습니다.");
            resp.sendRedirect(req.getContextPath() + "/homework?classId=" + context.getClassId());
        } catch (SQLException e) {
            deleteSavedFile(form.savedPath);
            throw new ServletException("과제 제출 중 오류가 발생했습니다.", e);
        }
    }

    private SubmissionForm parseForm(HttpServletRequest req) throws ServletException, IOException {
        ensureUploadDirectory();

        JakartaServletFileUpload<DiskFileItem, DiskFileItemFactory> upload =
                new JakartaServletFileUpload<>(DiskFileItemFactory.builder().get());
        SubmissionForm form = new SubmissionForm();

        try {
            List<DiskFileItem> items = upload.parseRequest(req);
            for (DiskFileItem item : items) {
                if (item.isFormField()) {
                    applyFormField(form, item);
                } else {
                    saveUploadedFile(form, item);
                }
            }
            return form;
        } catch (Exception e) {
            throw new ServletException("업로드 요청을 처리할 수 없습니다.", e);
        }
    }

    private void applyFormField(SubmissionForm form, DiskFileItem item) throws IOException {
        String name = item.getFieldName();
        String value = item.getString();
        if ("homeworkId".equals(name) && value != null && !value.isBlank()) {
            form.homeworkId = Long.valueOf(value);
        } else if ("studentNo".equals(name)) {
            form.studentNo = value;
        } else if ("studentName".equals(name)) {
            form.studentName = value;
        }
    }

    private void saveUploadedFile(SubmissionForm form, DiskFileItem item) throws Exception {
        String originalName = item.getName();
        if (originalName == null || originalName.isBlank()) {
            return;
        }

        originalName = originalName.substring(originalName.lastIndexOf("/") + 1);
        originalName = originalName.substring(originalName.lastIndexOf("\\") + 1);
        String savedName = System.currentTimeMillis() + "_" + originalName;
        File target = new File(UPLOAD_DIR, savedName);
        item.write(target.toPath());

        form.savedName = savedName;
        form.savedPath = target.getAbsolutePath();
    }

    private void ensureUploadDirectory() throws IOException {
        Files.createDirectories(new File(UPLOAD_DIR).toPath());
    }

    private void saveStudentCookies(HttpServletResponse resp, String studentNo, String studentName) {
        Cookie cNo = new Cookie("student_no", encodeCookieValue(studentNo));
        cNo.setMaxAge(60 * 60 * 24 * 30);
        resp.addCookie(cNo);

        Cookie cName = new Cookie("student_name", encodeCookieValue(studentName));
        cName.setMaxAge(60 * 60 * 24 * 30);
        resp.addCookie(cName);
    }

    private String encodeCookieValue(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private void deleteSavedFile(String savedPath) {
        if (savedPath == null) {
            return;
        }
        try {
            Files.deleteIfExists(new File(savedPath).toPath());
        } catch (IOException ignored) {
            // The failed request is still handled even if cleanup cannot remove the file.
        }
    }

    private User getLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    private static class SubmissionForm {
        private Long homeworkId;
        private String studentNo = "";
        private String studentName = "";
        private String savedPath;
        private String savedName;
    }
}
