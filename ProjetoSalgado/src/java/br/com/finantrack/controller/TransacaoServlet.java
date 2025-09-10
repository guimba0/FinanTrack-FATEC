package br.com.finantrack.controller;

import br.com.finantrack.util.database;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class TransacaoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        // Pega o ID do usuário que está logado na sessão
        Integer idUsuario = (Integer) session.getAttribute("userId");

        // Se por algum motivo não houver usuário logado, não faz nada e redireciona para o login
        if (idUsuario == null) {
            response.sendRedirect("tela_login.jsp");
            return;
        }

        // Pega os dados do formulário
        String descricao = request.getParameter("descricao");
        double valor = Double.parseDouble(request.getParameter("valor"));
        String data = request.getParameter("data");
        String tipo = request.getParameter("tipo");

        String sql = "INSERT INTO transacoes (descricao, valor, data, tipo, id_usuario) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, descricao);
            stmt.setDouble(2, valor);
            stmt.setString(3, data);
            stmt.setString(4, tipo);
            stmt.setInt(5, idUsuario);

            stmt.executeUpdate();

            // Após inserir, redireciona de volta para o dashboard
            response.sendRedirect("dashboard.jsp");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Erro de banco de dados ao salvar transação.", e);
        }
    }
}