<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="br.com.finantrack.controller.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%
    // 1. Verifica sessão
    Usuario u = (Usuario) session.getAttribute("usuario");
    if (u == null) {
        response.sendRedirect("../tela_login.jsp");
        return;
    }

    // 2. Busca grupos
    int usuarioId = u.getId();
    List<Grupo> meusGrupos = new ArrayList<>();
    String erro = null;

    try {
        GrupoDAO gDao = new GrupoDAO();
        meusGrupos = gDao.listarMeusGrupos(usuarioId);
    } catch (Exception e) {
        erro = "Erro ao carregar grupos: " + e.getMessage();
        e.printStackTrace();
    }
%>

<jsp:include page="../navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="Meus Grupos"/>
</jsp:include>

<style>
    /* Fundo geral escuro */
    body {
        background-color: #343a40 !important;
        color: #f8f9fa;
    }
    
    /* Estilo do Card (Bloco) */
    .card-grupo {
        background-color: #212529; /* Cinza quase preto */
        border: 1px solid #495057;
        transition: transform 0.2s, box-shadow 0.2s;
    }
    
    /* Efeito ao passar o mouse (sobe um pouquinho) */
    .card-grupo:hover {
        transform: translateY(-5px);
        box-shadow: 0 10px 20px rgba(0,0,0,0.3);
        border-color: #0d6efd; /* Borda azul ao focar */
    }

    /* Ícone grande dentro do card */
    .card-icon {
        font-size: 3rem;
        color: #6c757d;
        opacity: 0.3;
    }
</style>

<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold text-primary"><i class="bi bi-grid-fill me-2"></i>Meus Grupos</h2>
            <p class="text-muted">Gerencie suas finanças em conjunto</p>
        </div>
        
        <button class="btn btn-success btn-lg shadow-sm" data-bs-toggle="modal" data-bs-target="#modalCriarGrupo">
            <i class="bi bi-plus-lg me-2"></i>Novo Grupo
        </button>
    </div>

    <% if (erro != null) { %>
        <div class="alert alert-danger"><%= erro %></div>
    <% } %>

    <div class="row g-4">
        
        <% if (meusGrupos.isEmpty()) { %>
            <div class="col-12 text-center py-5">
                <i class="bi bi-people card-icon d-block mb-3"></i>
                <h4 class="text-muted">Você não participa de nenhum grupo.</h4>
                <p class="text-muted mb-4">Crie um novo grupo para começar a compartilhar despesas.</p>
                <button class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#modalCriarGrupo">
                    Criar meu primeiro grupo
                </button>
            </div>
        <% } else { %>
            
            <% for (Grupo g : meusGrupos) { %>
            <div class="col-md-6 col-lg-4">
                <div class="card card-grupo h-100 p-3">
                    <div class="card-body d-flex flex-column">
                        
                        <div class="d-flex justify-content-between align-items-start mb-3">
                            <div class="d-flex align-items-center">
                                <div class="bg-primary bg-opacity-25 text-primary rounded-circle p-3 me-3">
                                    <i class="bi bi-people-fill fs-4"></i>
                                </div>
                                <h5 class="card-title fw-bold mb-0 text-white"><%= g.getNome() %></h5>
                            </div>
                            <span class="badge bg-secondary border border-secondary text-light">
                                <%= (g.getTipo() != null) ? g.getTipo() : "Geral" %>
                            </span>
                        </div>

                        <p class="card-text text-muted small mt-2 mb-4">
                            Clique para ver o extrato compartilhado e adicionar despesas.
                        </p>

                        <div class="mt-auto">
                            <a href="${pageContext.request.contextPath}/dashboard/visualizar_grupo.jsp?idGrupo=<%= g.getId() %>&nomeGrupo=<%= g.getNome() %>" class="btn btn-primary w-100">
                                <i class="bi bi-box-arrow-in-right me-2"></i>Acessar Grupo
                            </a>
                        </div>
                        
                    </div>
                </div>
            </div>
            <% } %>
            
        <% } %>
    </div>
</div>

<div class="modal fade" id="modalCriarGrupo" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content bg-dark border-secondary text-white shadow-lg">
            <div class="modal-header border-secondary">
                <h5 class="modal-title fw-bold">Criar Novo Grupo</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/GrupoServlet" method="POST">
                <div class="modal-body">
                    <input type="hidden" name="acao" value="criar">
                    
                    <div class="mb-3">
                        <label for="nome" class="form-label text-muted small fw-bold">NOME DO GRUPO</label>
                        <input type="text" class="form-control bg-secondary text-white border-0 py-2" id="nome" name="nome" required placeholder="Ex: Casa de Praia, Família..." style="background-color: #2b3035 !important;">
                    </div>
                    
                    <div class="alert alert-secondary d-flex align-items-center small py-2 mt-3" role="alert">
                        <i class="bi bi-info-circle-fill me-2"></i>
                        <div>Você será o administrador deste grupo.</div>
                    </div>
                </div>
                <div class="modal-footer border-secondary">
                    <button type="button" class="btn btn-link text-muted text-decoration-none" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-success px-4 fw-bold">Criar Grupo</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../navbar/rodape.jsp"/>