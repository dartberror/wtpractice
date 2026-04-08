<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Учебный центр</title>
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
            <a class="nav-link" href="${pageContext.request.contextPath}/companies">Организации</a>
        </div>
    </div>
</nav>

<div class="container py-5">
    <div class="p-5 bg-white rounded-4 shadow-sm">
        <h1 class="display-5 fw-bold mb-3">Учебный центр</h1>
        <p class="fs-5 text-muted mb-4">
            Главная страница приложения. Здесь можно перейти к спискам курсов, преподавателей, обучающихся и организаций.
        </p>
        <div class="d-flex gap-3 flex-wrap">
            <a href="${pageContext.request.contextPath}/courses" class="btn btn-primary btn-lg">Курсы</a>
            <a href="${pageContext.request.contextPath}/teachers" class="btn btn-success btn-lg">Преподаватели</a>
            <a href="${pageContext.request.contextPath}/students" class="btn btn-warning btn-lg">Обучающиеся</a>
            <a href="${pageContext.request.contextPath}/companies" class="btn btn-info btn-lg">Организации</a>
        </div>
    </div>
</div>

</body>
</html>

