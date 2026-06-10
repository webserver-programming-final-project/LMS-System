<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.apache.commons.fileupload2.core.*" %>
<%@ page import="org.apache.commons.fileupload2.jakarta.servlet6.*" %>
<%@ page import="com.example.lmssystem.entity.User" %>
<%@ page import="java.io.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
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

    // 2) multipart 파싱 (commons-fileupload2)
    String fileUploadPath = "/Users/pyosang-gyun/lms-uploads";
    File dir = new File(fileUploadPath);
    if (!dir.exists()) dir.mkdirs();

    JakartaServletFileUpload<DiskFileItem, DiskFileItemFactory> upload =
        new JakartaServletFileUpload<>(DiskFileItemFactory.builder().get());
    List<DiskFileItem> items = upload.parseRequest(request);

    Long homeworkId = null;
    String studentNo = "";
    String studentName = "";
    String savedPath = null;
    String savedName = null;

    for (DiskFileItem item : items) {
        if (item.isFormField()) {
            String name = item.getFieldName();
            String value = item.getString();
            if ("homeworkId".equals(name)) homeworkId = Long.valueOf(value);
            else if ("studentNo".equals(name)) studentNo = value;
            else if ("studentName".equals(name)) studentName = value;
        } else {
            String origName = item.getName();
            if (origName == null || origName.isEmpty()) continue;
            origName = origName.substring(origName.lastIndexOf("/") + 1);
            savedName = System.currentTimeMillis() + "_" + origName;
            File target = new File(fileUploadPath + "/" + savedName);
            item.write(target.toPath());
            savedPath = target.getAbsolutePath();
        }
    }

    if (homeworkId == null) {
        response.sendRedirect("exceptionNoHomework.jsp");
        return;
    }
    if (savedPath == null) {
        // 파일 미선택 → 폼으로 되돌리기
        response.sendRedirect("submitHomework.jsp?homeworkId=" + homeworkId);
        return;
    }

    // 3) 학번·이름 쿠키 30일 저장
    Cookie cNo = new Cookie("student_no", studentNo);
    cNo.setMaxAge(60 * 60 * 24 * 30);
    response.addCookie(cNo);
    Cookie cName = new Cookie("student_name", studentName);
    cName.setMaxAge(60 * 60 * 24 * 30);
    response.addCookie(cName);

    // 4) 과제·학생 정보 조회
    long classId = -1L;
    Timestamp endTo = null;
    PreparedStatement hp = conn.prepareStatement(
        "SELECT class_id, end_to FROM homeworks WHERE id = ?");
    hp.setLong(1, homeworkId);
    ResultSet hRs = hp.executeQuery();
    if (hRs.next()) {
        classId = hRs.getLong("class_id");
        endTo = hRs.getTimestamp("end_to");
    }
    hRs.close();
    hp.close();
    if (classId < 0) {
        conn.close();
        response.sendRedirect("exceptionNoHomework.jsp");
        return;
    }

    if (endTo != null && new java.util.Date().after(endTo)) {
        conn.close();
        response.sendError(500, "마감일이 지났습니다.");
        return;
    }

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

    // 5) files INSERT
    long fileId;
    PreparedStatement fp = conn.prepareStatement(
        "INSERT INTO files (path) VALUES (?)",
        PreparedStatement.RETURN_GENERATED_KEYS);
    fp.setString(1, savedPath);
    fp.executeUpdate();
    ResultSet fRs = fp.getGeneratedKeys();
    fRs.next();
    fileId = fRs.getLong(1);
    fRs.close();
    fp.close();

    // 6) submissions upsert (UNIQUE(homework_id, student_id) 활용)
    long submissionId = -1L;
    PreparedStatement findSub = conn.prepareStatement(
        "SELECT id FROM submissions WHERE homework_id = ? AND student_id = ?");
    findSub.setLong(1, homeworkId);
    findSub.setLong(2, studentId);
    ResultSet fsRs = findSub.executeQuery();
    if (fsRs.next()) submissionId = fsRs.getLong(1);
    fsRs.close();
    findSub.close();

    if (submissionId < 0) {
        PreparedStatement insSub = conn.prepareStatement(
            "INSERT INTO submissions (homework_id, student_id) VALUES (?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS);
        insSub.setLong(1, homeworkId);
        insSub.setLong(2, studentId);
        insSub.executeUpdate();
        ResultSet isRs = insSub.getGeneratedKeys();
        isRs.next();
        submissionId = isRs.getLong(1);
        isRs.close();
        insSub.close();
    } else {
        // 재제출: submitted_at 만 갱신
        PreparedStatement upSub = conn.prepareStatement(
            "UPDATE submissions SET submitted_at = CURRENT_TIMESTAMP WHERE id = ?");
        upSub.setLong(1, submissionId);
        upSub.executeUpdate();
        upSub.close();
    }

    // 7) submission_attachments 교체 (기존 모두 지우고 새 file_id 연결)
    PreparedStatement delAtt = conn.prepareStatement(
        "DELETE FROM submission_attachments WHERE submission_id = ?");
    delAtt.setLong(1, submissionId);
    delAtt.executeUpdate();
    delAtt.close();

    PreparedStatement insAtt = conn.prepareStatement(
        "INSERT INTO submission_attachments (submission_id, file_id) VALUES (?, ?)");
    insAtt.setLong(1, submissionId);
    insAtt.setLong(2, fileId);
    insAtt.executeUpdate();
    insAtt.close();

    conn.close();
%>
<html>
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/adminlte.css">
    <meta charset="UTF-8">
    <title>제출 완료</title>
</head>
<body>
<div class="wrapper container min-vh-100 d-flex justify-content-center align-items-center">
    <div class="card" style="min-width: 40vw">
        <div class="card-header">
            <h2 class="card-title">제출 완료</h2>
        </div>
        <div class="card-body">
            <p>학번: <%= studentNo %> / 이름: <%= studentName %></p>
            <p>업로드 파일: <%= savedName %></p>
        </div>
        <div class="card-footer d-flex justify-content-end">
            <a href="submitHomework.jsp?homeworkId=<%= homeworkId %>"
               class="btn btn-secondary">다시 보기</a>
        </div>
    </div>
</div>
</body>
</html>
