package br.com.finantrack.controller;

import br.com.finantrack.util.database;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, nome, senha FROM usuarios WHERE email = ?")) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String senhaDoBanco = rs.getString("senha");

                // Compara a senha digitada com a senha do banco (ambas em texto puro)
                if (senha.equals(senhaDoBanco)) {
                    // Senha correta, cria a sessão
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", rs.getInt("id"));
                    session.setAttribute("usuarioLogado", rs.getString("nome"));
                    response.sendRedirect("dashboard/dashboard.jsp");
                } else {
                    // Senha incorreta
                    request.setAttribute("error", "Email ou senha inválidos.");
                    RequestDispatcher dispatcher = request.getRequestDispatcher("tela_login.jsp");
                    dispatcher.forward(request, response);
                }
            } else {
                // Usuário não encontrado
                request.setAttribute("error", "Email ou senha inválidos.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("tela_login.jsp");
                dispatcher.forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Erro de banco de dados", e);
        }
    }
}