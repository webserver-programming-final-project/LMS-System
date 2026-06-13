<%@ page pageEncoding="UTF-8" %>
<%@ page import="com.example.lmssystem.entity.Lecture" %>
<%@ page import="com.example.lmssystem.entity.TimeTable" %>
<%@ page import="com.example.lmssystem.entity.User" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%!
    private String formatDay(String day) {
        return day + "요일";
    }
%>

<%
    
    Lecture lecture     = (Lecture) request.getAttribute("lecture");
    List<TimeTable> tts = (List<TimeTable>) request.getAttribute("timeTables");
    User loginUser      = (User) session.getAttribute("user");
    String contextPath  = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>강의 상세</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/adminlte.css">
</head>
<body>

<%@ include file="../layout/header.jsp" %>

<div class="wrapper container py-4" style="max-width: 750px;">

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <% if (lecture == null) { %>
        <div class="alert alert-warning">강의 정보를 찾을 수 없습니다.</div>
    <% } else { %>
    
        <jsp:useBean id="lectureBean" class="com.example.lmssystem.entity.Lecture" scope="request"/>

        <div class="card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h2 class="card-title mb-0">강의 상세 정보</h2>
            </div>
            <div class="card-body">
                <table class="table table-bordered">
                    <tr>
                        <th style="width:140px;" class="table-light">강의 번호</th>
                        <td><%= lecture.getId() %></td>
                    </tr>
                    <tr>
                        <th class="table-light">강의명</th>
                        <td><%= lecture.getTitle() %></td>
                    </tr>
                    <tr>
                        <th class="table-light">강의실</th>
                        <td><%= lecture.getClassroom() %></td>
                    </tr>
                    <tr>
                        <th class="table-light">담당 교수</th>
                        <td><%= lecture.getProfessorName() %></td>
                    </tr>
                    <tr>
                        <th class="table-light">강의 설명</th>
                        <td><%= lecture.getDescription() %></td>
                    </tr>
                    <tr>
                        <th class="table-light">등록 과제 수</th>
                        <td><%= lecture.getHomeworkCount() %>개</td>
                    </tr>
                </table>
            </div>
        </div>
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="card-title mb-0">강의 시간표</h5>
            </div>
            <div class="card-body">
                <% if (tts == null || tts.isEmpty()) { %>
                    <p class="text-muted">등록된 시간표가 없습니다.</p>
                <% } else { %>
                    <table class="table table-bordered">
                        <thead class="table-dark">
                            <tr>
                                <th>요일</th>
                                <th>교시</th>
                                <th>시작 시간</th>
                                <th>종료 시간</th>
                            </tr>
                        </thead>
                        <tbody>
                       
                        <% for (TimeTable tt : tts) { %>
                            <tr>
                                <td><%= formatDay(tt.getDays()) %></td>
                                <td><%= tt.getSlotNo() %>교시</td>
                                <td><%= tt.getStartTime() %></td>
                                <td><%= tt.getEndTime() %></td>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>
                <% } %>
            </div>
        </div>

        
        <jsp:include page="/WEB-INF/views/lectures/lectureActions.jsp">
            <jsp:param name="lectureId" value="<%= lecture.getId() %>"/>
        </jsp:include>

    <% } %>

    <div class="mt-3">
        
        <a href="<%= contextPath %>/lectures" class="btn btn-secondary">← 목록으로</a>
    </div>
</div>

<%@ include file="../layout/footer.jsp" %>
</body>
</html>
