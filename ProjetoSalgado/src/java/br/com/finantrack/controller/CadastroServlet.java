// no arquivo br/com/finantrack/controller/CadastroServlet.java
package br.com.finantrack.controller;

import br.com.finantrack.util.database; // Deve importar Database, e não DatabaseManager
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CadastroServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        try (Connection conn = database.getConnection()) {
            String sqlCheck = "SELECT id FROM usuarios WHERE email = ?";
            PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck);
            stmtCheck.setString(1, email);
            ResultSet rs = stmtCheck.executeQuery();

            if (rs.next()) {
                request.setAttribute("errorMessage", "Este e-mail já está cadastrado!");
                request.getRequestDispatcher("tela_cadastro.jsp").forward(request, response);
            } else {
                String sqlInsert = "INSERT INTO usuarios (nome, email, senha) VALUES (?, ?, ?)";
                PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert);
                stmtInsert.setString(1, nome);
                stmtInsert.setString(2, email);
                stmtInsert.setString(3, senha);

                int linhasAfetadas = stmtInsert.executeUpdate();
                if (linhasAfetadas > 0) {
                    response.sendRedirect("tela_login.jsp?success=true");
                } else {
                    request.setAttribute("errorMessage", "Ocorreu um erro ao cadastrar.");
                    request.getRequestDispatcher("tela_cadastro.jsp").forward(request, response);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Erro de banco de dados ao tentar cadastrar usuário.", e);
        }
    }
}