<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="training_center.entity.Schedule" %>
<%@ page import="training_center.entity.Student" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Расписание обучающегося</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/site-background.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg bg-white border-bottom shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">Учебный центр</a>
        <div class="navbar-nav">
            <a class="nav-link" href="${pageContext.request.contextPath}/courses">Курсы</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/teachers">Преподаватели</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/students">Обучающиеся</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/companies">Организации</a>
        </div>
    </div>
</nav>

<div class="container py-4">
    <%
        Student student = (Student) request.getAttribute("student");
        @SuppressWarnings("unchecked")
        List<Schedule> schedule = (List<Schedule>) request.getAttribute("schedule");
        @SuppressWarnings("unchecked")
        Map<Long, String> scheduleCourseTitles = (Map<Long, String>) request.getAttribute("scheduleCourseTitles");
        @SuppressWarnings("unchecked")
        Map<Long, String> scheduleTeacherNames = (Map<Long, String>) request.getAttribute("scheduleTeacherNames");
        String from = (String) request.getAttribute("from");
        String to = (String) request.getAttribute("to");
        String errorMessage = (String) request.getAttribute("errorMessage");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    %>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="mb-1">Расписание обучающегося</h1>
            <p class="text-muted mb-0"><%= student.getFullName() %></p>
        </div>
        <a href="${pageContext.request.contextPath}/students/<%= student.getId() %>" class="btn btn-outline-secondary">Назад</a>
    </div>

    <div class="card shadow-sm border-0 rounded-4 mb-4">
        <div class="card-body">
            <form method="get" action="${pageContext.request.contextPath}/students/<%= student.getId() %>/schedule" class="row g-3 align-items-end">
                <div class="col-md-4">
                    <label class="form-label">С даты</label>
                    <input type="date" name="from" class="form-control" value="<%= from %>">
                </div>
                <div class="col-md-4">
                    <label class="form-label">По дату</label>
                    <input type="date" name="to" class="form-control" value="<%= to %>">
                </div>
                <div class="col-md-4 d-flex gap-2">
                    <button type="submit" class="btn btn-primary">Показать</button>
                    <a href="${pageContext.request.contextPath}/students/<%= student.getId() %>/schedule" class="btn btn-outline-secondary">Сбросить</a>
                </div>
            </form>
        </div>
    </div>

    <%
        if (errorMessage != null && !errorMessage.isBlank()) {
    %>
    <div class="alert alert-danger"><%= errorMessage %></div>
    <%
        }
    %>

    <div class="card shadow-sm border-0 rounded-4">
        <div class="card-body p-0">
            <table class="table table-striped align-middle mb-0">
                <thead class="table-light">
                <tr>
                    <th>Курс</th>
                    <th>Преподаватель</th>
                    <th>Начало</th>
                    <th>Окончание</th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (schedule != null && !schedule.isEmpty()) {
                        for (Schedule item : schedule) {
                %>
                <tr>
                    <td><%= scheduleCourseTitles != null ? scheduleCourseTitles.get(item.getId()) : "" %></td>
                    <td><%= scheduleTeacherNames != null ? scheduleTeacherNames.get(item.getId()) : "" %></td>
                    <td><%= item.getStartAt().format(formatter) %></td>
                    <td><%= item.getEndAt().format(formatter) %></td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="4" class="text-center text-muted py-4">Занятий за выбранный период нет</td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>

</div>

</body>
</html>

