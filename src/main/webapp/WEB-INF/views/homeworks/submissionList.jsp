<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.lmssystem.entity.HomeworkSubmission" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    List<HomeworkSubmission> submissions =
            (List<HomeworkSubmission>) request.getAttribute("submissions");
    Long homeworkId = (Long) request.getAttribute("homeworkId");
    String homeworkTitle = (String) request.getAttribute("homeworkTitle");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>과제 제출 목록</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/adminlte.css">
    <style>
        body { background-color: #f8f9fa; }
        .page-container { max-width: 1000px; margin: 0 auto; padding: 32px 16px; }
        .table td { vertical-align: middle; }
    </style>
</head>
<body>
<%@ include file="../layout/header.jsp" %>

<div class="page-container">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h2 class="mb-1">과제 제출 목록</h2>
            <p class="text-muted mb-0"><%= homeworkTitle %></p>
        </div>
        <a href="${pageContext.request.contextPath}/lectures" class="btn btn-outline-secondary btn-sm">강의 목록</a>
    </div>

    <table class="table table-bordered table-hover bg-white">
        <thead class="table-dark">
        <tr>
            <th>번호</th>
            <th>학생</th>
            <th>이메일</th>
            <th>제출 일시</th>
            <th>파일</th>
            <th>파일 업로드 일시</th>
        </tr>
        </thead>
        <tbody>
        <%
            if (submissions == null || submissions.isEmpty()) {
        %>
        <tr>
            <td colspan="6" class="text-center text-muted py-4">아직 제출한 수강생이 없습니다.</td>
        </tr>
        <%
            } else {
                for (int i = 0; i < submissions.size(); i++) {
                    HomeworkSubmission submission = submissions.get(i);
        %>
        <tr>
            <td><%= i + 1 %></td>
            <td><%= submission.getStudentName() %></td>
            <td><%= submission.getStudentEmail() %></td>
            <td><%= submission.getSubmittedAt() %></td>
            <td>
                <% if (submission.getFileId() == null) { %>
                    -
                <% } else { %>
                    <a href="${pageContext.request.contextPath}/homework/submissions/download?submissionId=<%= submission.getSubmissionId() %>"
                       class="btn btn-sm btn-outline-primary">
                        다운로드
                    </a>
                <% } %>
            </td>
            <td><%= submission.getUploadedAt() == null ? "-" : submission.getUploadedAt() %></td>
        </tr>
        <%
                }
            }
        %>
        </tbody>
    </table>
</div>

<%@ include file="../layout/footer.jsp" %>
</body>
</html>
