<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    String lectureId   = request.getParameter("lectureId");
    String isProfessor = request.getParameter("isProfessor");
%>
<div class="d-flex gap-2">
    <% if ("true".equals(isProfessor)) { %>
        <a href="${pageContext.request.contextPath}/homeworks/add?lectureId=<%= lectureId %>"
           class="btn btn-warning btn-sm">과제 등록</a>
    <% } %>
    <a href="${pageContext.request.contextPath}/lectures" class="btn btn-outline-secondary btn-sm">목록</a>
</div>
