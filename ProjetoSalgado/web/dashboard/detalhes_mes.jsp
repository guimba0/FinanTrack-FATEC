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
    
    // --- LINHA CORRIGIDA AQUI ---
    // A ordem correta dos parâmetros é (usuarioId, ano, mes)
    List<Transacao> lancamentos = dao.listarLancamentosDoMes(usuarioId, ano, mes);
    
    // --- LÓGICA ATUALIZADA PARA CALCULAR OS CARDS ---
    double totalReceitas = 0;
    double totalDespesas = 0;
    for (Transacao t : lancamentos) {
        if ("entrada".equals(t.getTipo()) || "receita".equals(t.getTipo())) {
            totalReceitas += t.getValor();
        } else {
            totalDespesas += t.getValor();
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
        <div class="col-lg-4 col-md-6 mb-3">
            <div class="card text-white bg-success">
                <div class="card-header">Receitas</div>
                <div class="card-body">
                    <h5 class="card-title h5"><%= formatoMoeda.format(totalReceitas) %></h5>
                </div>
            </div>
        </div>
        <div class="col-lg-4 col-md-6 mb-3">
            <div class="card text-white bg-danger">
                <div class="card-header">Despesas</div>
                <div class="card-body">
                    <h5 class="card-title h5"><%= formatoMoeda.format(totalDespesas) %></h5>
                </div>
            </div>
        </div>
        <div class="col-lg-4 col-md-12 mb-3">
            <div class="card text-white bg-primary">
                <div class="card-header">Saldo</div>
                <div class="card-body">
                    <h5 class="card-title h5"><%= formatoMoeda.format(saldo) %></h5>
                </div>
            </div>
        </div>
    </div>

    <div class="table-responsive">
        <table class="table table-dark table-hover">
            <thead>
                <tr>
                    <th scope="col">Data</th>
                    <th scope="col">Descrição</th>
                    <th scope="col">Categoria</th>
                    <th scope="col">Valor (R$)</th>
                    <th scope="col" class="text-end">Ações</th>
                </tr>
            </thead>
            <tbody>
                <% if (lancamentos.isEmpty()) { %>
                    <tr><td colspan="5" class="text-center text-muted py-4">Nenhuma transação encontrada para este mês.</td></tr>
                <% } else { %>
                    <% for (Transacao t : lancamentos) { %>
                        <tr>
                            <td><%= t.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) %></td>
                            <td><%= t.getDescricao() %></td>
                            <td><span class="badge bg-secondary"><%= t.getCategoria() %></span></td>
                            <% boolean isSaida = "saida".equals(t.getTipo()) || "despesa".equals(t.getTipo()); %>
                            <td class="text-<%= isSaida ? "danger" : "success" %>">
                                <%= isSaida ? "- " : "+ " %><%= formatoMoeda.format(t.getValor()) %>
                            </td>
                            <td class="text-end">
                                <button class="btn btn-sm btn-outline-primary"><i class="bi bi-pencil-fill"></i></button>
                                <button class="btn btn-sm btn-outline-danger"><i class="bi bi-trash-fill"></i></button>
                            </td>
                        </tr>
                    <% } %>
                <% } %>
            </tbody>
        </table>
    </div>
</div>

<div class="modal fade" id="addTransactionModal" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content bg-dark text-white">
      <div class="modal-header border-secondary">
        <h5 class="modal-title" id="modalLabel">Nova Transação em <%= Character.toUpperCase(nomeMes.charAt(0)) + nomeMes.substring(1) %></h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <form action="${pageContext.request.contextPath}/transacao" method="post" id="transactionForm">
          <div class="mb-3">
            <label for="descricao" class="form-label">Descrição</label>
            <input type="text" class="form-control" name="descricao" required>
          </div>
          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="valor" class="form-label">Valor (R$)</label>
              <input type="number" step="0.01" class="form-control" name="valor" required>
            </div>
            <div class="col-md-6 mb-3">
              <label for="data" class="form-label">Data</label>
              <input type="date" class="form-control" name="data" value="<%= hojeFormatado %>" required>
            </div>
          </div>
          <div class="mb-3">
            <label for="tipo" class="form-label">Tipo de Transação</label>
            <select class="form-select" name="tipo" required>
              <option value="saida" selected>Saída (Gasto)</option>
              <option value="entrada">Entrada (Receita)</option>
            </select>
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
          <div class="mb-3 d-none" id="outroCampoContainer">
            <label for="categoriaOutro" class="form-label">Especifique a Categoria</label>
            <input type="text" class="form-control" name="categoriaOutro">
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

<script>
    // Seu script original para o campo "Outro"
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
</script>

<jsp:include page="../navbar/rodape.jsp" />