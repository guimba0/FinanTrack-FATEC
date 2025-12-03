<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="br.com.finantrack.controller.*"%>
<%@page import="java.util.*, java.time.LocalDate, java.time.format.DateTimeFormatter"%>

<%
    // Validação de Login
    Usuario u = (Usuario) session.getAttribute("usuario");
    if (u == null) { response.sendRedirect("../tela_login.jsp"); return; }

    // Recupera dados do Grupo e Mês
    String idGrupoStr = request.getParameter("idGrupo");
    String nomeGrupo = request.getParameter("nomeGrupo");
    
    if (idGrupoStr == null) { response.sendRedirect("meus_grupos.jsp"); return; }
    int idGrupo = Integer.parseInt(idGrupoStr);

    // Lógica do Mês (Igual detalhes_mes)
    LocalDate hoje = LocalDate.now();
    String mesStr = request.getParameter("mes");
    String anoStr = request.getParameter("ano");
    int mesAtual = (mesStr != null) ? Integer.parseInt(mesStr) : hoje.getMonthValue();
    int anoAtual = (anoStr != null) ? Integer.parseInt(anoStr) : hoje.getYear();

    // Busca transações do Grupo
    TransacaoDAO tDao = new TransacaoDAO();
    List<Transacao> transacoesGrupo = new ArrayList<>();
    try {
        transacoesGrupo = tDao.listarPorGrupo(idGrupo, mesAtual, anoAtual);
    } catch(Exception e) { e.printStackTrace(); }
    
    // Nomes dos meses para o seletor
    String[] nomesMeses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
%>

<jsp:include page="../navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="<%= nomeGrupo %>"/>
</jsp:include>

<style>
    body { background-color: #343a40 !important; color: #f8f9fa; }
    .card { background-color: #212529; border: 1px solid #495057; }
    .table { color: #e9ecef; }
    .nav-pills .nav-link { color: #adb5bd; }
    .nav-pills .nav-link.active { background-color: #0d6efd; color: white; }
</style>

<div class="container mt-4">
    
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold text-primary"><i class="bi bi-people-fill me-2"></i><%= nomeGrupo %></h2>
            <p class="text-muted">Gestão Financeira Compartilhada</p>
        </div>
        <div>
            <a href="meus_grupos.jsp" class="btn btn-outline-secondary me-2">Voltar</a>
            <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#modalNovaDespesa">
                <i class="bi bi-plus-lg"></i> Nova Despesa
            </button>
        </div>
    </div>

    <div class="card mb-4 shadow-sm">
        <div class="card-body py-2">
            <ul class="nav nav-pills justify-content-center">
                <% for (int i = 0; i < 12; i++) { %>
                    <li class="nav-item">
                        <a class="nav-link <%= (i + 1 == mesAtual) ? "active" : "" %>" 
                           href="visualizar_grupo.jsp?idGrupo=<%= idGrupo %>&nomeGrupo=<%= nomeGrupo %>&mes=<%= i + 1 %>&ano=<%= anoAtual %>">
                           <%= nomesMeses[i] %>
                        </a>
                    </li>
                <% } %>
            </ul>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th class="ps-4">Data</th>
                            <th>Descrição</th>
                            <th>Quem Pagou?</th>
                            <th>Categoria</th>
                            <th class="text-end pe-4">Valor</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (transacoesGrupo.isEmpty()) { %>
                            <tr>
                                <td colspan="5" class="text-center py-5 text-muted">
                                    <i class="bi bi-inbox fs-1 d-block mb-2 opacity-50"></i>
                                    Nenhuma despesa neste grupo em <%= nomesMeses[mesAtual-1] %>.
                                </td>
                            </tr>
                        <% } else { 
                            UsuarioDAO uDao = new UsuarioDAO(); // Para buscar nome de quem pagou
                        %>
                            <% for (Transacao t : transacoesGrupo) { 
                                String nomePagador = "Usuário"; 
                                try { 
                                    Usuario pagador = uDao.buscarPorId(t.getUsuarioId()); 
                                    if(pagador != null) nomePagador = pagador.getNome();
                                } catch(Exception e) {}
                            %>
                            <tr>
                                <td class="ps-4 text-muted"><%= t.getData().format(DateTimeFormatter.ofPattern("dd/MM")) %></td>
                                <td class="fw-bold"><%= t.getDescricao() %></td>
                                <td>
                                    <span class="badge bg-secondary bg-opacity-25 text-light border border-secondary">
                                        <i class="bi bi-person-fill"></i> <%= nomePagador %>
                                    </span>
                                </td>
                                <td><%= t.getCategoria() %></td>
                                <td class="text-end pe-4 fw-bold <%= t.getTipo().equals("Receita") ? "text-success" : "text-danger" %>">
                                    R$ <%= String.format("%.2f", t.getValor()) %>
                                </td>
                            </tr>
                            <% } %>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="modalNovaDespesa" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content bg-dark border-secondary text-white">
            <div class="modal-header border-secondary">
                <h5 class="modal-title">Adicionar ao Grupo</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/TransacaoServlet" method="POST">
                <div class="modal-body">
                    <input type="hidden" name="idGrupo" value="<%= idGrupo %>">
                    <input type="hidden" name="nomeGrupo" value="<%= nomeGrupo %>">
                    
                    <div class="mb-3">
                        <label class="form-label text-muted small fw-bold">DESCRIÇÃO</label>
                        <input type="text" name="descricao" class="form-control bg-secondary text-white border-0" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label text-muted small fw-bold">VALOR (R$)</label>
                            <input type="number" step="0.01" name="valor" class="form-control bg-secondary text-white border-0" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label text-muted small fw-bold">DATA</label>
                            <input type="date" name="data" value="<%= LocalDate.now() %>" class="form-control bg-secondary text-white border-0" required>
                        </div>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label text-muted small fw-bold">TIPO</label>
                            <select name="tipo" class="form-select bg-secondary text-white border-0">
                                <option value="Despesa">Despesa</option>
                                <option value="Receita">Receita (Depósito)</option>
                            </select>
                        </div>
                        <div class="col-6">
                            <label class="form-label text-muted small fw-bold">CATEGORIA</label>
                            <select name="categoria" class="form-select bg-secondary text-white border-0">
                                <option>Alimentação</option>
                                <option>Moradia</option>
                                <option>Transporte</option>
                                <option>Lazer</option>
                                <option>Outros</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-secondary">
                    <button type="button" class="btn btn-link text-muted" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-success fw-bold">Salvar no Grupo</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../navbar/rodape.jsp"/>