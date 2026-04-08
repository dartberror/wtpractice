<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="training_center.entity.Course" %>
<%@ page import="training_center.entity.Teacher" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Добавить занятие</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/site-background.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg bg-white border-bottom shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">Учебный центр</a>
        <div class="navbar-nav">
            <a class="nav-link active" href="${pageContext.request.contextPath}/courses">Курсы</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/teachers">Преподаватели</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/students">Обучающиеся</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/companies">Организации</a>
        </div>
    </div>
</nav>

<div class="container py-4">
    <%
        Course course = (Course) request.getAttribute("course");
        @SuppressWarnings("unchecked")
        List<Teacher> teachers = (List<Teacher>) request.getAttribute("teachers");
        Long selectedTeacherId = (Long) request.getAttribute("selectedTeacherId");
        String lessonDate = (String) request.getAttribute("lessonDate");
        String startTime = (String) request.getAttribute("startTime");
        String endTime = (String) request.getAttribute("endTime");
        String errorMessage = (String) request.getAttribute("errorMessage");

        if (lessonDate == null) {
            lessonDate = "";
        }
        if (startTime == null) {
            startTime = "";
        }
        if (endTime == null) {
            endTime = "";
        }
    %>

    <div class="mb-4">
        <h1 class="mb-1">Добавить занятие</h1>
        <p class="text-muted mb-0">Курс: <%= course.getTitle() %></p>
    </div>

    <%
        if (errorMessage != null && !errorMessage.isBlank()) {
    %>
    <div class="alert alert-danger"><%= errorMessage %></div>
    <%
        }
    %>

    <div class="card shadow-sm border-0 rounded-4">
        <div class="card-body p-4">
            <form method="post" action="${pageContext.request.contextPath}/courses/<%= course.getId() %>/schedule">

                <div class="mb-3">
                    <label class="form-label">Преподаватель</label>
                    <select name="teacherId" class="form-select" required>
                        <%
                            if (teachers != null) {
                                for (Teacher teacher : teachers) {
                                    boolean selected = selectedTeacherId != null && selectedTeacherId.equals(teacher.getId());
                        %>
                        <option value="<%= teacher.getId() %>" <%= selected ? "selected" : "" %>><%= teacher.getFullName() %></option>
                        <%
                                }
                            }
                        %>
                    </select>
                </div>

                <div class="mb-3">
                    <label class="form-label">Дата занятия</label>
                    <input type="date" name="lessonDate" class="form-control" value="<%= lessonDate %>" required>
                </div>

                <div class="mb-3">
                    <label class="form-label">Время начала</label>
                    <input type="time" name="startTime" class="form-control" value="<%= startTime %>" required>
                </div>

                <div class="mb-4">
                    <label class="form-label">Время окончания</label>
                    <input type="time" name="endTime" class="form-control" value="<%= endTime %>" required>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary">Сохранить</button>
                    <a href="${pageContext.request.contextPath}/courses/<%= course.getId() %>" class="btn btn-outline-secondary">Отмена</a>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>

