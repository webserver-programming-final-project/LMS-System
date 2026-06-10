<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.example.lmssystem.entity.User" %>
<%@ include file="dbconn.jsp" %>
<%
    // 1) 로그인 체크
    User loginUser = (User) session.getAttribute("user");
    if (loginUser == null) {
        response.sendRedirect("login");
        return;
    }
    if (loginUser.isProfessor()) {
        response.sendError(403, "교수는 과제를 제출할 수 없습니다.");
        return;
    }

    // 2) 과제 id
    String homeworkIdStr = request.getParameter("homeworkId");
    if (homeworkIdStr == null || homeworkIdStr.isEmpty()) {
        response.sendRedirect("exceptionNoHomework.jsp");
        return;
    }
    long homeworkId;
    try {
        homeworkId = Long.parseLong(homeworkIdStr);
    } catch (NumberFormatException e) {
        response.sendRedirect("exceptionNoHomework.jsp");
        return;
    }

    // 3) 과제 조회
    String hwTitle = null;
    long classId = -1L;
    Timestamp endTo = null;
    PreparedStatement hp = conn.prepareStatement(
        "SELECT title, class_id, end_to FROM homeworks WHERE id = ?");
    hp.setLong(1, homeworkId);
    ResultSet hRs = hp.executeQuery();
    if (hRs.next()) {
        hwTitle = hRs.getString("title");
        classId = hRs.getLong("class_id");
        endTo = hRs.getTimestamp("end_to");
    }
    hRs.close();
    hp.close();
    if (hwTitle == null) {
        // 조회 결과 null 안내 페이지
        conn.close();
        response.sendRedirect("exceptionNoHomework.jsp");
        return;
    }

    // 4) 학생 id 조회 (수강 안 하면 거절)
    long studentId = -1L;
    PreparedStatement sp = conn.prepareStatement(
        "SELECT id FROM students WHERE user_id = ? AND class_id = ?");
    sp.setLong(1, loginUser.getId());
    sp.setLong(2, classId);
    ResultSet sRs = sp.executeQuery();
    if (sRs.next()) studentId = sRs.getLong("id");
    sRs.close();
    sp.close();
    if (studentId < 0) {
        conn.close();
        response.sendError(403, "이 과제 제출 권한이 없습니다.");
        return;
    }

    // 5) 이미 제출했는지 확인
    Timestamp lastSubmittedAt = null;
    PreparedStatement sub = conn.prepareStatement(
        "SELECT submitted_at FROM submissions WHERE homework_id = ? AND student_id = ?");
    sub.setLong(1, homeworkId);
    sub.setLong(2, studentId);
    ResultSet subRs = sub.executeQuery();
    if (subRs.next()) lastSubmittedAt = subRs.getTimestamp("submitted_at");
    subRs.close();
    sub.close();
    conn.close();

    // 6) 쿠키에서 학번·이름 자동완성
    String cookieStudentNo = String.valueOf(studentId);
    String cookieStudentName = loginUser.getName() == null ? "" : loginUser.getName();
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie c : cookies) {
            if ("student_no".equals(c.getName())) cookieStudentNo = c.getValue();
            else if ("student_name".equals(c.getName())) cookieStudentName = c.getValue();
        }
    }
%>
<html>
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/adminlte.css">
    <meta charset="UTF-8">
    <title>과제 제출</title>
</head>
<body>
<div class="wrapper container min-vh-100 d-flex justify-content-center align-items-center">
    <form action="processSubmitHomework.jsp" method="post" enctype="multipart/form-data">
        <input type="hidden" name="homeworkId" value="<%= homeworkId %>">
        <div class="card" style="min-width: 40vw">
            <div class="card-header">
                <h2 class="card-title">과제 제출 — <%= hwTitle %></h2>
            </div>
            <div class="card-body">
                <p>
                    마감:
                    <%= endTo == null ? "없음" : endTo.toString() %>
                </p>
                <% if (lastSubmittedAt != null) { %>
                <div class="alert alert-warning">
                    이미 제출한 과제입니다. 새 파일을 올리면 기존 제출이 갱신됩니다.
                    (마지막 제출: <%= lastSubmittedAt %>)
                </div>
                <% } %>
                <p>학번 : <input type="text" name="studentNo"
                       class="form-control mb-2"
                       value="<%= cookieStudentNo %>"></p>
                <p>이름 : <input type="text" name="studentName"
                       class="form-control mb-2"
                       value="<%= cookieStudentName %>"></p>
                <p>파일 : <input type="file" name="file"
                       class="form-control mb-2" required></p>
            </div>
            <div class="card-footer d-flex justify-content-end">
                <input type="submit" class="btn btn-primary" value="제출">
            </div>
        </div>
    </form>
</div>
</body>
</html>
