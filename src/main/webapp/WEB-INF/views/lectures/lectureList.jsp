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
    List<Lecture> enrolledLectureList = (List<Lecture>) request.getAttribute("enrolledLectureList");
    List<Lecture> availableLectureList = (List<Lecture>) request.getAttribute("availableLectureList");
    User loginUser = (User) session.getAttribute("user");
    boolean isProfessor = loginUser != null && loginUser.isProfessor();

    String enrollMsg = (String) session.getAttribute("enrollMsg");
    if (enrollMsg != null) session.removeAttribute("enrollMsg");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>강의 목록</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/adminlte.css">
    <style>
        .tab-pane { display: none; }
        .tab-pane.active { display: block; }
    </style>
</head>
<body>

<%@ include file="../layout/header.jsp" %>

<c:if test="${not empty error}">
    <div class="alert alert-danger m-3">${error}</div>
    <%
        session.removeAttribute("error");
    %>
</c:if>

<% if (enrollMsg != null) { %>
    <div class="alert alert-success m-3"><%= enrollMsg %></div>
    <%
        session.removeAttribute("enrollMsg");
    %>
<% } %>

<div class="wrapper container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2><%= isProfessor ? "내 강의 목록" : "강의 목록" %></h2>
        <% if (isProfessor) { %>
            <a href="${pageContext.request.contextPath}/lectures/add"
               class="btn btn-primary">+ 강의 등록</a>
        <% } %>
    </div>

    <% if (isProfessor) { %>
    <table class="table table-bordered table-hover">
        <thead class="table-dark">
            <tr>
                <th>번호</th>
                <th>강의명</th>
                <th>강의실</th>
                <th>담당 교수</th>
                <th>등록 과제 수</th>
                <th>상세</th>
                <th>수정</th>
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
                <td>
                    <a href="${pageContext.request.contextPath}/lectures/edit?id=<%= lec.getId() %>"
                       class="btn btn-sm btn-warning">수정</a>
                </td>
            </tr>
        <%
                }
            }
        %>
        </tbody>
    </table>
    <% } else { %>
        <ul class="nav nav-tabs mb-3" id="lectureTabs" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" data-tab-target="enrolled" type="button" role="tab">
                    수강 중인 강의
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" data-tab-target="available" type="button" role="tab">
                    수강 신청 가능
                </button>
            </li>
        </ul>

        <div class="tab-content" id="lectureTabContent">
            <div class="tab-pane active" id="enrolled" role="tabpanel">
                <table class="table table-bordered table-hover">
                    <thead class="table-dark">
                    <tr>
                        <th>번호</th>
                        <th>강의명</th>
                        <th>강의실</th>
                        <th>담당 교수</th>
                        <th>등록 과제 수</th>
                        <th>상세</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (enrolledLectureList == null || enrolledLectureList.isEmpty()) {
                    %>
                        <tr>
                            <td colspan="6" class="text-center text-muted py-4">
                                수강 중인 강의가 없습니다.
                            </td>
                        </tr>
                    <%
                        } else {
                            for (int i = 0; i < enrolledLectureList.size(); i++) {
                                Lecture lec = enrolledLectureList.get(i);
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
                        </tr>
                    <%
                            }
                        }
                    %>
                    </tbody>
                </table>
            </div>

            <div class="tab-pane" id="available" role="tabpanel">
                <table class="table table-bordered table-hover">
                    <thead class="table-dark">
                    <tr>
                        <th>번호</th>
                        <th>강의명</th>
                        <th>강의실</th>
                        <th>담당 교수</th>
                        <th>등록 과제 수</th>
                        <th>상세</th>
                        <th>수강 신청</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (availableLectureList == null || availableLectureList.isEmpty()) {
                    %>
                        <tr>
                            <td colspan="7" class="text-center text-muted py-4">
                                수강 신청 가능한 강의가 없습니다.
                            </td>
                        </tr>
                    <%
                        } else {
                            for (int i = 0; i < availableLectureList.size(); i++) {
                                Lecture lec = availableLectureList.get(i);
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
                            <td>
                                <form name="enrollForm" action="${pageContext.request.contextPath}/enroll"
                                      method="post" style="display:inline;">
                                    <input type="hidden" name="classId" value="<%= lec.getId() %>">
                                    <input type="submit" class="btn btn-sm btn-success" value="수강 신청">
                                </form>
                            </td>
                        </tr>
                    <%
                            }
                        }
                    %>
                    </tbody>
                </table>
            </div>
        </div>
    <% } %>
</div>

<script>
    document.querySelectorAll('[data-tab-target]').forEach(function(button) {
        button.addEventListener('click', function() {
            document.querySelectorAll('[data-tab-target]').forEach(function(tab) {
                tab.classList.remove('active');
            });
            document.querySelectorAll('.tab-pane').forEach(function(pane) {
                pane.classList.remove('active');
            });
            button.classList.add('active');
            document.getElementById(button.dataset.tabTarget).classList.add('active');
        });
    });
</script>

<%@ include file="../layout/footer.jsp" %>
</body>
</html>
