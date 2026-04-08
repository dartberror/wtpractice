<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="training_center.entity.Schedule" %>
<%@ page import="training_center.entity.Teacher" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Расписание преподавателя</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/site-background.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg bg-white border-bottom shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">Учебный центр</a>
        <div class="navbar-nav">
            <a class="nav-link" href="${pageContext.request.contextPath}/courses">Курсы</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/teachers">Преподаватели</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/students">Обучающиеся</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/companies">Организации</a>
        </div>
    </div>
</nav>

<div class="container py-4">
    <%
        Teacher teacher = (Teacher) request.getAttribute("teacher");
        @SuppressWarnings("unchecked")
        List<Schedule> schedule = (List<Schedule>) request.getAttribute("schedule");
        String from = (String) request.getAttribute("from");
        String to = (String) request.getAttribute("to");
        String errorMessage = (String) request.getAttribute("errorMessage");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    %>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="mb-1">Расписание преподавателя</h1>
            <p class="text-muted mb-0"><%= teacher.getFullName() %></p>
        </div>
        <a href="${pageContext.request.contextPath}/teachers/<%= teacher.getId() %>" class="btn btn-outline-secondary">Назад</a>
    </div>

    <div class="card shadow-sm border-0 rounded-4 mb-4">
        <div class="card-body">
            <form method="get" action="${pageContext.request.contextPath}/teachers/<%= teacher.getId() %>/schedule" class="row g-3 align-items-end">
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
                    <a href="${pageContext.request.contextPath}/teachers/<%= teacher.getId() %>/schedule" class="btn btn-outline-secondary">Сбросить</a>
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
                    <td><%= item.getCourse() != null ? item.getCourse().getTitle() : "" %></td>
                    <td><%= item.getStartAt().format(formatter) %></td>
                    <td><%= item.getEndAt().format(formatter) %></td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="3" class="text-center text-muted py-4">Занятий за выбранный период нет</td>
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

