<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - FinanTrack</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container">
        <div class="row justify-content-center mt-5">
            <div class="col-md-4">
                <div class="card">
                    <div class="card-body">
                        <h3 class="card-title text-center">Login</h3>
                        <form action="login" method="post">
                            <div class="mb-3">
                                <label for="email" class="form-label">Email</label>
                                <input type="email" class="form-control" name="email" value="teste@email.com" required>
                            </div>
                            <div class="mb-3">
                                <label for="senha" class="form-label">Senha</label>
                                <input type="password" class="form-control" name="senha" value="123" required>
                            </div>
                            <% if (request.getAttribute("errorMessage") != null) { %>
                                <div class="alert alert-danger" role="alert">
                                    <%= request.getAttribute("errorMessage") %>
                                </div>
                            <% } %>
                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary">Entrar</button>
                            </div>
                        </form>
                            </form>
                        <hr>
                        <div class="text-center">
                            <p>Não tem uma conta? <a href="tela_cadastro.jsp">Cadastre-se</a></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>