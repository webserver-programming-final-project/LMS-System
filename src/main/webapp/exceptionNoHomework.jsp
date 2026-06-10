<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/adminlte.css">
    <meta charset="UTF-8">
    <title>과제 없음</title>
</head>
<body>
<div class="wrapper container min-vh-100 d-flex justify-content-center align-items-center">
    <div class="card" style="min-width: 40vw">
        <div class="card-header">
            <h2 class="alert alert-danger">해당 과제를 찾을 수 없습니다.</h2>
        </div>
        <div class="card-body">
            <p><%= request.getRequestURL() %>?<%= request.getQueryString() %></p>
        </div>
        <div class="card-footer d-flex justify-content-end">
            <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">홈으로</a>
        </div>
    </div>
</div>
</body>
</html>
