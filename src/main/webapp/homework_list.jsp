<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.lmssystem.entity.Homework" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    Boolean canAddHomeworkAttr = (Boolean) request.getAttribute("canAddHomework");
    boolean canAddHomework = canAddHomeworkAttr != null && canAddHomeworkAttr;
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>과제 목록</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/adminlte.css">
    <style>
        body { background-color: #f8f9fa; }
        .page-container { max-width: 1000px; margin: 0 auto; padding: 32px 16px; }
        .table td { vertical-align: middle; }
        .description-cell { max-width: 360px; white-space: pre-wrap; }
    </style>
</head>
<body>

<%@ include file="WEB-INF/views/layout/header.jsp" %>

<div class="page-container">
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <c:if test="${canAddHomework}">
        <div class="card mb-4">
            <div class="card-header">
                <h2 class="card-title mb-0">새 과제 등록</h2>
            </div>
            <form action="${pageContext.request.contextPath}/homework" method="post">
                <input type="hidden" name="classId" value="${classId}">
                <div class="card-body">
                    <div class="mb-3">
                        <label class="form-label fw-bold">과제 제목</label>
                        <input type="text" name="title" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">과제 설명</label>
                        <textarea name="description" class="form-control" rows="4" required></textarea>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">시작 일시</label>
                            <input type="datetime-local" name="startFrom" class="form-control" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">마감 일시</label>
                            <input type="datetime-local" name="endTo" class="form-control" required>
                        </div>
                    </div>
                </div>
                <div class="card-footer d-flex justify-content-end">
                    <button type="submit" class="btn btn-primary">과제 등록</button>
                </div>
            </form>
        </div>
    </c:if>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="mb-0">과제 목록</h2>
        <a href="${pageContext.request.contextPath}/lectures?id=${classId}" class="btn btn-outline-secondary btn-sm">강의 상세</a>
    </div>

    <table class="table table-bordered table-hover bg-white">
        <thead class="table-dark">
        <tr>
            <th>ID</th>
            <th>강의</th>
            <th>과제 제목</th>
            <th>설명</th>
            <th>시작 일시</th>
            <th>마감 일시</th>
            <th><%= canAddHomework ? "제출 목록" : "제출" %></th>
        </tr>
        </thead>
        <tbody>
        <%
            List<Homework> list = (List<Homework>) request.getAttribute("homeworkList");
            if (list != null && !list.isEmpty()) {
                for (Homework hw : list) {
        %>
        <tr>
            <td><%= hw.getId() %></td>
            <td>
                <strong><%= hw.getClassTitle() %></strong><br>
                <small class="text-muted"><%= hw.getProfessorName() %></small>
            </td>
            <td><strong><%= hw.getTitle() %></strong></td>
            <td class="description-cell"><%= hw.getDescription() %></td>
            <td><%= hw.getStartFrom() %></td>
            <td><span class="text-danger fw-bold"><%= hw.getEndTo() %></span></td>
            <td>
                <% if (canAddHomework) { %>
                    <a href="${pageContext.request.contextPath}/homework/submissions?homeworkId=<%= hw.getId() %>"
                       class="btn btn-sm btn-outline-primary">제출 목록</a>
                <% } else { %>
                    <a href="${pageContext.request.contextPath}/submitHomework.jsp?homeworkId=<%= hw.getId() %>"
                       class="btn btn-sm btn-success">제출</a>
                <% } %>
            </td>
        </tr>
        <%
            }
        } else {
        %>
        <tr>
            <td colspan="7" class="text-center text-muted py-4">등록된 과제가 없습니다.</td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>

</body>
</html>
