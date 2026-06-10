<%@ page pageEncoding="UTF-8" %>
<%@ page import="com.example.lmssystem.entity.User" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    String errorMsg = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>강의 등록</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/adminlte.css">
</head>
<body>
<%@ include file="../layout/header.jsp" %>

<c:if test="${not empty error}">
    <div class="alert alert-danger m-3">${error}</div>
</c:if>

<div class="wrapper container py-4">
    <div class="card" style="max-width: 700px; margin: 0 auto;">
        <div class="card-header">
            <h2 class="card-title">강의 등록</h2>
        </div>

        <form name="frm" action="${pageContext.request.contextPath}/lectures/add" method="post">
            <div class="card-body">

                <div class="mb-3">
                    <label class="form-label fw-bold">강의명 <span class="text-danger">*</span></label>
                  
                    <input type="text" name="title" class="form-control"
                           placeholder="강의명을 입력하세요" maxlength="50" required>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">강의실 <span class="text-danger">*</span></label>
                    <input type="text" name="classroom" class="form-control"
                           placeholder="예) A관 301호" maxlength="50" required>
                </div>
                <div class="mb-3">
                    <label class="form-label fw-bold">강의 설명</label>
                    <textarea name="description" class="form-control" rows="3"
                              placeholder="강의 설명을 입력하세요"></textarea>
                </div>
                <div class="mb-3">
                    <label class="form-label fw-bold">요일 / 교시 <span class="text-danger">*</span></label>
                    <div id="timeSlotContainer">
                        <div class="d-flex gap-2 mb-2 time-row">
                            <select name="days" class="form-control" style="width:120px;" required>
                                <option value="">요일 선택</option>
                                <option value="월">월</option>
                                <option value="화">화</option>
                                <option value="수">수</option>
                                <option value="목">목</option>
                                <option value="금">금</option>
                            </select>
                            <select name="slots" class="form-control" style="width:220px;" required>
                                <option value="">교시 선택</option>
                                <option value="10">21교시 (09:00~10:15)</option>
                                <option value="11">22교시 (10:30~11:45)</option>
                                <option value="12">23교시 (12:00~13:15)</option>
                                <option value="13">24교시 (13:30~14:45)</option>
                                <option value="14">25교시 (15:00~16:15)</option>
                                <option value="15">26교시 (16:30~17:45)</option>
                            </select>
                            <button type="button" class="btn btn-danger btn-sm"
                                    onclick="removeTimeRow(this)">삭제</button>
                        </div>
                    </div>
                    <button type="button" class="btn btn-secondary btn-sm mt-1"
                            onclick="addTimeRow()">+ 요일/교시 추가</button>
                </div>

            </div>

            <div class="card-footer d-flex justify-content-end">
                <a href="${pageContext.request.contextPath}/lectures"
                   class="btn btn-secondary me-2">취소</a>
                <input type="submit" class="btn btn-primary" value="등록"
                       onclick="return checkForm()">
            </div>
        </form>
    </div>
</div>

<script type="text/javascript">
    function addTimeRow() {
        var container = document.getElementById('timeSlotContainer');
        var firstRow  = container.querySelector('.time-row');
        var newRow    = firstRow.cloneNode(true);
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
        if (title.length > 50) {
            alert("강의명은 50자 이내로 입력해주세요.");
            document.frm.title.focus();
            return false;
        }
        var regExp = /^[a-zA-Z\uAC00-\uD7A3]/;
        if (!regExp.test(title)) {
            alert("강의명은 한글 또는 영문자로 시작해야 합니다.");
            document.frm.title.focus();
            return false;
        }

        var classroom = document.frm.classroom.value;
        if (classroom.length === 0) {
            alert("강의실을 입력해주세요.");
            document.frm.classroom.focus();
            return false;
        }

        var daySelects  = document.querySelectorAll('select[name="days"]');
        var slotSelects = document.querySelectorAll('select[name="slots"]');
        for (var i = 0; i < daySelects.length; i++) {
            if (daySelects[i].value === '') {
                alert((i + 1) + "번째 행의 요일을 선택해주세요.");
                daySelects[i].focus();
                return false;
            }
            if (slotSelects[i].value === '') {
                alert((i + 1) + "번째 행의 교시를 선택해주세요.");
                slotSelects[i].focus();
                return false;
            }
        }

        var seen = {};
        for (var j = 0; j < daySelects.length; j++) {
            var key = daySelects[j].value + '_' + slotSelects[j].value;
            if (seen[key]) {
                alert("같은 요일과 교시가 중복되어 있습니다. 확인해주세요.");
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
