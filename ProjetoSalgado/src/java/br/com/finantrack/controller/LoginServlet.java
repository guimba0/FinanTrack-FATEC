// no arquivo br/com/finantrack/controller/LoginServlet.java
package br.com.finantrack.controller;

import br.com.finantrack.util.database; // Deve importar Database, e não DatabaseManager
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        // A forma correta e segura de usar a conexão
        try (Connection conn = database.getConnection()) {
            String sql = "SELECT id, nome FROM usuarios WHERE email = ? AND senha = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("userId", rs.getInt("id"));
                session.setAttribute("userName", rs.getString("nome"));
                response.sendRedirect("tela_loginfeito.jsp");
            } else {
                request.setAttribute("errorMessage", "Email ou senha inválidos!");
                request.getRequestDispatcher("tela_login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}