<%
    // Prote��o da p�gina: s� permite acesso se o usu�rio estiver logado.
    if (session.getAttribute("usuarioLogado") == null) {
        response.sendRedirect("../tela_login.jsp");
        return;
    }
%>

<%-- Inclui o cabe�alho padr�o (com menu de navega��o) --%>
<jsp:include page="../navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="An�lise Gr�fica" />
</jsp:include>

<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap">
        <div>
            <h1 class="display-5">An�lise Gr�fica</h1>
            <p class="lead">Visualize suas finan�as de forma mais clara.</p>
        </div>
        </a>
    </div>

    <%-- Espa�os reservados para os futuros gr�ficos --%>
    <div class="row">
        <div class="col-lg-6 mb-4">
            <div class="card">
                <div class="card-header">
                    Despesas por Categoria (�ltimos 30 dias)
                </div>
                <div class="card-body">
                    <%-- O gr�fico de pizza/rosca vir� aqui --%>
                    <p class="text-center text-muted">Gr�fico ser� implementado aqui.</p>
                </div>
            </div>
        </div>

        <div class="col-lg-6 mb-4">
            <div class="card">
                <div class="card-header">
                    Receitas vs. Despesas (�ltimos 6 meses)
                </div>
                <div class="card-body">
                    <%-- O gr�fico de barras vir� aqui --%>
                    <p class="text-center text-muted">Gr�fico ser� implementado aqui.</p>
                </div>
            </div>
        </div>
    </div>
    
</div>

<%-- Inclui o rodap� padr�o --%>
<jsp:include page="../navbar/rodape.jsp" />