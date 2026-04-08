<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="training_center.entity.Student" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Обучающиеся</title>
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
        @SuppressWarnings("unchecked")
        List<Student> students = (List<Student>) request.getAttribute("students");
        String fullNameFilter = (String) request.getAttribute("fullNameFilter");
        if (fullNameFilter == null) {
            fullNameFilter = "";
        }
    %>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="mb-1">Обучающиеся</h1>
            <p class="text-muted mb-0">Список всех обучающихся</p>
        </div>
        <a href="${pageContext.request.contextPath}/students/new" class="btn btn-primary">Добавить обучающегося</a>
    </div>

    <div class="card shadow-sm border-0 rounded-4 mb-4">
        <div class="card-body">
            <form method="get" action="${pageContext.request.contextPath}/students" class="row g-3 align-items-end">
                <div class="col-md-8">
                    <label class="form-label">Поиск по ФИО</label>
                    <input type="text" name="fullName" class="form-control" value="<%= fullNameFilter %>" placeholder="Введите ФИО">
                </div>
                <div class="col-md-4 d-flex gap-2">
                    <button type="submit" class="btn btn-outline-primary">Найти</button>
                    <a href="${pageContext.request.contextPath}/students" class="btn btn-outline-secondary">Сбросить</a>
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
                    <th>ФИО</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (students != null && !students.isEmpty()) {
                        for (Student student : students) {
                %>
                <tr>
                    <td><%= student.getId() %></td>
                    <td><%= student.getFullName() %></td>
                    <td class="text-end">
                        <a href="${pageContext.request.contextPath}/students/<%= student.getId() %>" class="btn btn-sm btn-outline-primary">Открыть</a>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="3" class="text-center text-muted py-4">Обучающиеся не найдены</td>
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

