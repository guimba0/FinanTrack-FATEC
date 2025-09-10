<%@ page pageEncoding="UTF-8" %>
<%
    if (session.getAttribute("userName") == null) {
        response.sendRedirect("tela_login.jsp");
        return;
    }
    String userName = (String) session.getAttribute("userName");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <div class="card">
            <div class="card-body">
                <h1 class="card-title">Login bem-sucedido!</h1>
                <p>Boas-vindas, <strong><%= userName %></strong>!</p>
                <hr>
                <a href="logout" class="btn btn-danger">Sair</a>
            </div>
        </div>
    </div>
</body>
</html>