<%-- 
    Página: Visualizar Grupo (Planilha Compartilhada)
    Objetivo: Exibir despesas do grupo, totais e permitir pagamentos.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="br.com.finantrack.controller.*, java.util.*, java.time.format.DateTimeFormatter"%>
<%
    // 1. Verificação de Segurança
    Usuario u = (Usuario) session.getAttribute("usuario");
    if (u == null) { 
        response.sendRedirect("../tela_login.jsp");
        return; 
    }
    
    // 2. Validação do ID do Grupo
    String gIdParam = request.getParameter("id");
    if (gIdParam == null) { 
        response.sendRedirect("meus_grupos.jsp"); 
        return; 
    }
    int grupoId = Integer.parseInt(gIdParam);
    
    // 3. Carregamento de Dados
    GrupoDAO dao = new GrupoDAO();
    List<Transacao> lista = dao.listarTransacoesGrupo(grupoId);
    
    // 4. Cálculo de Totais (Quanto o grupo deve no total?)
    double totalPendente = 0;
    for(Transacao t : lista) { 
        if(!t.isPago()) totalPendente += t.getValor();
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"><title>Grupo - FinanTrack</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <style>
        /* Define a cor de fundo via CSS, sem usar class="bg-light" no body */
        body { background-color: #f8f9fa; }
        
        .input-dark { background: #343a40; border: 1px solid #495057; color: #fff; }
        .badge-payer { font-size: 0.8em; opacity: 0.8; }
    </style>
</head>
<body> <%-- Tag BODY limpa, sem classes --%>
    
    <jsp:include page="../navbar/cabecalho.jsp" />

    <div class="container mt-4 mb-5">
        
        <%-- Barra de Navegação Interna --%>
        <div class="row align-items-center mb-4">
            <div class="col-md-6">
                <a href="meus_grupos.jsp" class="text-decoration-none text-secondary small"><i class="bi bi-arrow-left"></i> Voltar</a>
                <h3 class="fw-bold mt-1"><i class="bi bi-people-fill text-primary"></i> Finanças Compartilhadas</h3>
            </div>
            <div class="col-md-6 text-end">
                <button class="btn btn-outline-primary btn-sm" data-bs-toggle="modal" data-bs-target="#modalInvite"><i class="bi bi-person-plus"></i> Convidar Membro</button>
                <button class="btn btn-dark btn-sm fw-bold ms-2" data-bs-toggle="modal" data-bs-target="#modalAdd"><i class="bi bi-plus-lg"></i> Nova Despesa</button>
            </div>
        </div>
        
        <%-- Resumo de Dívidas --%>
        <div class="alert alert-light border shadow-sm d-flex justify-content-between align-items-center">
            <div><strong>Total Pendente na Casa:</strong></div>
            <div class="text-danger fw-bold fs-4">R$ <%= String.format("%.2f", totalPendente) %></div>
        </div>

        <%-- Feedback de Ações (Mensagens de Sucesso/Erro) --%>
        <% String msg = (String) session.getAttribute("msgGrupo");
           if(msg != null) { session.removeAttribute("msgGrupo"); %> <div class="alert alert-success"><%= msg %></div> <% } %>
        <% String err = (String) session.getAttribute("errGrupo");
           if(err != null) { session.removeAttribute("errGrupo"); %> <div class="alert alert-danger"><%= err %></div> <% } %>

        <%-- Tabela de Despesas --%>
        <div class="card shadow border-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="bg-light text-secondary">
                        <tr>
                            <th class="ps-4">Vencimento</th>
                            <th>Descrição</th>
                            <th>Categoria</th>
                            <th>Valor</th>
                            <th>Status / Quem Pagou</th>
                            <th class="text-center">Ação</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if(lista.isEmpty()) { %>
                            <tr><td colspan="6" class="text-center py-5 text-muted">Nenhuma conta adicionada ao grupo ainda.</td></tr>
                        <% } %>
                        
                        <% for(Transacao t : lista) { %>
                        <tr class="<%= t.isPago() ? "table-light text-muted" : "" %>">
                            <td class="ps-4 fw-bold"><%= t.getData().format(DateTimeFormatter.ofPattern("dd/MM")) %></td>
                            <td><%= t.getDescricao() %></td>
                            <td><span class="badge bg-light text-dark border"><%= t.getCategoria() %></span></td>
                            <td class="fw-bold">R$ <%= String.format("%.2f", t.getValor()) %></td>
                            <td>
                                <% if(t.isPago()) { %>
                                    <span class="badge bg-success"><i class="bi bi-check-lg"></i> Pago</span>
                                    <div class="badge-payer text-success small">por <%= t.getOrigem() != null ? t.getOrigem() : "Alguém" %></div>
                                <% } else { %>
                                    <span class="badge bg-warning text-dark">Pendente</span>
                                <% } %>
                            </td>
                            <td class="text-center">
                                <%-- Botão de Pagar --%>
                                <% if(!t.isPago()) { %>
                                    <form action="${pageContext.request.contextPath}/GrupoServlet" method="POST" style="display:inline">
                                        <input type="hidden" name="action" value="pagar">
                                        <input type="hidden" name="grupoId" value="<%= grupoId %>">
                                        <input type="hidden" name="transacaoId" value="<%= t.getId() %>">
                                        <button type="submit" class="btn btn-sm btn-outline-success" title="Eu paguei isso">
                                            <i class="bi bi-cash-coin"></i> Pagar
                                        </button>
                                    </form>
                                <% } else { %>
                                    <i class="bi bi-lock-fill text-muted"></i>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <%-- Modal Adicionar Despesa --%>
    <div class="modal fade" id="modalAdd" tabindex="-1"><div class="modal-dialog modal-dialog-centered"><div class="modal-content">
        <form action="${pageContext.request.contextPath}/GrupoServlet" method="POST">
            <input type="hidden" name="action" value="adicionarDespesa">
            <input type="hidden" name="grupoId" value="<%= grupoId %>">
            <div class="modal-header bg-dark text-white"><h5 class="modal-title">Adicionar Conta na Casa</h5><button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">
                <div class="mb-3"><label>O que é?</label><input type="text" name="descricao" class="form-control" placeholder="Ex: Conta de Luz" required></div>
                <div class="row g-2 mb-3">
                    <div class="col-6"><label>Valor</label><input type="number" name="valor" step="0.01" class="form-control" required></div>
                    <div class="col-6"><label>Vencimento</label><input type="date" name="data" class="form-control" required></div>
                </div>
                <div class="mb-3"><label>Categoria</label>
                    <select name="categoria" class="form-select">
                        <option>Moradia</option><option>Alimentação</option><option>Internet/TV</option><option>Manutenção</option><option>Outros</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer"><button type="submit" class="btn btn-dark w-100">Adicionar à Lista</button></div>
        </form>
    </div></div></div>

    <%-- Modal Convidar Membro --%>
    <div class="modal fade" id="modalInvite" tabindex="-1"><div class="modal-dialog modal-dialog-centered"><div class="modal-content">
        <form action="${pageContext.request.contextPath}/GrupoServlet" method="POST">
            <input type="hidden" name="action" value="convidar">
            <input type="hidden" name="grupoId" value="<%= grupoId %>">
            <div class="modal-header"><h5 class="modal-title">Convidar por Email</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">
                <p class="text-muted small">A pessoa precisa ter cadastro no FinanTrack.</p>
                <input type="email" name="email" class="form-control" placeholder="email@exemplo.com" required>
            </div>
            <div class="modal-footer"><button type="submit" class="btn btn-primary">Enviar Convite</button></div>
        </form>
    </div></div></div>

    <jsp:include page="../navbar/rodape.jsp" />
</body>
</html>