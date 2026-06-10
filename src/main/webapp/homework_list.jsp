<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.lmssystem.entity.Homework" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>과제 관리 시스템</title>
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; padding: 30px; background-color: #f8f9fa; }
        .container { max-width: 900px; margin: 0 auto; background: white; padding: 25px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        h2 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; margin-top: 30px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input, .form-group textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; box-sizing: border-box; }
        .btn { background-color: #3498db; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; }
        .btn:hover { background-color: #2980b9; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #eee; padding: 12px; text-align: left; }
        th { background-color: #34495e; color: white; }
        tr:nth-child(even) { background-color: #f9f9f9; }
    </style>
</head>
<body>

<div class="container">
    <h2>📝 새 과제 등록하기</h2>
    <form action="${pageContext.request.contextPath}/homework" method="post">
        <div class="form-group">
            <label>과제 제목</label>
            <input type="text" name="title" placeholder="과제 제목을 입력하세요" required>
        </div>
        <div class="form-group">
            <label>과제 상세 내용</label>
            <textarea name="content" rows="4" placeholder="과제 설명을 입력하세요" required></textarea>
        </div>
        <div class="form-group">
            <label>제출 마감 기한</label>
            <input type="date" name="dueDate" required>
        </div>
        <button type="submit" class="btn">과제 등록</button>
    </form>

    <h2>📊 과제 부여 현황</h2>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>과제 제목</th>
            <th>과제 내용</th>
            <th>마감 기한</th>
        </tr>
        </thead>
        <tbody>
        <%
            List<Homework> list = (List<Homework>) request.getAttribute("homeworkList");
            if (list != null && !list.isEmpty()) {
                for (Homework hw : list) {
        %>
        <tr>
            <td><%= hw.getId() %></td>
            <td><strong><%= hw.getTitle() %></strong></td>
            <td><%= hw.getContent() %></td>
            <td><span style="color: #e74c3c; font-weight: bold;"><%= hw.getDueDate() %></span></td>
        </tr>
        <%
            }
        } else {
        %>
        <tr>
            <td colspan="4" style="text-align: center; color: #95a5a6; padding: 30px;">아직 등록된 과제가 없습니다. 첫 과제를 등록해 보세요!</td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>

</body>
</html>