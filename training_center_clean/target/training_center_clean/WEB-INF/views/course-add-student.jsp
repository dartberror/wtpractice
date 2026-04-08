<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="training_center.entity.Course" %>
<%@ page import="training_center.entity.Student" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Добавить обучающегося</title>
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
        Long selectedStudentId = (Long) request.getAttribute("selectedStudentId");
        String errorMessage = (String) request.getAttribute("errorMessage");
    %>

    <div class="mb-4">
        <h1 class="mb-1">Добавить обучающегося</h1>
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
            <form method="post" action="${pageContext.request.contextPath}/courses/<%= course.getId() %>/students">
                <div class="mb-4">
                    <label class="form-label">Обучающийся</label>
                    <select name="studentId" class="form-select" required>
                        <%
                            if (students != null) {
                                for (Student student : students) {
                                    boolean selected = selectedStudentId != null && selectedStudentId.equals(student.getId());
                        %>
                        <option value="<%= student.getId() %>" <%= selected ? "selected" : "" %>><%= student.getFullName() %></option>
                        <%
                                }
                            }
                        %>
                    </select>
                </div>
                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary">Добавить</button>
                    <a href="${pageContext.request.contextPath}/courses/<%= course.getId() %>" class="btn btn-outline-secondary">Отмена</a>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>

