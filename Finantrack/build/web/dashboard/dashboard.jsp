<%@ page import="java.time.LocalDate, java.time.format.TextStyle, java.util.Locale, br.com.finantrack.controller.ContaRecorrenteDAO, br.com.finantrack.controller.ContaRecorrente, java.util.List, java.text.NumberFormat" %>
<%
    // Proteção da página...
    if (session.getAttribute("usuarioLogado") == null) {
        response.sendRedirect("../tela_login.jsp");
        return;
    }
    String nomeUsuario = (String) session.getAttribute("usuarioLogado");
    Integer usuarioId = (Integer) session.getAttribute("userId");
    LocalDate hoje = LocalDate.now();

    // Instancia o DAO para buscar as contas
    ContaRecorrenteDAO contaRecorrenteDAO = new ContaRecorrenteDAO();
    List<ContaRecorrente> listaContas = contaRecorrenteDAO.listar(usuarioId);
%>

<jsp:include page="../navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="Meu Dashboard" />
</jsp:include>

<div class="container">
    <div class="text-center mb-4">
        <h1 class="display-5">Bem-vindo(a), <%= nomeUsuario %>!</h1>
        <p class="lead">Aqui você pode gerenciar suas finanças de forma simples e eficaz.</p>
    </div>

    <ul class="nav nav-tabs nav-fill mb-4" id="myTab" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" id="mensal-tab" data-bs-toggle="tab" data-bs-target="#mensal-pane" type="button" role="tab" aria-controls="mensal-pane" aria-selected="true">Visão Mensal</button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="recorrente-tab" data-bs-toggle="tab" data-bs-target="#recorrente-pane" type="button" role="tab" aria-controls="recorrente-pane" aria-selected="false">Contas Recorrentes</button>
        </li>
    </ul>

    <div class="tab-content" id="myTabContent">
        <div class="tab-pane fade show active" id="mensal-pane" role="tabpanel" aria-labelledby="mensal-tab" tabindex="0">
            <div class="row g-3">
                <% for (int i = 0; i < 6; i++) {
                    LocalDate mesAtual = hoje.plusMonths(i);
                    String nomeMes = mesAtual.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
                    int ano = mesAtual.getYear();
                %>
                <div class="col-md-4 col-lg-3">
                    <a href="detalhes_mes.jsp?mes=<%= mesAtual.getMonthValue() %>&ano=<%= ano %>" class="text-decoration-none">
                        <div class="card bg-transparent border-secondary text-center h-100 card-hover">
                            <div class="card-body d-flex flex-column justify-content-center">
                                <i class="bi bi-calendar-month-fill fs-1 text-primary"></i>
                                <h5 class="card-title mt-3"><%= Character.toUpperCase(nomeMes.charAt(0)) + nomeMes.substring(1) %></h5>
                                <p class="card-text text-muted"><%= ano %></p>
                            </div>
                        </div>
                    </a>
                </div>
                <% } %>
            </div>
        </div>

        <div class="tab-pane fade" id="recorrente-pane" role="tabpanel" aria-labelledby="recorrente-tab" tabindex="0">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h3>Gerenciar Contas Recorrentes</h3>
                <button class="btn btn-custom" data-bs-toggle="modal" data-bs-target="#addContaModal">
                    <i class="bi bi-plus-circle-fill"></i> Adicionar Conta
                </button>
            </div>
            
            <div class="table-responsive">
                <table class="table table-dark table-striped table-hover">
                    <thead>
                        <tr>
                            <th scope="col">Descrição</th>
                            <th scope="col">Valor</th>
                            <th scope="col">Categoria</th>
                            <th scope="col">Dia do Venc.</th>
                            <th scope="col">Início</th>
                            <th scope="col">Fim</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (ContaRecorrente conta : listaContas) { %>
                        <tr>
                            <td><%= conta.getDescricao() %></td>
                            <td><%= NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(conta.getValor()) %></td>
                            <td><%= conta.getCategoria() %></td>
                            <td><%= conta.getDiaVencimento() %></td>
                            <td><%= conta.getDataInicio() %></td>
                            <td><%= (conta.getDataFim() != null ? conta.getDataFim() : "N/A") %></td>
                        </tr>
                        <% } %>
                        <% if (listaContas.isEmpty()) { %>
                        <tr>
                            <td colspan="6" class="text-center">Nenhuma conta recorrente cadastrada.</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="addContaModal" tabindex="-1" aria-labelledby="addContaModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content bg-dark text-light">
            <div class="modal-header">
                <h5 class="modal-title" id="addContaModalLabel">Adicionar Conta Recorrente</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form action="${pageContext.request.contextPath}/conta-recorrente" method="POST">
                    <div class="mb-3">
                        <label for="descricao" class="form-label">Descrição</label>
                        <input type="text" class="form-control" id="descricao" name="descricao" required>
                    </div>
                    <div class="mb-3">
                        <label for="valor" class="form-label">Valor</label>
                        <input type="number" step="0.01" class="form-control" id="valor" name="valor" required>
                    </div>
                    <div class="mb-3">
                         <label for="categoria" class="form-label">Categoria</label>
                         <select class="form-select" name="categoria" id="categoriaSelect" required>
                         <option value="Moradia">Moradia</option>
                        <option value="Supermercado">Supermercado</option>
                         <option value="Streaming">Streaming</option>
                        <option value="Transporte">Transporte</option>
                         <option value="Lazer">Lazer</option>
                         <option value="Salário">Salário</option>
                        <option value="Outro">Outro (Personalizado)</option>
                     </select>
                    </div>
                    <div class="row">
                      <div class="col-md-6 mb-3">
                            <label for="dataInicio" class="form-label">Data de Início</label>
                            <input type="date" class="form-control" id="dataInicio" name="dataInicio" required>
                        </div>  
                        <div class="col-md-6 mb-3">
                            <label for="dataFim" class="form-label">Data de Fim (Opcional)</label>
                            <input type="date" class="form-control" id="dataFim" name="dataFim">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary">Salvar Conta</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>


<style>
    .nav-tabs .nav-link { color: var(--cor-texto-claro); }
    .nav-tabs .nav-link.active { color: #fff; background-color: var(--cor-fundo-navbar); border-color: var(--cor-azul-principal); }
    .card-hover{transition:transform .2s ease-in-out,border-color .2s ease-in-out;}
    .card-hover:hover{transform:scale(1.05);border-color:var(--cor-azul-principal) !important;}

    /* Adicionado para corrigir a cor do texto nos cards de mês */
    .card-hover .card-title {
        color: #f8f9fa; /* Cor branca para o título (nome do mês) */
    }
    .card-hover .card-text {
        color: #adb5bd !important; /* Cor cinza claro para o texto secundário (ano) */
    }
</style>

<jsp:include page="../navbar/rodape.jsp" />