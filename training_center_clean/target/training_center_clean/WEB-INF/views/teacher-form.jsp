<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="training_center.entity.Company" %>
<%@ page import="training_center.entity.Teacher" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Форма преподавателя</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/site-background.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg bg-white border-bottom shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">Учебный центр</a>
        <div class="navbar-nav">
            <a class="nav-link" href="${pageContext.request.contextPath}/companies">Организации</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/courses">Курсы</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/teachers">Преподаватели</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/students">Обучающиеся</a>
        </div>
    </div>
</nav>

<div class="container py-4">
    <%
        Teacher teacher = (Teacher) request.getAttribute("teacher");

        @SuppressWarnings("unchecked")
        List<Company> companies = (List<Company>) request.getAttribute("companies");

        String formTitle = (String) request.getAttribute("formTitle");
        String formAction = (String) request.getAttribute("formAction");
        String submitLabel = (String) request.getAttribute("submitLabel");
        String cancelUrl = (String) request.getAttribute("cancelUrl");
        String errorMessage = (String) request.getAttribute("errorMessage");
        Long selectedCompanyId = (Long) request.getAttribute("selectedCompanyId");

        String fullName = teacher != null && teacher.getFullName() != null ? teacher.getFullName() : "";
    %>

    <div class="mb-4">
        <h1 class="mb-1"><%= formTitle %></h1>
        <p class="text-muted mb-0">Заполните данные преподавателя</p>
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
            <form method="post" action="${pageContext.request.contextPath}<%= formAction %>">
                <div class="mb-3">
                    <label class="form-label">ФИО</label>
                    <input type="text" name="fullName" class="form-control" value="<%= fullName %>" required>
                </div>
                <div class="mb-4">
                    <label class="form-label">Компания</label>
                    <select name="companyId" class="form-select" required>
                        <%
                            if (companies != null) {
                                for (Company company : companies) {
                                    boolean selected = selectedCompanyId != null && selectedCompanyId.equals(company.getId());
                        %>
                        <option value="<%= company.getId() %>" <%= selected ? "selected" : "" %>><%= company.getName() %></option>
                        <%
                                }
                            }
                        %>
                    </select>
                    <div class="mt-2 d-flex gap-2">
                        <a href="${pageContext.request.contextPath}/companies/new" class="btn btn-sm btn-outline-secondary">Добавить организацию</a>
                        <a href="${pageContext.request.contextPath}/companies" class="btn btn-sm btn-outline-secondary">Список организаций</a>
                    </div>
                </div>
                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary"><%= submitLabel %></button>
                    <a href="${pageContext.request.contextPath}<%= cancelUrl %>" class="btn btn-outline-secondary">Отмена</a>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>
