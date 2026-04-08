<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="training_center.entity.Course" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Курсы</title>
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
        @SuppressWarnings("unchecked")
        List<Course> courses = (List<Course>) request.getAttribute("courses");
        @SuppressWarnings("unchecked")
        Map<Long, String> companyNames = (Map<Long, String>) request.getAttribute("companyNames");
        String titleFilter = (String) request.getAttribute("titleFilter");
        if (titleFilter == null) {
            titleFilter = "";
        }
    %>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="mb-1">Курсы</h1>
            <p class="text-muted mb-0">Список всех курсов учебного центра</p>
        </div>
        <a href="${pageContext.request.contextPath}/courses/new" class="btn btn-primary">Добавить курс</a>
    </div>

    <div class="card shadow-sm border-0 rounded-4 mb-4">
        <div class="card-body">
            <form method="get" action="${pageContext.request.contextPath}/courses" class="row g-3 align-items-end">
                <div class="col-md-8">
                    <label class="form-label">Поиск по названию</label>
                    <input type="text" name="title" class="form-control" value="<%= titleFilter %>" placeholder="Введите название курса">
                </div>
                <div class="col-md-4 d-flex gap-2">
                    <button type="submit" class="btn btn-outline-primary">Найти</button>
                    <a href="${pageContext.request.contextPath}/courses" class="btn btn-outline-secondary">Сбросить</a>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow-sm border-0 rounded-4">
        <div class="card-body p-0">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                <tr>
                    <th>ID</th>
                    <th>Название</th>
                    <th>Компания</th>
                    <th>Длительность</th>
                    <th>Интенсивность</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (courses != null && !courses.isEmpty()) {
                        for (Course course : courses) {
                            String unit = course.getDurationUnit();
                            String unitLabel = "";
                            if ("day".equals(unit)) {
                                unitLabel = "день";
                            } else if ("days".equals(unit)) {
                                unitLabel = "дней";
                            } else if ("week".equals(unit)) {
                                unitLabel = "неделя";
                            } else if ("weeks".equals(unit)) {
                                unitLabel = "недель";
                            } else if ("month".equals(unit)) {
                                unitLabel = "месяц";
                            } else if ("months".equals(unit)) {
                                unitLabel = "месяцев";
                            } else {
                                unitLabel = unit;
                            }
                %>
                <tr>
                    <td><%= course.getId() %></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/courses/<%= course.getId() %>" class="text-decoration-none">
                            <%= course.getTitle() %>
                        </a>
                    </td>
                    <td><%= companyNames != null ? companyNames.get(course.getId()) : "" %></td>
                    <td><%= course.getDurationValue() %> <%= unitLabel %></td>
                    <td><%= course.getIntensity() %> зан./нед.</td>
                    <td class="text-end">
                        <a href="${pageContext.request.contextPath}/courses/<%= course.getId() %>" class="btn btn-sm btn-outline-primary">Открыть</a>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="6" class="text-center text-muted py-4">Курсы не найдены</td>
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

