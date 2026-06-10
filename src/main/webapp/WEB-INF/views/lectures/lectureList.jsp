<%@ page pageEncoding="UTF-8" %>
<%@ page import="com.example.lmssystem.entity.Lecture" %>
<%@ page import="com.example.lmssystem.entity.User" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%!
    private String formatHomeworkCount(int count) {
        return count == 0 ? "없음" : count + "개";
    }
%>

<%
    List<Lecture> lectureList = (List<Lecture>) request.getAttribute("lectureList");
    User loginUser = (User) session.getAttribute("user");

    String enrollMsg = (String) session.getAttribute("enrollMsg");
    if (enrollMsg != null) session.removeAttribute("enrollMsg");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>강의 목록</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/adminlte.css">
</head>
<body>

<%@ include file="../layout/header.jsp" %>

<c:if test="${not empty error}">
    <div class="alert alert-danger m-3">${error}</div>
</c:if>

<% if (enrollMsg != null) { %>
    <div class="alert alert-success m-3"><%= enrollMsg %></div>
<% } %>

<div class="wrapper container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>강의 목록</h2>
        <% if (loginUser != null && loginUser.isProfessor()) { %>
            <a href="${pageContext.request.contextPath}/lectures/add"
               class="btn btn-primary">+ 강의 등록</a>
        <% } %>
    </div>

    <table class="table table-bordered table-hover">
        <thead class="table-dark">
            <tr>
                <th>번호</th>
                <th>강의명</th>
                <th>강의실</th>
                <th>담당 교수</th>
                <th>등록 과제 수</th>
                <th>상세</th>
                <% if (loginUser != null && !loginUser.isProfessor()) { %>
                    <th>수강 신청</th>
                <% } %>
            </tr>
        </thead>
        <tbody>
        <%
            if (lectureList == null || lectureList.isEmpty()) {
        %>
            <tr>
                <td colspan="7" class="text-center text-muted py-4">
                    등록된 강의가 없습니다.
                </td>
            </tr>
        <%
            } else {
                for (int i = 0; i < lectureList.size(); i++) {
                    Lecture lec = lectureList.get(i);
        %>
            <tr>
                <td><%= (i + 1) %></td>
                <td><%= lec.getTitle() %></td>
                <td><%= lec.getClassroom() %></td>
                <td><%= lec.getProfessorName() %></td>
                <td><%= formatHomeworkCount(lec.getHomeworkCount()) %></td>
                <td>
                    <a href="${pageContext.request.contextPath}/lectures?id=<%= lec.getId() %>"
                       class="btn btn-sm btn-outline-primary">상세 보기</a>
                </td>
                <% if (loginUser != null && !loginUser.isProfessor()) { %>
                    <td>
                        <form name="enrollForm" action="${pageContext.request.contextPath}/enroll"
                              method="post" style="display:inline;">
                            <input type="hidden" name="classId" value="<%= lec.getId() %>">
                            <input type="submit" class="btn btn-sm btn-success" value="수강 신청">
                        </form>
                    </td>
                <% } %>
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
