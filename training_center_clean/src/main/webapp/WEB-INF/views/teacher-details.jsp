<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="training_center.entity.Course" %>
<%@ page import="training_center.entity.Teacher" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Карточка преподавателя</title>
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
        List<Course> courses = (List<Course>) request.getAttribute("courses");
        String companyName = (String) request.getAttribute("companyName");
        String errorMessage = (String) request.getAttribute("errorMessage");
    %>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="mb-1">Карточка преподавателя</h1>
            <p class="text-muted mb-0"><%= teacher.getFullName() %></p>
        </div>
        <div class="d-flex gap-2 flex-wrap justify-content-end">
            <a href="${pageContext.request.contextPath}/teachers" class="btn btn-outline-secondary">К списку</a>
            <a href="${pageContext.request.contextPath}/teachers/<%= teacher.getId() %>/schedule" class="btn btn-outline-primary">Расписание</a>
            <a href="${pageContext.request.contextPath}/teachers/<%= teacher.getId() %>/edit" class="btn btn-outline-primary">Редактировать</a>
            <form method="post" action="${pageContext.request.contextPath}/teachers/<%= teacher.getId() %>/delete" class="d-inline">
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
                        <div class="fw-semibold"><%= teacher.getId() %></div>
                    </div>
                </div>
                <div class="col-md-5">
                    <div class="border rounded-3 p-3 bg-white">
                        <div class="text-muted small">ФИО</div>
                        <div class="fw-semibold"><%= teacher.getFullName() %></div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="border rounded-3 p-3 bg-white">
                        <div class="text-muted small">Компания</div>
                        <div class="fw-semibold"><%= companyName %></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="card shadow-sm border-0 rounded-4">
        <div class="card-body p-4">
            <h4 class="mb-3">Курсы преподавателя</h4>
            <%
                if (courses != null && !courses.isEmpty()) {
            %>
            <ul class="list-group">
                <%
                    for (Course course : courses) {
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
            <p class="text-muted mb-0">Курсы пока не назначены</p>
            <%
                }
            %>
        </div>
    </div>

</div>

</body>
</html>

