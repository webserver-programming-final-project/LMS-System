<%--
  Created by IntelliJ IDEA.
  User: hoshi
  Date: 26. 6. 2.
  Time: 오후 3:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<html>
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/adminlte.css">
    <title>회원가입</title>
</head>
<body>
<c:if test="${not empty error}">
    <div class="alert alert-danger">
            ${error}
    </div>
</c:if>
<div class="wrapper container min-vh-100 d-flex justify-content-center align-items-center">
    <form action="register" method="post">
        <div class="card" style="min-width: 40vw">
            <div class="card-header">
                <h2 class="card-title">회원가입</h2>
            </div>
            <div class="card-body">
                <input type="text" name="name" id="email_field"
                       class="form-control mb-2" placeholder="name" required>
                <input type="email" name="email" id="email_field"
                       class="form-control mb-2" placeholder="email" required>
                <input type="password" name="password" id="password_field"
                       class="form-control mb-2" placeholder="password" required>
                <input type="checkbox" name="is_professor" id="is_professor_checkbox" class="form-check-input mb-2">
                <label for="is_professor_checkbox">교수 입니다.</label>
            </div>
            <div class="card-footer d-flex justify-content-end">
                <input type="submit" class="btn btn-primary ms-2" value="회원가입">
            </div>
        </div>
    </form>
</body>
</html>
