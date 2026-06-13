<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    String lectureId   = request.getParameter("lectureId");
%>
<div class="d-flex gap-2">
    <a href="${pageContext.request.contextPath}/homework?classId=<%= lectureId %>"
       class="btn btn-warning btn-sm">과제</a>
    <a href="${pageContext.request.contextPath}/lectures" class="btn btn-outline-secondary btn-sm">목록</a>
</div>
