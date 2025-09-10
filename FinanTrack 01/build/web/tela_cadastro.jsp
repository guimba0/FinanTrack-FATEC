<%-- Local: web/tela_cadastro.jsp --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%-- Inclui o cabeçalho padrão com a navbar --%>
<jsp:include page="navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="Cadastro" />
</jsp:include>

<%-- Estilos para corrigir a cor do texto nos inputs e centralizar o conteúdo --%>
<style>
    .form-control:focus, .form-control {
        color: #fff !important;
        background-color: #2a2a2a !important;
    }
    .form-control::placeholder {
        color: #888 !important;
    }
    .cadastro-container {
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: calc(100vh - 140px); /* Ajuste de altura */
    }
</style>

<div class="cadastro-container">
    <div class="card bg-dark border-secondary" style="width: 100%; max-width: 450px;">
        <div class="card-body p-4">
            <div class="text-center mb-4">
                <i class="bi bi-person-plus-fill fs-1 text-primary"></i>
                <h1 class="h3 mb-3 fw-normal">Crie sua Conta</h1>
            </div>

            <%-- Exibe mensagem de erro de cadastro, se houver --%>
            <% String registerError = (String) request.getAttribute("error");
               if (registerError != null) { %>
            <div class="alert alert-danger" role="alert">
                <%= registerError %>
            </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/CadastroServlet" method="post">
                <div class="form-floating mb-3">
                    <input type="text" class="form-control" id="nome" name="nome" placeholder=" " required>
                    <label for="nome">Nome Completo</label>
                </div>
                <div class="form-floating mb-3">
                    <input type="email" class="form-control" id="email" name="email" placeholder=" " required>
                    <label for="email">Email</label>
                </div>
                <div class="form-floating mb-3">
                    <input type="password" class="form-control" id="senha" name="senha" placeholder=" " required>
                    <label for="senha">Senha</label>
                </div>
                <button class="w-100 btn btn-lg btn-primary" type="submit">Cadastrar</button>
            </form>

            <div class="text-center mt-4">
                <p class="mb-0">Já tem uma conta?</p>
                <a href="tela_login.jsp" class="btn btn-outline-light w-100 mt-2">Fazer Login</a>
            </div>
        </div>
    </div>
</div>

<%-- Inclui o rodapé padrão --%>
<jsp:include page="navbar/rodape.jsp" />