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
    Map<String, Map<Integer, Lecture>> grid =
        (Map<String, Map<Integer, Lecture>>) request.getAttribute("grid");
    List<Lecture> myLectures = (List<Lecture>) request.getAttribute("myLectures");
    User loginUser = (User) session.getAttribute("user");

    String[] days  = {"월", "화", "수", "목", "금"};
    int[]    slots = {21, 22, 23, 24, 25, 26};
    String[] times = {
        "09:00~10:15", "10:30~11:45", "12:00~13:15",
        "13:30~14:45", "15:00~16:15", "16:30~17:45"
    };
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
        }
        .empty-cell { background: #fff; }
    </style>
</head>
<body>

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
                    <th class="time-col">교시 / 시간</th>
                    <% for (String day : days) { %>
                        <th><%= day %></th>
                    <% } %>
                </tr>
            </thead>
            <tbody>
                <% for (int i = 0; i < slots.length; i++) {
                       int slotNo = slots[i];
                %>
                    <tr>
                        <td class="time-col">
                            <%= slotNo %>교시<br>
                            <small><%= times[i] %></small>
                        </td>
                        <% for (String day : days) {
                               Lecture lec = (grid != null && grid.get(day) != null)
                                             ? grid.get(day).get(slotNo) : null;
                        %>
                            <td class="<%= lec == null ? "empty-cell" : "" %>">
                                <% if (lec != null) { %>
                                    <div class="lecture-block"
                                         style="background-color: <%= getLectureColor(lec.getId()) %>">
                                        <strong><%= lec.getTitle() %></strong><br>
                                        <small><%= lec.getClassroom() %></small>
                                    </div>
                                <% } %>
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
