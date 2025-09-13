<%@ page import="java.util.*, java.time.*, java.time.format.DateTimeFormatter, java.time.temporal.ChronoUnit, br.com.finantrack.controller.ContaRecorrenteDAO, br.com.finantrack.controller.ContaRecorrente" %>
<%
    // Lógica das notificações (sem alterações)
    List<String> notificacoes = new ArrayList<>();
    if (session.getAttribute("usuarioLogado") != null) {
        Integer usuarioId = (Integer) session.getAttribute("userId");
        ContaRecorrenteDAO dao = new ContaRecorrenteDAO();
        List<ContaRecorrente> contas = dao.listar(usuarioId);
        
        LocalDate hoje = LocalDate.now();
        String mesAnoAtual = hoje.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        for (ContaRecorrente conta : contas) {
            if ("Boleto".equals(conta.getTipoPagamento())) {
                boolean pagoEsteMes = mesAnoAtual.equals(conta.getUltimoMesPago());
                if (!pagoEsteMes && conta.getDiaVencimento() != null) {
                    int diaVenc = conta.getDiaVencimento();
                    if (diaVenc > hoje.lengthOfMonth()) {
                        diaVenc = hoje.lengthOfMonth();
                    }
                    LocalDate dataVencimento = LocalDate.of(hoje.getYear(), hoje.getMonth(), diaVenc);
                    long diasRestantes = ChronoUnit.DAYS.between(hoje, dataVencimento);

                    if (diasRestantes >= 0 && diasRestantes <= 7) {
                        if (diasRestantes == 0) {
                            notificacoes.add("'" + conta.getDescricao() + "' vence HOJE!");
                        } else if (diasRestantes == 1) {
                            notificacoes.add("'" + conta.getDescricao() + "' vence amanhã!");
                        } else {
                            notificacoes.add("'" + conta.getDescricao() + "' vence em " + diasRestantes + " dias.");
                        }
                    }
                }
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="pt-BR" data-bs-theme="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= request.getParameter("titulo") != null ? request.getParameter("titulo") : "FinanTrack" %></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        .card-hover:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 12px rgba(0, 123, 255, 0.2);
            transition: all 0.2s ease-in-out;
        }
        .btn-xs {
            padding: 0.2rem 0.4rem;
            font-size: 0.75rem;
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg bg-body-tertiary mb-4">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">
                <i class="bi bi-bar-chart-line-fill"></i>
                FinanTrack
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <% if (session.getAttribute("usuarioLogado") != null) { %>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/dashboard/dashboard.jsp">Dashboard</a>
                        </li>
                        <%-- ****** NOVO LINK ADICIONADO AQUI ****** --%>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/dashboard/graficos.jsp">Gráficos</a>
                        </li>
                    <% } %>
                </ul>
                
                <ul class="navbar-nav ms-auto">
                    <% if (session.getAttribute("usuarioLogado") != null) { %>
                        
                        <li class="nav-item dropdown">
                            <a class="nav-link" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="bi bi-bell-fill position-relative">
                                    <% if (!notificacoes.isEmpty()) { %>
                                        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                                            <%= notificacoes.size() %>
                                            <span class="visually-hidden">notificações não lidas</span>
                                        </span>
                                    <% } %>
                                </i>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <% if (notificacoes.isEmpty()) { %>
                                    <li><a class="dropdown-item" href="#">Nenhuma notificação</a></li>
                                <% } else { %>
                                    <% for (String notificacao : notificacoes) { %>
                                        <li><a class="dropdown-item" href="#"><%= notificacao %></a></li>
                                    <% } %>
                                <% } %>
                            </ul>
                        </li>

                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="bi bi-person-circle"></i>
                                <%= session.getAttribute("usuarioLogado") %>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/navbar/dados_usuario.jsp">Seus Dados</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Sair</a></li>
                            </ul>
                        </li>
                    <% } else { %>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/tela_login.jsp">Entrar</a>
                        </li>
                    <% } %>
                </ul>
            </div>
        </div>
    </nav>