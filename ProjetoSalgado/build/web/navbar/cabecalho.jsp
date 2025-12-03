<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.*, java.time.*, java.time.format.DateTimeFormatter, java.time.temporal.ChronoUnit"%>
<%@page import="br.com.finantrack.controller.*"%>

<%
    // 1. RECUPERAÇÃO ROBUSTA DA SESSÃO (Igual ao Dashboard)
    Usuario u = (Usuario) session.getAttribute("usuario");
    boolean estaLogado = (u != null);
    
    // Variáveis para exibição
    String nomeExibicao = (estaLogado && u.getNome() != null) ? u.getNome() : "Usuário";
    Integer idUsuario = (estaLogado) ? u.getId() : null;

    // 2. LÓGICA DE NOTIFICAÇÕES (Blindada contra erros)
    List<String> notificacoes = new ArrayList<>();
    
    if (estaLogado && idUsuario != null) {
        try {
            ContaRecorrenteDAO dao = new ContaRecorrenteDAO();
            List<ContaRecorrente> contas = dao.listar(idUsuario);
            
            LocalDate hoje = LocalDate.now();
            String mesAnoAtual = hoje.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            for (ContaRecorrente conta : contas) {
                if ("Boleto".equals(conta.getTipoPagamento())) {
                    boolean pagoEsteMes = mesAnoAtual.equals(conta.getUltimoMesPago());
                    if (!pagoEsteMes && conta.getDiaVencimento() != null) {
                        int diaVenc = conta.getDiaVencimento();
                        if (diaVenc > hoje.lengthOfMonth()) { diaVenc = hoje.lengthOfMonth(); }
                        LocalDate dataVencimento = LocalDate.of(hoje.getYear(), hoje.getMonth(), diaVenc);
                        long diasRestantes = ChronoUnit.DAYS.between(hoje, dataVencimento);

                        if (diasRestantes >= 0 && diasRestantes <= 7) {
                            if (diasRestantes == 0) notificacoes.add("'" + conta.getDescricao() + "' vence HOJE!");
                            else if (diasRestantes == 1) notificacoes.add("'" + conta.getDescricao() + "' vence amanhã!");
                            else notificacoes.add("'" + conta.getDescricao() + "' vence em " + diasRestantes + " dias.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Se der erro no banco, o menu continua funcionando, só sem notificações.
            System.out.println("Erro ao carregar notificações no cabeçalho: " + e.getMessage());
        }
    }
%>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<style>
    /* CSS para garantir sobreposição correta */
    .dropdown-menu { z-index: 2050 !important; }
    .navbar { box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
</style>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-0">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/index.jsp">
            <i class="bi bi-bar-chart-line-fill text-primary"></i> FinanTrack
        </a>
        
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <% if (estaLogado) { %>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard/dashboard.jsp">Dashboard</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard/meus_grupos.jsp">Grupos</a>
                    </li>
                <% } %>
            </ul>
            
            <ul class="navbar-nav ms-auto align-items-center">
                <% if (estaLogado) { %>
                    
                    <li class="nav-item dropdown me-2">
                        <a class="nav-link position-relative" href="#" role="button" data-bs-toggle="dropdown">
                            <i class="bi bi-bell-fill fs-5"></i>
                            <% if (!notificacoes.isEmpty()) { %>
                                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" style="font-size: 0.5rem;">
                                    <%= notificacoes.size() %>
                                </span>
                            <% } %>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end shadow border-0 mt-2">
                            <% if (notificacoes.isEmpty()) { %>
                                <li><a class="dropdown-item small text-muted" href="#">Sem notificações</a></li>
                            <% } else { %>
                                <li><h6 class="dropdown-header">Contas a Vencer</h6></li>
                                <% for (String notif : notificacoes) { %>
                                    <li><a class="dropdown-item text-danger small fw-bold" href="#"><i class="bi bi-exclamation-circle me-1"></i> <%= notif %></a></li>
                                <% } %>
                            <% } %>
                        </ul>
                    </li>

                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" role="button" data-bs-toggle="dropdown">
                            <i class="bi bi-person-circle fs-4 me-2"></i> <span><%= nomeExibicao %></span>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end shadow border-0 mt-2">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/navbar/dados_usuario.jsp"><i class="bi bi-person-lines-fill me-2"></i> Meus Dados</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/LogoutServlet"><i class="bi bi-box-arrow-right me-2"></i> Sair</a></li>
                        </ul>
                    </li>

                <% } else { %>
                    <li class="nav-item">
                        <a class="nav-link btn btn-sm btn-outline-light px-3 ms-2 text-white" href="${pageContext.request.contextPath}/tela_login.jsp">Entrar</a>
                    </li>
                <% } %>
            </ul>
        </div>
    </div>
</nav>