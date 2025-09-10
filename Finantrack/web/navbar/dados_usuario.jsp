<%@ page import="br.com.finantrack.controller.UsuarioDAO" %>
<%@ page import="br.com.finantrack.controller.Usuario" %>
<%
    // Proteção da página
    if (session.getAttribute("usuarioLogado") == null) {
        response.sendRedirect("../tela_login.jsp");
        return;
    }

    Integer usuarioId = (Integer) session.getAttribute("userId");
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    Usuario usuario = null; // Inicializa como nulo
    
    try {
        // Busca os dados atuais do usuário no banco
        usuario = usuarioDAO.buscarPorId(usuarioId);
    } catch (Exception e) {
        // Se der erro, imprime no console do servidor
        e.printStackTrace(); 
    }

    // Pega as mensagens de sucesso ou erro da sessão
    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");
    // Limpa as mensagens para que não apareçam de novo se a página for recarregada
    session.removeAttribute("successMessage");
    session.removeAttribute("errorMessage");
%>

<jsp:include page="../navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="Meus Dados"/>
</jsp:include>

<div class="container" style="max-width: 700px;">
    <div class="text-center mb-4">
        <h1 class="display-6">Meus Dados</h1>
        <p class="lead">Atualize suas informações pessoais e financeiras.</p>
    </div>

    <%-- Exibe as mensagens de feedback --%>
    <% if (successMessage != null) { %>
        <div class="alert alert-success" role="alert"><%= successMessage %></div>
    <% } %>
    <% if (errorMessage != null) { %>
        <div class="alert alert-danger" role="alert"><%= errorMessage %></div>
    <% } %>

    <div class="card bg-dark border-secondary">
        <div class="card-body p-4">
            <%-- Verifica se os dados do usuário foram carregados antes de mostrar o formulário --%>
            <% if (usuario != null) { %>
            <form action="${pageContext.request.contextPath}/UsuarioServlet" method="POST">
                <div class="mb-3">
                    <label for="nome" class="form-label">Nome Completo</label>
                    <input type="text" class="form-control" id="nome" name="nome" value="<%= usuario.getNome() %>" required>
                </div>
                <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control" id="email" name="email" value="<%= usuario.getEmail() %>" required>
                </div>
                <div class="mb-4">
                    <label for="salario" class="form-label">Salário Mensal (R$)</label>
                    <input type="number" step="0.01" class="form-control" id="salario" name="salario" value="<%= usuario.getSalario() %>" required>
                </div>
                <div class="d-grid">
                    <button type="submit" class="btn btn-primary btn-lg">Salvar Alterações</button>
                </div>
            </form>
            <% } else { %>
                <div class="alert alert-warning">Não foi possível carregar os dados do usuário. Verifique a conexão com o banco de dados.</div>
            <% } %>
        </div>
    </div>
</div>

<jsp:include page="../navbar/rodape.jsp"/>