<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, br.com.finantrack.controller.*"%>
<%
    Usuario u = (Usuario) session.getAttribute("usuario");
    if (u == null) { response.sendRedirect("../tela_login.jsp"); return; }
    
    GrupoDAO gDao = new GrupoDAO();
    List<Grupo> meusGrupos = gDao.listarMeusGrupos(u.getId());
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"><title>Meus Grupos - FinanTrack</title>
    <style>
        body { background-color: #f8f9fa !important; }
        .card-hover { transition: transform 0.2s, box-shadow 0.2s; }
        .card-hover:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important; }
        .btn-add-email { cursor: pointer; border: 1px dashed #6c757d; color: #6c757d; width: 100%; transition: 0.3s; }
        .btn-add-email:hover { background: #e9ecef; color: #0d6efd; border-color: #0d6efd; }
    </style>
</head>
<body>
    
    <div style="position: sticky; top: 0; z-index: 1060;">
        <jsp:include page="../navbar/cabecalho.jsp" />
    </div>

    <div class="container mt-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold text-secondary"><i class="bi bi-people-fill"></i> Finanças em Grupo</h2>
                <p class="text-muted">Gerencie despesas compartilhadas.</p>
            </div>
            <button class="btn btn-primary shadow-sm" data-bs-toggle="modal" data-bs-target="#modalNovoGrupo">
                <i class="bi bi-plus-lg"></i> Criar Grupo
            </button>
        </div>

        <div class="row g-4">
            <% if (meusGrupos.isEmpty()) { %>
                <div class="col-12 text-center py-5">
                    <div class="display-1 text-muted opacity-25"><i class="bi bi-collection"></i></div>
                    <h5 class="mt-3 text-muted fw-normal">Você ainda não participa de nenhum grupo.</h5>
                    <p class="small text-muted">Crie um novo para começar!</p>
                </div>
            <% } else { %>
                <% for (Grupo g : meusGrupos) { %>
                <div class="col-md-6 col-lg-4">
                    <div class="card h-100 border-0 shadow-sm card-hover">
                        <div class="card-body d-flex flex-column">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <h5 class="fw-bold text-primary mb-0 text-truncate" title="<%= g.getNome() %>"><%= g.getNome() %></h5>
                                <span class="badge bg-secondary bg-opacity-10 text-secondary border border-secondary rounded-pill">
                                    <%= (g.getTipo() != null) ? g.getTipo() : "Geral" %>
                                </span>
                            </div>
                            
                            <div class="mt-auto pt-3">
                                <hr class="text-muted opacity-25">
                                <a href="detalhes_grupo.jsp?id=<%= g.getId() %>" class="btn btn-outline-primary w-100">
                                    Acessar Planilha <i class="bi bi-arrow-right ms-1"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>
            <% } %>
        </div>
    </div>

    <div class="modal fade" id="modalNovoGrupo" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow-lg">
                <form action="${pageContext.request.contextPath}/GrupoServlet" method="POST">
                    <input type="hidden" name="acao" value="criar">
                    
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title fw-bold"><i class="bi bi-people-fill me-2"></i>Novo Grupo</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label fw-bold small text-muted">Nome do Grupo</label>
                            <input type="text" name="nome" class="form-control" placeholder="Ex: Casa de Praia" required>
                        </div>
                        
                        <hr>

                        <label class="form-label fw-bold small text-primary mb-2">Convidar Membros (Email)</label>
                        <div id="emailContainer">
                            <div class="input-group mb-2">
                                <span class="input-group-text bg-light"><i class="bi bi-envelope"></i></span>
                                <input type="email" name="emails" class="form-control" placeholder="email@exemplo.com">
                            </div>
                        </div>

                        <button type="button" id="btnAddEmail" class="btn btn-sm btn-add-email py-2 mt-1" onclick="adicionarCampoEmail()">
                            <i class="bi bi-plus-circle"></i> Adicionar outra pessoa
                        </button>
                        <small class="text-muted d-block mt-2 text-end" id="contadorPessoas">1/5 pessoas</small>
                    </div>
                    
                    <div class="modal-footer bg-light">
                        <button type="button" class="btn btn-link text-muted text-decoration-none" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary px-4 fw-bold">Criar Grupo</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        let contador = 1;
        const maximo = 5;
        const container = document.getElementById('emailContainer');
        const btnAdd = document.getElementById('btnAddEmail');
        const textoContador = document.getElementById('contadorPessoas');

        function adicionarCampoEmail() {
            if (contador >= maximo) return;
            contador++;
            const div = document.createElement('div');
            div.className = 'input-group mb-2';
            div.innerHTML = `
                <span class="input-group-text bg-light"><i class="bi bi-envelope"></i></span>
                <input type="email" name="emails" class="form-control" placeholder="Outro participante...">
                <button type="button" class="btn btn-outline-danger" onclick="removerCampo(this)"><i class="bi bi-x"></i></button>
            `;
            container.appendChild(div);
            atualizarUI();
        }

        function removerCampo(botao) {
            botao.parentElement.remove();
            contador--;
            atualizarUI();
        }

        function atualizarUI() {
            textoContador.innerText = contador + "/" + maximo + " pessoas";
            btnAdd.style.display = (contador >= maximo) ? 'none' : 'block';
        }
    </script>
</body>
</html>