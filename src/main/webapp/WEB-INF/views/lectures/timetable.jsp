<%@ page pageEncoding="UTF-8" %>
<%@ page import="com.example.lmssystem.entity.Lecture" %>
<%@ page import="com.example.lmssystem.entity.User" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%!
    private static final String[] COLORS = {
        "#4e73df", "#1cc88a", "#36b9cc", "#f6c23e",
        "#e74a3b", "#858796", "#5a5c69", "#2e59d9"
    };

    private String getLectureColor(Long lectureId) {
        int idx = (int)(lectureId % COLORS.length);
        return COLORS[idx];
    }
%>

<%
    Map<String, Map<Integer, List<Lecture>>> grid =
        (Map<String, Map<Integer, List<Lecture>>>) request.getAttribute("grid");
    List<Lecture> myLectures = (List<Lecture>) request.getAttribute("myLectures");
    User loginUser = (User) session.getAttribute("user");
    int[] hours = (int[]) request.getAttribute("hours");

    String[] days  = {"월", "화", "수", "목", "금"};
    if (hours == null) {
        hours = new int[] {9, 10, 11, 12, 13, 14, 15, 16, 17};
    }
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>주간 시간표</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/adminlte.css">
    <style>
        .timetable { width: 100%; border-collapse: collapse; table-layout: fixed; }
        .timetable th, .timetable td {
            border: 1px solid #dee2e6;
            text-align: center;
            padding: 6px 4px;
            vertical-align: middle;
            font-size: 13px;
        }
        .timetable th { background: #343a40; color: #fff; }
        .timetable .time-col { background: #f8f9fa; color: #555; font-size: 11px; width: 90px; }
        .lecture-block {
            border-radius: 6px;
            color: #fff;
            padding: 6px 4px;
            font-size: 12px;
            line-height: 1.4;
            margin-bottom: 4px;
        }
        .lecture-block:last-child { margin-bottom: 0; }
        .empty-cell { background: #fff; }
    </style>
</head>
<body>
<%--주간시간표 수정 --%>

<%@ include file="../layout/header.jsp" %>

<c:if test="${not empty error}">
    <div class="alert alert-danger m-3">${error}</div>
</c:if>

<div class="wrapper container py-4">
    <h2 class="mb-4">주간 시간표</h2>

    <% if (myLectures == null || myLectures.isEmpty()) { %>
        <div class="alert alert-info">등록된 강의가 없습니다.</div>
    <% } else { %>

    <div class="table-responsive">
        <table class="timetable">
            <thead>
                <tr>
                    <th class="time-col">시간</th>
                    <% for (String day : days) { %>
                        <th><%= day %></th>
                    <% } %>
                </tr>
            </thead>
            <tbody>
                <% for (int hour : hours) {
                       String timeLabel = String.format("%02d:00~%02d:00", hour, hour + 1);
                %>
                    <tr>
                        <td class="time-col">
                            <%= timeLabel %>
                        </td>
                        <% for (String day : days) {
                               List<Lecture> lectures = (grid != null && grid.get(day) != null)
                                             ? grid.get(day).get(hour) : null;
                               boolean empty = lectures == null || lectures.isEmpty();
                        %>
                            <td class="<%= empty ? "empty-cell" : "" %>">
                                <% if (!empty) {
                                       for (Lecture lec : lectures) {
                                %>
                                    <div class="lecture-block"
                                         style="background-color: <%= getLectureColor(lec.getId()) %>">
                                        <strong><%= lec.getTitle() %></strong><br>
                                        <small><%= lec.getClassroom() %></small>
                                    </div>
                                <%
                                       }
                                   }
                                %>
                            </td>
                        <% } %>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>

    <% } %>
</div>

<%@ include file="../layout/footer.jsp" %>
</body>
</html>
