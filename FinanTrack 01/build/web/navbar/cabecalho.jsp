<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR" data-bs-theme="dark"> <%-- Adiciona o tema escuro do Bootstrap --%>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.titulo} - FinanTrack</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/estilo.css" rel="stylesheet" type="text/css">

    <%-- ESTILOS GLOBAIS PARA CORRIGIR AS CORES --%>
    <style>
        /* Define a cor padrão do texto do corpo para um branco suave */
        body {
            color: #f8f9fa;
        }

        /* Deixa o texto dos cabeçalhos das tabelas mais nítido */
        .table thead th {
            color: #fff;
            font-weight: 600;
        }
        
        /* Deixa o texto do corpo das tabelas mais claro */
        .table tbody td {
            color: #e0e0e0;
        }

        /* Estilo geral para todos os inputs e selects em tema escuro */
        .form-control, .form-select {
            color: #fff !important;
            background-color: #2a2a2a !important;
            border-color: #555 !important;
        }
        .form-control::placeholder {
            color: #aaa !important;
        }

        /* Deixa os labels (títulos dos campos) mais claros */
        .form-label {
            color: #ced4da;
            font-weight: 500;
        }

        /* Melhora o contraste dos botões de contorno */
        .btn-outline-primary {
            --bs-btn-color: #69b3f7;
            --bs-btn-border-color: #69b3f7;
            --bs-btn-hover-bg: #69b3f7;
            --bs-btn-hover-border-color: #69b3f7;
            --bs-btn-hover-color: #000;
        }
        .btn-outline-danger {
            --bs-btn-color: #ff7b7b;
            --bs-btn-border-color: #ff7b7b;
            --bs-btn-hover-bg: #ff7b7b;
            --bs-btn-hover-border-color: #ff7b7b;
            --bs-btn-hover-color: #000;
        }
    </style>
</head>
<body class="bg-dark">

<nav class="navbar navbar-expand-lg navbar-dark fixed-top" style="background-color: #1c1f23;">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center" href="${pageContext.request.contextPath}/dashboard/dashboard.jsp">
            <i class="bi bi-bar-chart-line-fill fs-4 me-2"></i>
            <strong>FinanTrack</strong>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <% if (session.getAttribute("usuarioLogado") != null) { %>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-person-circle fs-4 me-2"></i>
                            <%= session.getAttribute("usuarioLogado") %>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="navbarDropdown">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/navbar/dados_usuario.jsp"><i class="bi bi-person-fill-gear"></i> Meus Dados</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right"></i> Sair</a></li>
                        </ul>
                    </li>
                <% } else { %>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/tela_login.jsp">Login</a>
                    </li>
                <% } %>
            </ul>
        </div>
    </div>
</nav>

<main class="container" style="padding-top: 80px; padding-bottom: 60px;">