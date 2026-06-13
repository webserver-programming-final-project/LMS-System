<%@ page pageEncoding="UTF-8" %>
<%@ page import="com.example.lmssystem.entity.User" %>
<%
    User headerUser = (User) session.getAttribute("user");
    String headerPath = request.getRequestURI().substring(request.getContextPath().length());
    boolean headerProfessor = headerUser != null && headerUser.isProfessor();
%>
<style>
    .app-header {
        background: #212529;
        color: #fff;
        border-bottom: 1px solid rgba(255, 255, 255, .08);
    }
    .app-header .navbar-brand,
    .app-header .nav-link,
    .app-header .user-label {
        color: #fff;
    }
    .app-header .nav-link {
        opacity: .82;
        padding: .5rem .75rem;
        border-radius: .25rem;
    }
    .app-header .nav-link:hover,
    .app-header .nav-link.active {
        opacity: 1;
        background: rgba(255, 255, 255, .12);
        color: #fff;
    }
    .app-header .role-badge {
        font-size: .75rem;
        border: 1px solid rgba(255, 255, 255, .35);
        border-radius: 999px;
        padding: .15rem .5rem;
        margin-left: .35rem;
    }
    .app-footer {
        border-top: 1px solid #dee2e6;
        background: #fff;
        color: #6c757d;
        font-size: .875rem;
        margin-top: 3rem;
        padding: 1rem 0;
    }
</style>

<nav class="navbar navbar-expand app-header">
    <div class="container">
        <a class="navbar-brand fw-bold" href="<%= request.getContextPath() %>/lectures">LMS System</a>

        <% if (headerUser != null) { %>
            <div class="navbar-nav me-auto">
                <a class="nav-link <%= headerPath.equals("/lectures") ? "active" : "" %>"
                   href="<%= request.getContextPath() %>/lectures">강의</a>
                <a class="nav-link <%= headerPath.equals("/timetable") ? "active" : "" %>"
                   href="<%= request.getContextPath() %>/timetable">시간표</a>
                <% if (headerProfessor) { %>
                    <a class="nav-link <%= headerPath.equals("/lectures/add") ? "active" : "" %>"
                       href="<%= request.getContextPath() %>/lectures/add">강의 등록</a>
                <% } %>
            </div>

            <div class="d-flex align-items-center gap-3">
                <span class="user-label">
                    <%= headerUser.getName() %>
                    <span class="role-badge"><%= headerProfessor ? "교수" : "학생" %></span>
                </span>
                <a class="btn btn-outline-light btn-sm" href="<%= request.getContextPath() %>/logout">로그아웃</a>
            </div>
        <% } else { %>
            <div class="d-flex gap-2">
                <a class="btn btn-outline-light btn-sm" href="<%= request.getContextPath() %>/login">로그인</a>
                <a class="btn btn-light btn-sm" href="<%= request.getContextPath() %>/register">회원가입</a>
            </div>
        <% } %>
    </div>
</nav>
