<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bem-vindo ao FinanTrack</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap" rel="stylesheet">
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
            color: var(--cor-principal);
        }
        .container {
            max-width: 600px;
        }
        .welcome-box {
            background-color: white;
            padding: 40px;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }
        .btn-custom {
            background-color: var(--cor-principal);
            border-color: var(--cor-principal);
            color: white;
            padding: 12px 30px;
            font-weight: 600;
            border-radius: 50px;
            transition: all 0.3s ease;
        }
        .btn-custom:hover {
            background-color: #1a193c;
            border-color: #1a193c;
            transform: translateY(-3px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }
    </style>
</head>
<body>
    <div class="container text-center">
        <div class="welcome-box">
            <h1 class="display-4 fw-bold mb-3">? FinanTrack</h1>
            <p class="lead mb-4">Seu assistente financeiro pessoal.</p>
            <a class="btn btn-custom btn-lg" href="tela_login.jsp" role="button">
                Começar a Organizar
            </a>
        </div>
    </div>
</body>
</html>
