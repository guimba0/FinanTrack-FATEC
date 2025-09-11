<%-- Local: web/tela_login.jsp --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%-- Inclui o cabeçalho padrão com a navbar --%>
<jsp:include page="navbar/cabecalho.jsp">
    <jsp:param name="titulo" value="Login" />
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
    .login-container {
        display: flex;
        justify-content: center;
        align-items: center;
        /* Remove a altura extra do padding do main para um melhor alinhamento */
        min-height: calc(100vh - 140px); /* 100% da altura da tela menos o padding do cabeçalho/rodapé */
    }
</style>

<div class="login-container">
    <div class="card bg-dark border-secondary" style="width: 100%; max-width: 400px;">
        <div class="card-body p-4">
            <div class="text-center mb-4">
                <i class="bi bi-bar-chart-line-fill fs-1 text-primary"></i>
                <h1 class="h3 mb-3 fw-normal">Login FinanTrack</h1>
            </div>

            <%-- Exibe mensagem de erro, se houver --%>
            <% String error = (String) request.getAttribute("error");
               if (error != null) { %>
            <div class="alert alert-danger" role="alert">
                <%= error %>
            </div>
            <% } %>
            
            <form action="LoginServlet" method="post">
                <div class="form-floating mb-3">
                    <input type="email" class="form-control" id="floatingInput" name="email" placeholder=" " required>
                    <label for="floatingInput">Email</label>
                </div>
                <div class="form-floating mb-3">
                    <input type="password" class="form-control" id="floatingPassword" name="senha" placeholder=" " required>
                    <label for="floatingPassword">Senha</label>
                </div>
                <button class="w-100 btn btn-lg btn-primary" type="submit">Entrar</button>
            </form>
            
            <div class="text-center mt-4">
                <p class="mb-0">Ainda não tem uma conta?</p>
                <a href="tela_cadastro.jsp" class="btn btn-outline-light w-100 mt-2">Cadastre-se</a>
            </div>
        </div>
    </div>
</div>

<%-- Inclui o rodapé padrão --%>
<jsp:include page="navbar/rodape.jsp" />