<%@ page pageEncoding="UTF-8" %>
<%
    // Bloco de segurança: se o usuário não estiver logado, redireciona para o login.
    if (session.getAttribute("userName") == null) {
        response.sendRedirect("tela_login.jsp");
        return;
    }
    String userName = (String) session.getAttribute("userName");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard - FinanTrack</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="#">💰 FinanTrack</a>
            <div class="collapse navbar-collapse">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <span class="navbar-text">
                            Olá, <strong><%= userName %></strong>!
                        </span>
                    </li>
                    <li class="nav-item ms-3">
                        <a href="logout" class="btn btn-outline-danger btn-sm">Sair</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row">
            <div class="col-md-4">
                <h3>Nova Transação</h3>
                <div class="card">
                    <div class="card-body">
                        <form action="transacao" method="post">
                            <div class="mb-3">
                                <label for="descricao" class="form-label">Descrição</label>
                                <input type="text" class="form-control" name="descricao" required>
                            </div>
                            <div class="mb-3">
                                <label for="valor" class="form-label">Valor (R$)</label>
                                <input type="number" step="0.01" class="form-control" name="valor" required>
                            </div>
                            <div class="mb-3">
                                <label for="data" class="form-label">Data</label>
                                <input type="date" class="form-control" name="data" required>
                            </div>
                            <div class="mb-3">
                                <label for="tipo" class="form-label">Tipo</label>
                                <select class="form-select" name="tipo">
                                    <option value="saida">Saída (Despesa)</option>
                                    <option value="entrada">Entrada (Receita)</option>
                                </select>
                            </div>
                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary">Adicionar</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <div class="col-md-8">
                <h3>Minhas Transações</h3>
                <p class="text-muted">Suas últimas transações aparecerão aqui.</p>
                </div>
        </div>
    </div>
</body>
</html>