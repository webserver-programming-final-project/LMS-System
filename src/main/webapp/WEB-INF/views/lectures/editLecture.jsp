<%@ page pageEncoding="UTF-8" %>
<%@ page import="com.example.lmssystem.entity.Lecture" %>
<%@ page import="com.example.lmssystem.entity.TimeTable" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    Lecture lecture = (Lecture) request.getAttribute("lecture");
    List<TimeTable> timeTables = (List<TimeTable>) request.getAttribute("timeTables");
    List<TimeTable> timeSlots = (List<TimeTable>) request.getAttribute("timeSlots");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>강의 수정</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/adminlte.css">
</head>
<body>
<%@ include file="../layout/header.jsp" %>

<c:if test="${not empty error}">
    <div class="alert alert-danger m-3">${error}</div>
</c:if>

<div class="wrapper container py-4">
    <% if (lecture == null) { %>
        <div class="alert alert-warning">강의 정보를 찾을 수 없습니다.</div>
    <% } else { %>
    <div class="card" style="max-width: 700px; margin: 0 auto;">
        <div class="card-header">
            <h2 class="card-title">강의 수정</h2>
        </div>

        <form name="frm" action="${pageContext.request.contextPath}/lectures/edit" method="post">
            <input type="hidden" name="id" value="<%= lecture.getId() %>">
            <div class="card-body">
                <div class="mb-3">
                    <label class="form-label fw-bold">강의명 <span class="text-danger">*</span></label>
                    <input type="text" name="title" class="form-control"
                           value="<%= lecture.getTitle() %>" maxlength="50" required>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">강의실 <span class="text-danger">*</span></label>
                    <input type="text" name="classroom" class="form-control"
                           value="<%= lecture.getClassroom() %>" maxlength="50" required>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">강의 설명</label>
                    <textarea name="description" class="form-control" rows="3"><%= lecture.getDescription() == null ? "" : lecture.getDescription() %></textarea>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">요일 / 교시 <span class="text-danger">*</span></label>
                    <div id="timeSlotContainer">
                        <%
                            if (timeTables == null || timeTables.isEmpty()) {
                                timeTables = java.util.List.of(new TimeTable());
                            }
                            for (TimeTable tt : timeTables) {
                                String day = tt.getDays() == null ? "" : tt.getDays();
                                Long slotId = tt.getSlotId();
                        %>
                        <div class="d-flex gap-2 mb-2 time-row">
                            <select name="days" class="form-control" style="width:120px;" required>
                                <option value="">요일 선택</option>
                                <option value="월" <%= "월".equals(day) ? "selected" : "" %>>월</option>
                                <option value="화" <%= "화".equals(day) ? "selected" : "" %>>화</option>
                                <option value="수" <%= "수".equals(day) ? "selected" : "" %>>수</option>
                                <option value="목" <%= "목".equals(day) ? "selected" : "" %>>목</option>
                                <option value="금" <%= "금".equals(day) ? "selected" : "" %>>금</option>
                            </select>
                            <select name="slots" class="form-control" style="width:220px;" required>
                                <option value="">교시 선택</option>
                                <%
                                    if (timeSlots != null) {
                                        for (TimeTable slot : timeSlots) {
                                            Long optionSlotId = slot.getSlotId();
                                            String startTime = slot.getStartTime();
                                            String endTime = slot.getEndTime();
                                %>
                                    <option value="<%= optionSlotId %>" <%= optionSlotId.equals(slotId) ? "selected" : "" %>>
                                        <%= slot.getSlotNo() %>교시 (<%= startTime %>~<%= endTime %>)
                                    </option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                            <button type="button" class="btn btn-danger btn-sm"
                                    onclick="removeTimeRow(this)">삭제</button>
                        </div>
                        <% } %>
                    </div>
                    <button type="button" class="btn btn-secondary btn-sm mt-1"
                            onclick="addTimeRow()">+ 요일/교시 추가</button>
                </div>
            </div>

            <div class="card-footer d-flex justify-content-end">
                <a href="${pageContext.request.contextPath}/lectures"
                   class="btn btn-secondary me-2">취소</a>
                <input type="submit" class="btn btn-primary" value="수정"
                       onclick="return checkForm()">
            </div>
        </form>
    </div>
    <% } %>
</div>

<script type="text/javascript">
    function addTimeRow() {
        var container = document.getElementById('timeSlotContainer');
        var firstRow = container.querySelector('.time-row');
        var newRow = firstRow.cloneNode(true);
        newRow.querySelectorAll('select').forEach(function(sel) { sel.selectedIndex = 0; });
        container.appendChild(newRow);
    }

    function removeTimeRow(btn) {
        var container = document.getElementById('timeSlotContainer');
        var rows = container.querySelectorAll('.time-row');
        if (rows.length === 1) {
            alert("최소 한 개의 요일/교시는 필요합니다.");
            return;
        }
        btn.closest('.time-row').remove();
    }

    function checkForm() {
        var title = document.frm.title.value;
        if (title.length === 0) {
            alert("강의명을 입력해주세요.");
            document.frm.title.focus();
            return false;
        }

        var classroom = document.frm.classroom.value;
        if (classroom.length === 0) {
            alert("강의실을 입력해주세요.");
            document.frm.classroom.focus();
            return false;
        }

        var daySelects = document.querySelectorAll('select[name="days"]');
        var slotSelects = document.querySelectorAll('select[name="slots"]');
        var seen = {};
        for (var i = 0; i < daySelects.length; i++) {
            if (daySelects[i].value === '' || slotSelects[i].value === '') {
                alert((i + 1) + "번째 행의 요일과 교시를 선택해주세요.");
                return false;
            }
            var key = daySelects[i].value + '_' + slotSelects[i].value;
            if (seen[key]) {
                alert("같은 요일과 교시가 중복되어 있습니다.");
                return false;
            }
            seen[key] = true;
        }

        return true;
    }
</script>

<%@ include file="../layout/footer.jsp" %>
</body>
</html>
