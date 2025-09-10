<%@ page import="java.time.LocalDate, java.time.format.TextStyle, java.util.Locale" %>
<%-- Proteção da página... (código continua o mesmo) --%>
<%
    if (session.getAttribute("usuarioLogado") == null) {
        response.sendRedirect("../tela_login.jsp");
        return;
    }
    String nomeUsuario = (String) session.getAttribute("usuarioLogado");
    LocalDate hoje = LocalDate.now();
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
                <button class="btn btn-custom"><i class="bi bi-plus-circle-fill"></i> Adicionar Conta</button>
            </div>
            <div class="alert alert-info" role="alert">
              Em breve, aqui você poderá cadastrar suas contas recorrentes como Netflix, energia, etc.
            </div>
        </div>
    </div>
</div>

<style>
    .nav-tabs .nav-link { color: var(--cor-texto-claro); }
    .nav-tabs .nav-link.active { color: #fff; background-color: var(--cor-fundo-navbar); border-color: var(--cor-azul-principal); }
    .card-hover{transition:transform .2s ease-in-out,border-color .2s ease-in-out;}.card-hover:hover{transform:scale(1.05);border-color:var(--cor-azul-principal) !important;}
</style>

<jsp:include page="../navbar/rodape.jsp" />