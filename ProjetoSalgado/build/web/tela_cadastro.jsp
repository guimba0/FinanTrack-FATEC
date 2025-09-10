<jsp:include page="navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="Cadastro - FinanTrack" />
</jsp:include>

<%-- CONTEÚDO DA PÁGINA DE CADASTRO --%>
<div class="row justify-content-center">
    <div class="col-md-6 col-lg-5">
        <div class="card bg-transparent border-secondary mt-4">
            <div class="card-body">
                <h2 class="text-center mb-4">Criar Nova Conta</h2>

                <%-- LINHA CORRIGIDA ABAIXO --%>
                <form action="${pageContext.request.contextPath}/CadastroServlet" method="post">

                    <div class="mb-3">
                        <label for="nome" class="form-label">Nome:</label>
                        <input type="text" class="form-control" id="nome" name="nome" required>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email:</label>
                        <input type="email" class="form-control" id="email" name="email" required>
                    </div>
                    <div class="mb-3">
                        <label for="senha" class="form-label">Senha:</label>
                        <input type="password" class="form-control" id="senha" name="senha" required>
                    </div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-custom">Cadastrar</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="navbar/rodape.jsp" />