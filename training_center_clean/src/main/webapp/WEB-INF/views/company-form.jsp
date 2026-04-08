<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="training_center.entity.Company" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Добавить организацию</title>
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
            <a class="nav-link" href="${pageContext.request.contextPath}/students">Обучающиеся</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/companies">Организации</a>
        </div>
    </div>
</nav>

<div class="container py-4">
    <%
        Company company = (Company) request.getAttribute("company");
        if (company == null) {
            company = new Company();
        }
        String nameValue = company.getName() == null ? "" : company.getName();
        String addressValue = company.getAddress() == null ? "" : company.getAddress();
        String errorMessage = (String) request.getAttribute("errorMessage");
    %>

    <div class="mb-4">
        <h1 class="mb-1">Добавить организацию</h1>
        <p class="text-muted mb-0">После добавления организация появится в списках выбора в курсах и преподавателях</p>
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
            <form method="post" action="${pageContext.request.contextPath}/companies">
                <div class="mb-3">
                    <label class="form-label">Название</label>
                    <input type="text" name="name" class="form-control" value="<%= nameValue %>" required>
                </div>

                <div class="mb-4">
                    <label class="form-label">Адрес</label>
                    <input type="text" name="address" class="form-control" value="<%= addressValue %>" required>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary">Сохранить</button>
                    <a href="${pageContext.request.contextPath}/companies" class="btn btn-outline-secondary">Отмена</a>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>
