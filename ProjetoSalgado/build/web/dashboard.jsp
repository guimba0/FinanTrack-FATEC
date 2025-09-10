<%-- Prote��o da p�gina: verifica se o usu�rio est� na sess�o --%>
<%
    if (session.getAttribute("usuarioLogado") == null) {
        response.sendRedirect("tela_login.jsp");
        return; // Impede o resto da p�gina de ser processado
    }
%>

<jsp:include page="navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="Meu Dashboard" />
</jsp:include>

<%-- CONTE�DO DA P�GINA DE DASHBOARD --%>
<div class="text-center">
    <h1 class="display-5">Bem-vindo(a) ao seu Dashboard!</h1>
    <p class="lead">Aqui voc� poder� gerenciar suas finan�as.</p>
    
    <%-- Exemplo de como voc� pode pegar o nome do usu�rio da sess�o --%>
    <div class="mt-4 p-4 border border-secondary rounded">
        <h4>Informa��es da Conta</h4>
        <p>Usu�rio: <strong><%= session.getAttribute("usuarioLogado") %></strong></p>
        <%-- Voc� poder� adicionar mais informa��es do usu�rio aqui no futuro --%>
    </div>
</div>

<jsp:include page="navbar/rodape.jsp" />