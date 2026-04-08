<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="training_center.entity.Course" %>
<%@ page import="training_center.entity.Student" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Карточка обучающегося</title>
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
        List<Course> currentCourses = (List<Course>) request.getAttribute("currentCourses");
        @SuppressWarnings("unchecked")
        List<Course> courseHistory = (List<Course>) request.getAttribute("courseHistory");
        String errorMessage = (String) request.getAttribute("errorMessage");
    %>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="mb-1">Карточка обучающегося</h1>
            <p class="text-muted mb-0"><%= student.getFullName() %></p>
        </div>
        <div class="d-flex gap-2 flex-wrap justify-content-end">
            <a href="${pageContext.request.contextPath}/students" class="btn btn-outline-secondary">К списку</a>
            <a href="${pageContext.request.contextPath}/students/<%= student.getId() %>/schedule" class="btn btn-outline-primary">Расписание</a>
            <a href="${pageContext.request.contextPath}/students/<%= student.getId() %>/edit" class="btn btn-outline-primary">Редактировать</a>
            <form method="post" action="${pageContext.request.contextPath}/students/<%= student.getId() %>/delete" class="d-inline">
                <button type="submit" class="btn btn-danger">Удалить</button>
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
            <div class="row g-3">
                <div class="col-md-3">
                    <div class="border rounded-3 p-3 bg-white">
                        <div class="text-muted small">ID</div>
                        <div class="fw-semibold"><%= student.getId() %></div>
                    </div>
                </div>
                <div class="col-md-9">
                    <div class="border rounded-3 p-3 bg-white">
                        <div class="text-muted small">ФИО</div>
                        <div class="fw-semibold"><%= student.getFullName() %></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="card shadow-sm border-0 rounded-4 mb-4">
        <div class="card-body p-4">
            <h4 class="mb-3">Текущие курсы</h4>
            <%
                if (currentCourses != null && !currentCourses.isEmpty()) {
            %>
            <ul class="list-group">
                <%
                    for (Course course : currentCourses) {
                %>
                <li class="list-group-item">
                    <a href="${pageContext.request.contextPath}/courses/<%= course.getId() %>" class="text-decoration-none"><%= course.getTitle() %></a>
                </li>
                <%
                    }
                %>
            </ul>
            <%
                } else {
            %>
            <p class="text-muted mb-0">Сейчас активных курсов нет</p>
            <%
                }
            %>
        </div>
    </div>

    <div class="card shadow-sm border-0 rounded-4">
        <div class="card-body p-4">
            <h4 class="mb-3">История обучения</h4>
            <%
                if (courseHistory != null && !courseHistory.isEmpty()) {
            %>
            <ul class="list-group">
                <%
                    for (Course course : courseHistory) {
                %>
                <li class="list-group-item">
                    <a href="${pageContext.request.contextPath}/courses/<%= course.getId() %>" class="text-decoration-none"><%= course.getTitle() %></a>
                </li>
                <%
                    }
                %>
            </ul>
            <%
                } else {
            %>
            <p class="text-muted mb-0">История обучения пока пуста</p>
            <%
                }
            %>
        </div>
    </div>

</div>

</body>
</html>

