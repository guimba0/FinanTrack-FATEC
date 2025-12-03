<%-- 
    Página: Dashboard (Home)
    Objetivo: Menu principal após o login.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Proteção de Rota: Redireciona se não estiver logado --%>
<%
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect("../tela_login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Início - FinanTrack</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    
    <style>
        body { 
            background-color: #f8f9fa;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        .main-content {
            flex: 1;
            display: flex;
            align-items: center; 
        }

        .menu-card { 
            width: 100%;
            text-align: center; 
            border: none; 
            border-radius: 12px; 
            box-shadow: 0 4px 6px rgba(0,0,0,0.05);
            transition: all 0.3s ease; 
            cursor: pointer; 
            background: white;
            padding: 2rem;
            height: 100%;
        }
        
        .menu-card:hover { 
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.1);
        }
        
        .icon-box { 
            font-size: 3rem;
            margin-bottom: 15px; 
        }

        /* Estilização dos Cards */
        .card-lancamentos { border-bottom: 4px solid #0d6efd; }
        .card-lancamentos .icon-box, .card-lancamentos h4 { color: #0d6efd; }

        .card-grupos { border-bottom: 4px solid #6c757d; }
        .card-grupos .icon-box, .card-grupos h4 { color: #6c757d; }
    </style>
</head>
<body>
    
    <jsp:include page="../navbar/cabecalho.jsp" />

    <%-- Mensagens de Sucesso/Erro vindas dos Controllers unificados --%>
    <% String formSuccess = (String) session.getAttribute("formSuccess");
       if(formSuccess != null) { session.removeAttribute("formSuccess"); %>
       <div class="alert alert-success text-center m-0 rounded-0"><%= formSuccess %></div>
    <% } %>
    <% String formError = (String) session.getAttribute("formError");
       if(formError != null) { session.removeAttribute("formError"); %>
       <div class="alert alert-danger text-center m-0 rounded-0"><%= formError %></div>
    <% } %>

    <div class="container main-content">
        <div class="w-100 pb-5"> 
            
            <div class="text-center mb-5">
                <h2 class="fw-light text-secondary">Olá, <c:out value="${sessionScope.usuario.nome}" default="Usuário"/></h2>
            </div>

            <div class="row justify-content-center g-4">
                
                <%-- Atalho: Módulo Pessoal --%>
                <div class="col-md-5 col-lg-4">
                    <a href="${pageContext.request.contextPath}/dashboard/detalhes_mes.jsp" class="text-decoration-none text-dark">
                        <div class="card menu-card card-lancamentos">
                            <div class="icon-box"><i class="bi bi-wallet2"></i></div>
                            <h4 class="fw-bold">Meus Lançamentos</h4>
                            <p class="text-muted small mb-0">Gerencie suas receitas, despesas e assinaturas mensais.</p>
                        </div>
                    </a>
                </div>

                <%-- Atalho: Módulo Grupos --%>
                <div class="col-md-5 col-lg-4">
                    <a href="${pageContext.request.contextPath}/dashboard/meus_grupos.jsp" class="text-decoration-none text-dark">
                        <div class="card menu-card card-grupos">
                            <div class="icon-box"><i class="bi bi-people-fill"></i></div>
                            <h4 class="fw-bold">Família & Grupos</h4>
                            <p class="text-muted small mb-0">Visualize e participe de finanças compartilhadas.</p>
                        </div>
                    </a>
                </div>
                
            </div>
        </div>
    </div>

    <jsp:include page="../navbar/rodape.jsp" />

</body>
</html>