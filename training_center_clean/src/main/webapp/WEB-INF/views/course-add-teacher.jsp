<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="training_center.entity.Course" %>
<%@ page import="training_center.entity.Teacher" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Назначить преподавателя</title>
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
        @SuppressWarnings("unchecked")
        Map<Long, String> teacherCompanyNames = (Map<Long, String>) request.getAttribute("teacherCompanyNames");
        Long selectedTeacherId = (Long) request.getAttribute("selectedTeacherId");
        String errorMessage = (String) request.getAttribute("errorMessage");
    %>

    <div class="mb-4">
        <h1 class="mb-1">Назначить преподавателя</h1>
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
            <form method="post" action="${pageContext.request.contextPath}/courses/<%= course.getId() %>/teachers">
                <div class="mb-4">
                    <label class="form-label">Преподаватель</label>
                    <select name="teacherId" class="form-select" required>
                        <%
                            if (teachers != null) {
                                for (Teacher teacher : teachers) {
                                    boolean selected = selectedTeacherId != null && selectedTeacherId.equals(teacher.getId());
                                    String companyName = teacherCompanyNames != null ? teacherCompanyNames.get(teacher.getId()) : "";
                        %>
                        <option value="<%= teacher.getId() %>" <%= selected ? "selected" : "" %>><%= teacher.getFullName() %><%= companyName != null && !companyName.isBlank() ? " (" + companyName + ")" : "" %></option>
                        <%
                                }
                            }
                        %>
                    </select>
                </div>
                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary">Назначить</button>
                    <a href="${pageContext.request.contextPath}/courses/<%= course.getId() %>" class="btn btn-outline-secondary">Отмена</a>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>

