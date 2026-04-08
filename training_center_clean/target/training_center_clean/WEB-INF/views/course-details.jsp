<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="training_center.entity.Course" %>
<%@ page import="training_center.entity.Schedule" %>
<%@ page import="training_center.entity.Student" %>
<%@ page import="training_center.entity.Teacher" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Карточка курса</title>
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
        List<Student> students = (List<Student>) request.getAttribute("students");
        @SuppressWarnings("unchecked")
        List<Teacher> teachers = (List<Teacher>) request.getAttribute("teachers");
        @SuppressWarnings("unchecked")
        List<Schedule> schedule = (List<Schedule>) request.getAttribute("schedule");
        @SuppressWarnings("unchecked")
        Map<Long, String> scheduleTeacherNames = (Map<Long, String>) request.getAttribute("scheduleTeacherNames");

        String companyName = (String) request.getAttribute("companyName");
        String errorMessage = (String) request.getAttribute("errorMessage");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String durationUnit = course.getDurationUnit();
        String durationUnitLabel = "";
        if ("day".equals(durationUnit)) {
            durationUnitLabel = "день";
        } else if ("days".equals(durationUnit)) {
            durationUnitLabel = "дней";
        } else if ("week".equals(durationUnit)) {
            durationUnitLabel = "неделя";
        } else if ("weeks".equals(durationUnit)) {
            durationUnitLabel = "недель";
        } else if ("month".equals(durationUnit)) {
            durationUnitLabel = "месяц";
        } else if ("months".equals(durationUnit)) {
            durationUnitLabel = "месяцев";
        } else {
            durationUnitLabel = durationUnit;
        }
    %>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="mb-1">Карточка курса</h1>
            <p class="text-muted mb-0">Подробная информация о курсе</p>
        </div>
        <div class="d-flex gap-2 flex-wrap justify-content-end">
            <a href="${pageContext.request.contextPath}/courses" class="btn btn-outline-secondary">К списку</a>
            <a href="${pageContext.request.contextPath}/courses/<%= course.getId() %>/edit" class="btn btn-outline-primary">Редактировать</a>
            <a href="${pageContext.request.contextPath}/courses/<%= course.getId() %>/add-student" class="btn btn-outline-success">Добавить обучающегося</a>
            <a href="${pageContext.request.contextPath}/courses/<%= course.getId() %>/add-teacher" class="btn btn-outline-success">Назначить преподавателя</a>
            <a href="${pageContext.request.contextPath}/courses/<%= course.getId() %>/schedule/new" class="btn btn-primary">Добавить занятие</a>
            <form method="post" action="${pageContext.request.contextPath}/courses/<%= course.getId() %>/delete" class="d-inline">
                <button type="submit" class="btn btn-danger">Удалить курс</button>
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

    <div class="card shadow-sm border-0 rounded-4 mb-4">
        <div class="card-body p-4">
            <h3 class="mb-4"><%= course.getTitle() %></h3>
            <div class="row g-3">
                <div class="col-md-3">
                    <div class="border rounded-3 p-3 bg-white">
                        <div class="text-muted small">ID</div>
                        <div class="fw-semibold"><%= course.getId() %></div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="border rounded-3 p-3 bg-white">
                        <div class="text-muted small">Компания</div>
                        <div class="fw-semibold"><%= companyName %></div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="border rounded-3 p-3 bg-white">
                        <div class="text-muted small">Длительность</div>
                        <div class="fw-semibold"><%= course.getDurationValue() %> <%= durationUnitLabel %></div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="border rounded-3 p-3 bg-white">
                        <div class="text-muted small">Интенсивность</div>
                        <div class="fw-semibold"><%= course.getIntensity() %> занятий в неделю</div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="card shadow-sm border-0 rounded-4 mb-4">
        <div class="card-body p-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h4 class="mb-0">Обучающиеся</h4>
                <a href="${pageContext.request.contextPath}/courses/<%= course.getId() %>/add-student" class="btn btn-sm btn-outline-success">Добавить</a>
            </div>
            <%
                if (students != null && !students.isEmpty()) {
            %>
            <ul class="list-group">
                <%
                    for (Student student : students) {
                %>
                <li class="list-group-item d-flex justify-content-between align-items-center">
                    <a href="${pageContext.request.contextPath}/students/<%= student.getId() %>" class="text-decoration-none">
                        <%= student.getFullName() %>
                    </a>
                    <form method="post" action="${pageContext.request.contextPath}/courses/<%= course.getId() %>/students/<%= student.getId() %>/delete" class="d-inline">
                        <button type="submit" class="btn btn-sm btn-outline-danger">Убрать</button>
                    </form>
                </li>
                <%
                    }
                %>
            </ul>
            <%
                } else {
            %>
            <p class="text-muted mb-0">Пока нет записанных обучающихся</p>
            <%
                }
            %>
        </div>
    </div>

    <div class="card shadow-sm border-0 rounded-4 mb-4">
        <div class="card-body p-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h4 class="mb-0">Преподаватели</h4>
                <a href="${pageContext.request.contextPath}/courses/<%= course.getId() %>/add-teacher" class="btn btn-sm btn-outline-success">Назначить</a>
            </div>
            <%
                if (teachers != null && !teachers.isEmpty()) {
            %>
            <ul class="list-group">
                <%
                    for (Teacher teacher : teachers) {
                %>
                <li class="list-group-item d-flex justify-content-between align-items-center">
                    <a href="${pageContext.request.contextPath}/teachers/<%= teacher.getId() %>" class="text-decoration-none">
                        <%= teacher.getFullName() %>
                    </a>
                    <form method="post" action="${pageContext.request.contextPath}/courses/<%= course.getId() %>/teachers/<%= teacher.getId() %>/delete" class="d-inline">
                        <button type="submit" class="btn btn-sm btn-outline-danger">Убрать</button>
                    </form>
                </li>
                <%
                    }
                %>
            </ul>
            <%
                } else {
            %>
            <p class="text-muted mb-0">Преподаватели пока не назначены</p>
            <%
                }
            %>
        </div>
    </div>

    <div class="card shadow-sm border-0 rounded-4">
        <div class="card-body p-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h4 class="mb-0">Расписание курса</h4>
                <a href="${pageContext.request.contextPath}/courses/<%= course.getId() %>/schedule/new" class="btn btn-sm btn-primary">Добавить занятие</a>
            </div>
            <%
                if (schedule != null && !schedule.isEmpty()) {
            %>
            <table class="table table-striped align-middle mb-0">
                <thead>
                <tr>
                    <th>Начало</th>
                    <th>Окончание</th>
                    <th>Преподаватель</th>
                </tr>
                </thead>
                <tbody>
                <%
                    for (Schedule item : schedule) {
                %>
                <tr>
                    <td><%= item.getStartAt().format(formatter) %></td>
                    <td><%= item.getEndAt().format(formatter) %></td>
                    <td><%= scheduleTeacherNames != null ? scheduleTeacherNames.get(item.getId()) : "" %></td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
            <%
                } else {
            %>
            <p class="text-muted mb-0">Занятий пока нет</p>
            <%
                }
            %>
        </div>
    </div>

</div>

</body>
</html>

