<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastro - FinanTrack</title>
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
            --cor-sucesso: #54B435;
        }
        body {
            font-family: 'Poppins', sans-serif;
            background-color: var(--cor-fundo);
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 20px 0;
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
        .btn-custom-success {
            background-color: var(--cor-sucesso);
            border-color: var(--cor-sucesso);
            color: white;
            font-weight: 600;
            padding: 12px;
            border-radius: 8px;
            transition: background-color 0.3s ease;
        }
        .btn-custom-success:hover {
            background-color: #379237;
            border-color: #379237;
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
    <div class="col-md-5">
        <div class="card p-3">
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
                    <div class="d-grid mt-4">
                        <button type="submit" class="btn btn-custom-success">Cadastrar</button>
                    </div>
                </form>
                <div class="text-center mt-3">
                    <p class="mb-0">Já tem uma conta? <a href="tela_login.jsp" class="link-custom">Faça o login</a></p>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
