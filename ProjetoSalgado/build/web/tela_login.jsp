<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - FinanTrack</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <style>
        :root {
            --cor-principal: #2B2A4C;
            --cor-secundaria: #B31312;
            --cor-acento: #EA906C;
            --cor-fundo: #EEE2DE;
        }
        body {
            font-family: 'Poppins', sans-serif;
            background-color: var(--cor-fundo);
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }
        .card-title {
            color: var(--cor-principal);
            font-weight: 600;
        }
        .form-control {
            border-radius: 8px;
            border: 1px solid #ced4da;
            padding: 10px;
        }
        .form-control:focus {
            border-color: var(--cor-acento);
            box-shadow: 0 0 0 0.25rem rgba(234, 144, 108, 0.25);
        }
        .btn-custom {
            background-color: var(--cor-principal);
            border-color: var(--cor-principal);
            color: white;
            font-weight: 600;
            padding: 12px;
            border-radius: 8px;
            transition: background-color 0.3s ease;
        }
        .btn-custom:hover {
            background-color: #1a193c;
            border-color: #1a193c;
        }
        .link-custom {
            color: var(--cor-acento);
            text-decoration: none;
            font-weight: 600;
        }
        .link-custom:hover {
            color: var(--cor-secundaria);
        }
    </style>
</head>
<body>
    <div class="col-md-4">
        <div class="card p-3">
            <div class="card-body">
                <h3 class="card-title text-center mb-4">Acessar FinanTrack</h3>
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
                     <% if ("true".equals(request.getParameter("success"))) { %>
                        <div class="alert alert-success" role="alert">
                           Cadastro realizado com sucesso! Faça o login.
                        </div>
                    <% } %>
                    <div class="d-grid mt-4">
                        <button type="submit" class="btn btn-custom">Entrar</button>
                    </div>
                </form>
                <div class="text-center mt-3">
                    <p class="mb-0">Não tem uma conta? <a href="tela_cadastro.jsp" class="link-custom">Cadastre-se</a></p>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
