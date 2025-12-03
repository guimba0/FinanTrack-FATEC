<%-- 
    Página: Meus Grupos
    Objetivo: Listar grupos que o usuário participa e gerenciar convites pendentes.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="br.com.finantrack.controller.*, java.util.List"%>
<%
    Usuario u = (Usuario) session.getAttribute("usuario");
    if (u == null) { response.sendRedirect("../tela_login.jsp"); return; }
    
    // Carrega grupos e convites do banco
    GrupoDAO dao = new GrupoDAO();
    List<Grupo> meusGrupos = dao.listarMeusGrupos(u.getId());
    List<Grupo> convites = dao.listarConvitesPendentes(u.getId());
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"><title>Grupos - FinanTrack</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body>
    <jsp:include page="../navbar/cabecalho.jsp" />

    <div class="container mt-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-light text-secondary">Meus Grupos</h2>
            <button class="btn btn-primary shadow-sm" data-bs-toggle="modal" data-bs-target="#modalNovoGrupo">
                <i class="bi bi-plus-lg"></i> Criar Grupo
            </button>
        </div>

        <%-- Seção de Convites Pendentes --%>
        <% if(!convites.isEmpty()) { %>
        <div class="card border-warning mb-4 shadow-sm">
            <div class="card-header bg-warning text-dark fw-bold"><i class="bi bi-envelope-exclamation"></i> Convites Pendentes</div>
            <div class="card-body">
                <% for(Grupo g : convites) { %>
                <div class="d-flex justify-content-between align-items-center border-bottom pb-2 mb-2">
                    <span>Você foi convidado para: <strong><%= g.getNome() %></strong></span>
                    <div>
                        <form action="${pageContext.request.contextPath}/GrupoServlet" method="POST" class="d-inline">
                            <input type="hidden" name="action" value="responder">
                            <input type="hidden" name="grupoId" value="<%= g.getId() %>">
                            <button name="resposta" value="ACEITO" class="btn btn-sm btn-success me-1">Aceitar</button>
                            <button name="resposta" value="RECUSADO" class="btn btn-sm btn-outline-danger">Recusar</button>
                        </form>
                    </div>
                </div>
                <% } %>
            </div>
        </div>
        <% } %>

        <%-- Lista de Grupos Ativos --%>
        <div class="row g-3">
            <% if(meusGrupos.isEmpty()) { %>
                <div class="col-12 text-center py-5 text-muted">
                    <i class="bi bi-people display-1"></i><br>
                    <p class="mt-3">Você não participa de nenhum grupo financeiro ainda.</p>
                </div>
            <% } %>
            
            <% for(Grupo g : meusGrupos) { %>
            <div class="col-md-4">
                <div class="card h-100 shadow-sm border-0">
                    <div class="card-body text-center">
                        <div class="display-4 text-primary mb-3"><i class="bi bi-house-door"></i></div>
                        <h4 class="card-title fw-bold"><%= g.getNome() %></h4>
                        <p class="text-muted small">Finanças Compartilhadas</p>
                        <a href="visualizar_grupo.jsp?id=<%= g.getId() %>" class="btn btn-outline-primary w-100 stretched-link">Acessar Planilha</a>
                    </div>
                </div>
            </div>
            <% } %>
        </div>
    </div>

    <%-- Modal Criar Grupo --%>
    <div class="modal fade" id="modalNovoGrupo" tabindex="-1"><div class="modal-dialog modal-dialog-centered"><div class="modal-content">
        <form action="${pageContext.request.contextPath}/GrupoServlet" method="POST">
            <input type="hidden" name="action" value="criar">
            <div class="modal-header bg-primary text-white"><h5 class="modal-title">Novo Grupo</h5><button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">
                <label>Nome do Grupo (Ex: Casa, Viagem)</label>
                <input type="text" name="nome" class="form-control mt-2" required>
            </div>
            <div class="modal-footer"><button type="submit" class="btn btn-primary w-100">Criar</button></div>
        </form>
    </div></div></div>

    <jsp:include page="../navbar/rodape.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>