<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Cadastro - FinanTrack</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container">
        <div class="row justify-content-center mt-5">
            <div class="col-md-5">
                <div class="card">
                    <div class="card-body">
                        <h3 class="card-title text-center">Crie sua Conta</h3>
                        <p class="text-center text-muted mb-4">É rápido e fácil.</p>
                        
                        <form action="cadastro" method="post">
                            <div class="mb-3">
                                <label for="nome" class="form-label">Nome Completo</label>
                                <input type="text" class="form-control" name="nome" required>
                            </div>
                            <div class="mb-3">
                                <label for="email" class="form-label">Email</label>
                                <input type="email" class="form-control" name="email" required>
                            </div>
                            <div class="mb-3">
                                <label for="senha" class="form-label">Senha</label>
                                <input type="password" class="form-control" name="senha" required>
                            </div>
                            
                            <% if (request.getAttribute("errorMessage") != null) { %>
                                <div class="alert alert-danger" role="alert">
                                    <%= request.getAttribute("errorMessage") %>
                                </div>
                            <% } %>
                             <% if (request.getAttribute("successMessage") != null) { %>
                                <div class="alert alert-success" role="alert">
                                    <%= request.getAttribute("successMessage") %>
                                </div>
                            <% } %>

                            <div class="d-grid">
                                <button type="submit" class="btn btn-success">Cadastrar</button>
                            </div>
                        </form>
                        <hr>
                        <div class="text-center">
                            <a href="tela_login.jsp">Já tem uma conta? Faça o login</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>