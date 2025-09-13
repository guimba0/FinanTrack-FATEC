<%@ page import="java.time.*, java.time.format.*, java.util.*, br.com.finantrack.controller.TransacaoDAO, br.com.finantrack.controller.Transacao, java.text.NumberFormat" %>
<%
    if (session.getAttribute("usuarioLogado") == null) {
        response.sendRedirect("../tela_login.jsp");
        return;
    }
    int usuarioId = (Integer) session.getAttribute("userId");
    int mes = Integer.parseInt(request.getParameter("mes"));
    int ano = Integer.parseInt(request.getParameter("ano"));
    String nomeMes = YearMonth.of(ano, mes).getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
    
    TransacaoDAO dao = new TransacaoDAO();
    List<Transacao> lancamentos = dao.listarLancamentosDoMes(usuarioId, ano, mes);

    double totalReceitas = 0;
    double totalDespesas = 0;
    for (Transacao t : lancamentos) {
        if ("entrada".equals(t.getTipo())) {
            totalReceitas += t.getValor();
        } else {
            if (t.isPago()) {
                totalDespesas += t.getValor();
            }
        }
    }
    double saldo = totalReceitas - totalDespesas;
    NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    String hojeFormatado = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
%>

<jsp:include page="../navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="Detalhes de <%= Character.toUpperCase(nomeMes.charAt(0)) + nomeMes.substring(1) %>" />
</jsp:include>

<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap">
        <div>
            <h1>Detalhes de <%= Character.toUpperCase(nomeMes.charAt(0)) + nomeMes.substring(1) %> de <%= ano %></h1>
            <a href="dashboard.jsp" class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-arrow-left"></i> Voltar para o Dashboard
            </a>
        </div>
        <button type="button" class="btn btn-lg btn-primary" data-bs-toggle="modal" data-bs-target="#addTransactionModal">
            <i class="bi bi-plus-circle-fill"></i> Adicionar Transação
        </button>
    </div>

    <div class="row mb-4">
        <div class="col-lg-4 col-md-6 mb-3"><div class="card text-white bg-success"><div class="card-header">Receitas</div><div class="card-body"><h5 class="card-title h5"><%= formatoMoeda.format(totalReceitas) %></h5></div></div></div>
        <div class="col-lg-4 col-md-6 mb-3"><div class="card text-white bg-danger"><div class="card-header">Despesas (Pagas)</div><div class="card-body"><h5 class="card-title h5"><%= formatoMoeda.format(totalDespesas) %></h5></div></div></div>
        <div class="col-lg-4 col-md-12 mb-3"><div class="card text-white bg-primary"><div class="card-header">Saldo Atual</div><div class="card-body"><h5 class="card-title h5"><%= formatoMoeda.format(saldo) %></h5></div></div></div>
    </div>

    <div class="table-responsive">
        <table class="table table-dark table-hover">
            <thead>
                <tr>
                    <th scope="col">Data</th>
                    <th scope="col">Descrição</th>
                    <th scope="col">Categoria</th>
                    <th scope="col">Origem</th>
                    <th scope="col">Valor (R$)</th>
                    <th scope="col">Status</th>
                    <th scope="col" class="text-end">Ações</th>
                </tr>
            </thead>
            <tbody>
                <% if (lancamentos.isEmpty()) { %>
                    <tr><td colspan="7" class="text-center text-muted py-4">Nenhuma transação encontrada para este mês.</td></tr>
                <% } else { %>
                    <% for (Transacao t : lancamentos) { %>
                        <%-- ****** CORREÇÃO DE ESTILO APLICADA AQUI ****** --%>
                        <tr class="align-middle">
                            <td><%= t.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) %></td>
                            <td><%= t.getDescricao() %></td>
                            <td><span class="badge bg-secondary"><%= t.getCategoria() %></span></td>
                            <td>
                                <% if ("Recorrente".equals(t.getOrigem())) { %>
                                    <span class="badge bg-info">Recorrente</span>
                                <% } else { %>
                                    <span class="badge bg-light text-dark">Mensal</span>
                                <% } %>
                            </td>
                            <% boolean isSaida = "saida".equals(t.getTipo()) || "despesa".equals(t.getTipo()); %>
                            <td class="text-<%= isSaida ? "danger" : "success" %>">
                                <%= isSaida ? "- " : "+ " %><%= formatoMoeda.format(t.getValor()) %>
                            </td>
                            <td>
                                <% if ("Recorrente".equals(t.getOrigem()) && t.getTipoPagamento() != null && t.getTipoPagamento().equals("Boleto")) { %>
                                    <% if (t.isPago()) { %>
                                        <span class="badge bg-success">Pago</span>
                                    <% } else { %>
                                        <form action="${pageContext.request.contextPath}/pagamento-recorrente" method="post" class="d-inline-block">
                                            <input type="hidden" name="id" value="<%= t.getId() %>">
                                            <input type="hidden" name="mes" value="<%= mes %>">
                                            <input type="hidden" name="ano" value="<%= ano %>">
                                            <button type="submit" class="btn btn-xs btn-success">Marcar Pago</button>
                                        </form>
                                    <% } %>
                                <% } else if (isSaida) { %>
                                    <span class="badge <%= t.isPago() ? "bg-success" : "bg-warning text-dark" %>"><%= t.isPago() ? "Pago" : "Pendente" %></span>
                                <% } %>
                            </td>
                            <td class="text-end">
                                <% if ("Mensal".equals(t.getOrigem())) { %>
                                    <button class="btn btn-sm btn-outline-primary edit-btn"
                                            data-bs-toggle="modal" data-bs-target="#editTransactionModal"
                                            data-id="<%= t.getId() %>" data-descricao="<%= t.getDescricao() %>"
                                            data-valor="<%= t.getValor() %>" data-data="<%= t.getData().toString() %>"
                                            data-tipo="<%= t.getTipo() %>" data-categoria="<%= t.getCategoria() %>"
                                            data-pago="<%= t.isPago() %>">
                                        <i class="bi bi-pencil-fill"></i>
                                    </button>
                                    <form action="${pageContext.request.contextPath}/transacao-deletar" method="post" style="display: inline;" onsubmit="return confirm('Tem certeza?');">
                                        <input type="hidden" name="id" value="<%= t.getId() %>">
                                        <input type="hidden" name="mes" value="<%= mes %>">
                                        <input type="hidden" name="ano" value="<%= ano %>">
                                        <button type="submit" class="btn btn-sm btn-outline-danger"><i class="bi bi-trash-fill"></i></button>
                                    </form>
                                <% } %>
                            </td>
                        </tr>
                    <% } %>
                <% } %>
            </tbody>
        </table>
    </div>
</div>

<div class="modal fade" id="addTransactionModal" tabindex="-1" aria-labelledby="addModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content bg-dark text-white">
      <div class="modal-header border-secondary">
        <h5 class="modal-title" id="addModalLabel">Nova Transação em <%= Character.toUpperCase(nomeMes.charAt(0)) + nomeMes.substring(1) %></h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <form action="${pageContext.request.contextPath}/transacao" method="post" id="addTransactionForm">
          <div class="mb-3">
            <label for="add-descricao" class="form-label">Descrição</label>
            <input type="text" class="form-control" name="descricao" id="add-descricao" required>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="add-valor" class="form-label">Valor (R$)</label>
              <input type="number" step="0.01" class="form-control" name="valor" id="add-valor" required>
            </div>
            <div class="col-md-6 mb-3">
              <label for="add-data" class="form-label">Data</label>
              <input type="date" class="form-control" name="data" id="add-data" value="<%= hojeFormatado %>" required>
            </div>
          </div>
          <div class="mb-3">
            <label for="add-tipo" class="form-label">Tipo de Transação</label>
            <select class="form-select" name="tipo" id="add-tipo" required>
              <option value="saida" selected>Saída (Gasto)</option>
              <option value="entrada">Entrada (Receita)</option>
            </select>
          </div>
          <div class="mb-3">
            <label for="add-categoria" class="form-label">Categoria</label>
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
           <div class="mb-3 d-none" id="outroCampoContainer">
            <label for="categoriaOutro" class="form-label">Especifique a Categoria</label>
            <input type="text" class="form-control" name="categoriaOutro">
          </div>
          <div class="form-check form-switch mb-3">
              <input class="form-check-input" type="checkbox" role="switch" name="pago" id="add-pago" checked>
              <label class="form-check-label" for="add-pago">Marcar como pago</label>
          </div>
          <input type="hidden" name="mes" value="<%= mes %>">
          <input type="hidden" name="ano" value="<%= ano %>">
          <div class="modal-footer border-0">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
            <button type="submit" class="btn btn-primary">Salvar</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="editTransactionModal" tabindex="-1" aria-labelledby="editModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content bg-dark text-white">
      <div class="modal-header border-secondary">
        <h5 class="modal-title" id="editModalLabel">Editar Transação</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <form action="${pageContext.request.contextPath}/transacao-editar" method="post">
          <input type="hidden" name="id" id="edit-id">
          <input type="hidden" name="mes" value="<%= mes %>">
          <input type="hidden" name="ano" value="<%= ano %>">
          <div class="mb-3">
            <label for="edit-descricao" class="form-label">Descrição</label>
            <input type="text" class="form-control" name="descricao" id="edit-descricao" required>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="edit-valor" class="form-label">Valor (R$)</label>
              <input type="number" step="0.01" class="form-control" name="valor" id="edit-valor" required>
            </div>
            <div class="col-md-6 mb-3">
              <label for="edit-data" class="form-label">Data</label>
              <input type="date" class="form-control" name="data" id="edit-data" required>
            </div>
          </div>
          <div class="mb-3">
            <label for="edit-tipo" class="form-label">Tipo de Transação</label>
            <select class="form-select" name="tipo" id="edit-tipo" required>
              <option value="saida">Saída (Gasto)</option>
              <option value="entrada">Entrada (Receita)</option>
            </select>
          </div>
          <div class="mb-3">
            <label for="edit-categoria" class="form-label">Categoria</label>
            <select class="form-select" name="categoria" id="edit-categoria" required>
                <option value="Moradia">Moradia</option>
                <option value="Supermercado">Supermercado</option>
                <option value="Streaming">Streaming</option>
                <option value="Transporte">Transporte</option>
                <option value="Lazer">Lazer</option>
                <option value="Salário">Salário</option>
                <option value="Outro">Outro</option>
            </select>
          </div>
          <div class="form-check form-switch mb-3">
              <input class="form-check-input" type="checkbox" role="switch" name="pago" id="edit-pago">
              <label class="form-check-label" for="edit-pago">Pago</label>
          </div>
          <div class="modal-footer border-0">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
            <button type="submit" class="btn btn-primary">Salvar Alterações</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>

<script>
    document.getElementById('categoriaSelect').addEventListener('change', function() {
        var outroContainer = document.getElementById('outroCampoContainer');
        if (this.value === 'Outro') {
            outroContainer.classList.remove('d-none');
            outroContainer.querySelector('input').setAttribute('required', 'required');
        } else {
            outroContainer.classList.add('d-none');
            outroContainer.querySelector('input').removeAttribute('required');
        }
    });

    document.addEventListener('DOMContentLoaded', function () {
        const editButtons = document.querySelectorAll('.edit-btn');
        const editFormId = document.getElementById('edit-id');
        const editFormDescricao = document.getElementById('edit-descricao');
        const editFormValor = document.getElementById('edit-valor');
        const editFormData = document.getElementById('edit-data');
        const editFormTipo = document.getElementById('edit-tipo');
        const editFormCategoria = document.getElementById('edit-categoria');
        const editFormPago = document.getElementById('edit-pago');

        editButtons.forEach(button => {
            button.addEventListener('click', function () {
                editFormId.value = this.dataset.id;
                editFormDescricao.value = this.dataset.descricao;
                editFormValor.value = this.dataset.valor;
                editFormData.value = this.dataset.data;
                editFormTipo.value = this.dataset.tipo;
                editFormCategoria.value = this.dataset.categoria;
                editFormPago.checked = (this.dataset.pago === 'true');
            });
        });
    });
</script>

<jsp:include page="../navbar/rodape.jsp" />