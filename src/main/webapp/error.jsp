<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%
    Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
    String errorMessage = (String) request.getAttribute("jakarta.servlet.error.message");
    String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");
    int code = statusCode == null ? 500 : statusCode;
    String defaultMsg;
    if (code == 404) defaultMsg = "요청하신 페이지를 찾을 수 없습니다.";
    else if (code == 403) defaultMsg = "접근 권한이 없습니다.";
    else if (code == 401) defaultMsg = "로그인이 필요합니다.";
    else defaultMsg = "서버 오류가 발생했습니다.";
    String display = (errorMessage != null && !errorMessage.isEmpty()) ? errorMessage : defaultMsg;
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/adminlte.css">
    <meta charset="UTF-8">
    <title>오류 <%= code %></title>
</head>
<body>
<div class="wrapper container min-vh-100 d-flex justify-content-center align-items-center">
    <div class="card" style="min-width: 40vw">
        <div class="card-header">
            <h2 class="alert alert-danger">오류 <%= code %></h2>
        </div>
        <div class="card-body">
            <p><%= display %></p>
            <% if (requestUri != null) { %>
            <p class="text-muted">요청 경로: <%= requestUri %></p>
            <% } %>
        </div>
        <div class="card-footer d-flex justify-content-end">
            <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">로그인으로</a>
        </div>
    </div>
</div>
</body>
</html>
