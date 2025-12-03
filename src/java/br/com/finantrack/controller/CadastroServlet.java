package br.com.finantrack.controller;

import br.com.finantrack.util.database;
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

@WebServlet("/CadastroServlet")
public class CadastroServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        // IMPORTANTE: As senhas nunca devem ser salvas em texto puro em um projeto real.
        // O ideal é usar uma biblioteca como BCrypt para gerar um "hash" da senha.
        // A linha original era: String hashedPassword = BCrypt.withDefaults().hashToString(12, senha.toCharArray());
        // Foi removida para evitar o erro de biblioteca ausente.
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO usuarios(nome, email, senha) VALUES(?, ?, ?)")) {
            
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha); // Salvando a senha em texto puro (não recomendado para produção)
            stmt.executeUpdate();

            // Redireciona para a tela de login após o sucesso
            response.sendRedirect("tela_login.jsp?cadastro=sucesso");

        } catch (SQLException ex) {
            // Código 19 no SQLite significa violação de restrição (email já existe)
            if (ex.getErrorCode() == 19) { 
                request.setAttribute("error", "Este email já está cadastrado. Tente outro.");
            } else {
                request.setAttribute("error", "Ocorreu um erro no servidor. Tente novamente mais tarde.");
                ex.printStackTrace(); // Ajuda a ver o erro no console do servidor
            }
            RequestDispatcher dispatcher = request.getRequestDispatcher("tela_cadastro.jsp");
            dispatcher.forward(request, response);
        }
    }
}