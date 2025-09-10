<%-- ATENÇÃO: O caminho para os arquivos mudou --%>
<jsp:include page="navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="Bem-vindo ao FinanTrack" />
</jsp:include>

<%-- Conteúdo da sua página --%>
<div class="text-center">
    <div class="welcome-box">
        <h1 class="display-4 fw-bold mb-3">FinanTrack</h1>
        <p class="lead mb-4">Seu assistente financeiro pessoal.</p>
        <a class="btn btn-custom btn-lg" href="tela_login.jsp" role="button">
            Começar a Organizar
        </a>
    </div>
</div>

<%-- ATENÇÃO: O caminho para os arquivos mudou --%>
<jsp:include page="navbar/rodape.jsp" />