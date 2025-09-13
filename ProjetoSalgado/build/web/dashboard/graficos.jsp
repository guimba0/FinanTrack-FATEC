<%
    // Proteção da página: só permite acesso se o usuário estiver logado.
    if (session.getAttribute("usuarioLogado") == null) {
        response.sendRedirect("../tela_login.jsp");
        return;
    }
%>

<%-- Inclui o cabeçalho padrão (com menu de navegação) --%>
<jsp:include page="../navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="Análise Gráfica" />
</jsp:include>

<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap">
        <div>
            <h1 class="display-5">Análise Gráfica</h1>
            <p class="lead">Visualize suas finanças de forma mais clara.</p>
        </div>
        </a>
    </div>

    <%-- Espaços reservados para os futuros gráficos --%>
    <div class="row">
        <div class="col-lg-6 mb-4">
            <div class="card">
                <div class="card-header">
                    Despesas por Categoria (Últimos 30 dias)
                </div>
                <div class="card-body">
                    <%-- O gráfico de pizza/rosca virá aqui --%>
                    <p class="text-center text-muted">Gráfico será implementado aqui.</p>
                </div>
            </div>
        </div>

        <div class="col-lg-6 mb-4">
            <div class="card">
                <div class="card-header">
                    Receitas vs. Despesas (Últimos 6 meses)
                </div>
                <div class="card-body">
                    <%-- O gráfico de barras virá aqui --%>
                    <p class="text-center text-muted">Gráfico será implementado aqui.</p>
                </div>
            </div>
        </div>
    </div>
    
</div>

<%-- Inclui o rodapé padrão --%>
<jsp:include page="../navbar/rodape.jsp" />