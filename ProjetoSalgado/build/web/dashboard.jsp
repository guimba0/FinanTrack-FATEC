<%-- Proteção da página: verifica se o usuário está na sessão --%>
<%
    if (session.getAttribute("usuarioLogado") == null) {
        response.sendRedirect("tela_login.jsp");
        return; // Impede o resto da página de ser processado
    }
%>

<jsp:include page="navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="Meu Dashboard" />
</jsp:include>

<%-- CONTEÚDO DA PÁGINA DE DASHBOARD --%>
<div class="text-center">
    <h1 class="display-5">Bem-vindo(a) ao seu Dashboard!</h1>
    <p class="lead">Aqui você poderá gerenciar suas finanças.</p>
    
    <%-- Exemplo de como você pode pegar o nome do usuário da sessão --%>
    <div class="mt-4 p-4 border border-secondary rounded">
        <h4>Informações da Conta</h4>
        <p>Usuário: <strong><%= session.getAttribute("usuarioLogado") %></strong></p>
        <%-- Você poderá adicionar mais informações do usuário aqui no futuro --%>
    </div>
</div>

<jsp:include page="navbar/rodape.jsp" />