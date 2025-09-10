<%-- Conteúdo para o arquivo: navbar/cabecalho.jsp --%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <%-- O head continua o mesmo, com meta tags, title, links e style --%>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.titulo != null ? param.titulo : "FinanTrack"}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        :root{--cor-fundo-pagina:#010120;--cor-fundo-navbar:#0a0a2e;--cor-texto-claro:#C9D1D9;--cor-azul-principal:#0606c5;--cor-azul-hover:#000057;}body{background-color:var(--cor-fundo-pagina);color:var(--cor-texto-claro);}.navbar-custom{background-color:var(--cor-fundo-navbar);border-bottom:1px solid var(--cor-azul-principal);}.navbar-custom .navbar-brand,.navbar-custom .nav-link{color:var(--cor-texto-claro);}.navbar-custom .nav-link:hover{color:var(--cor-azul-hover);}.navbar-custom .navbar-brand i,.navbar-custom .nav-link i{color:var(--cor-azul-principal);}.navbar-custom .nav-link:hover i{color:var(--cor-azul-hover);}.btn-custom{background-color:var(--cor-azul-principal);color:var(--cor-texto-claro);border-color:var(--cor-azul-principal);font-weight:bold;}.btn-custom:hover{background-color:var(--cor-azul-hover);color:var(--cor-texto-claro);border-color:var(--cor-azul-hover);}.welcome-box{padding-top:15vh;}.card-hover{transition:transform .2s ease-in-out,border-color .2s ease-in-out;}.card-hover:hover{transform:scale(1.05);border-color:var(--cor-azul-principal) !important;}
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark navbar-custom">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">
                <i class="bi bi-bar-chart-line-fill"></i>
                FinanTrack
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <% if (session.getAttribute("usuarioLogado") != null) { %>
                        <li class="nav-item">
                            <%-- *** CORREÇÃO AQUI: Apontando para a pasta dashboard *** --%>
                            <a class="nav-link" href="${pageContext.request.contextPath}/dashboard/dashboard.jsp"><i class="bi bi-person-circle"></i> Minha Conta</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/LogoutServlet"><i class="bi bi-box-arrow-right"></i> Sair</a>
                        </li>
                    <% } else { %>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/tela_login.jsp"><i class="bi bi-box-arrow-in-right"></i> Fazer Login</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/tela_cadastro.jsp"><i class="bi bi-person-plus-fill"></i> Fazer Cadastro</a>
                        </li>
                    <% } %>
                </ul>
            </div>
        </div>
    </nav>
    <main class="container mt-4">