package br.com.finantrack.controller;

import br.com.finantrack.util.DatabaseConnection;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*Controller de Registro de Usuários. */

@WebServlet("/CadastroServlet")
public class CadastroServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO usuarios(nome, email, senha) VALUES(?, ?, ?)")) {
            
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.executeUpdate();
            
            // Redireciona para login com flag de sucesso (exibe mensagem verde)
            response.sendRedirect("tela_login.jsp?cadastro=sucesso");

        } catch (SQLException ex) {
            // Tratamento de erro de e-mail duplicado (Código 19 no SQLite)
            if (ex.getErrorCode() == 19) {
                request.setAttribute("error", "Este email já está cadastrado. Tente outro.");
            } else {
                request.setAttribute("error", "Ocorreu um erro no servidor.");
                ex.printStackTrace();
            }
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("tela_cadastro.jsp");
            dispatcher.forward(request, response);
        }
    }
}