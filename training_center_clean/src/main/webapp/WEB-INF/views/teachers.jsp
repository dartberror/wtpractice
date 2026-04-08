<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="training_center.entity.Course" %>
<%@ page import="training_center.entity.Teacher" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Преподаватели</title>
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
        @SuppressWarnings("unchecked")
        List<Teacher> teachers = (List<Teacher>) request.getAttribute("teachers");
        @SuppressWarnings("unchecked")
        Map<Long, String> teacherCourses = (Map<Long, String>) request.getAttribute("teacherCourses");
        @SuppressWarnings("unchecked")
        Map<Long, String> teacherCompanyNames = (Map<Long, String>) request.getAttribute("teacherCompanyNames");
        @SuppressWarnings("unchecked")
        List<Course> courses = (List<Course>) request.getAttribute("courses");
        Long selectedCourseId = (Long) request.getAttribute("selectedCourseId");
    %>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="mb-1">Преподаватели</h1>
            <p class="text-muted mb-0">Список преподавателей учебного центра</p>
        </div>
        <a href="${pageContext.request.contextPath}/teachers/new" class="btn btn-primary">Добавить преподавателя</a>
    </div>

    <div class="card shadow-sm border-0 rounded-4 mb-4">
        <div class="card-body">
            <form method="get" action="${pageContext.request.contextPath}/teachers" class="row g-3 align-items-end">
                <div class="col-md-8">
                    <label class="form-label">Фильтр по курсу</label>
                    <select name="courseId" class="form-select">
                        <option value="">Все курсы</option>
                        <%
                            if (courses != null) {
                                for (Course course : courses) {
                                    boolean selected = selectedCourseId != null && selectedCourseId.equals(course.getId());
                        %>
                        <option value="<%= course.getId() %>" <%= selected ? "selected" : "" %>><%= course.getTitle() %></option>
                        <%
                                }
                            }
                        %>
                    </select>
                </div>
                <div class="col-md-4 d-flex gap-2">
                    <button type="submit" class="btn btn-outline-primary">Показать</button>
                    <a href="${pageContext.request.contextPath}/teachers" class="btn btn-outline-secondary">Сбросить</a>
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
                    <th>Компания</th>
                    <th>Курсы</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (teachers != null && !teachers.isEmpty()) {
                        for (Teacher teacher : teachers) {
                %>
                <tr>
                    <td><%= teacher.getId() %></td>
                    <td><%= teacher.getFullName() %></td>
                    <td><%= teacherCompanyNames != null ? teacherCompanyNames.get(teacher.getId()) : "" %></td>
                    <td><%= teacherCourses != null ? teacherCourses.get(teacher.getId()) : "" %></td>
                    <td class="text-end">
                        <a href="${pageContext.request.contextPath}/teachers/<%= teacher.getId() %>" class="btn btn-sm btn-outline-primary">Открыть</a>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="5" class="text-center text-muted py-4">Преподаватели не найдены</td>
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

