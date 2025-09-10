<jsp:include page="navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="Login - FinanTrack" />
</jsp:include>

<%-- CONTEÚDO DA PÁGINA DE LOGIN --%>
<div class="row justify-content-center">
    <div class="col-md-6 col-lg-4">
        <div class="card bg-transparent border-secondary mt-4">
            <div class="card-body">
                <h2 class="text-center mb-4">Acessar Conta</h2>
                
                <%-- LINHA CORRIGIDA ABAIXO --%>
                <form action="${pageContext.request.contextPath}/LoginServlet" method="post">

                    <div class="mb-3">
                        <label for="email" class="form-label">Email:</label>
                        <input type="email" class="form-control" id="email" name="email" required>
                    </div>
                    <div class="mb-3">
                        <label for="senha" class="form-label">Senha:</label>
                        <input type="password" class="form-control" id="senha" name="senha" required>
                    </div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-custom">Entrar</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="navbar/rodape.jsp" />