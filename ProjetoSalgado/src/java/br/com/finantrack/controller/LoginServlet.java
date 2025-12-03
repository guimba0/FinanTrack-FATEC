package br.com.finantrack.controller;

import br.com.finantrack.util.database;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String sql = "SELECT id, nome, email, senha, salario FROM usuarios WHERE email = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String senhaDoBanco = rs.getString("senha");
                if (senha.equals(senhaDoBanco)) {
                    HttpSession session = request.getSession();

                    // Salva as variáveis clássicas que o cabeçalho usa
                    session.setAttribute("usuarioLogado", rs.getString("nome"));
                    session.setAttribute("userId", rs.getInt("id"));

                    // Cria objeto usuario para compatibilidade futura
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSalario(rs.getDouble("salario"));
                    session.setAttribute("usuario", usuario);

                    // Busca o grupo (versão simples, sem erro de SQL)
                    try {
                        GrupoDAO grupoDao = new GrupoDAO();
                        Grupo grupo = grupoDao.buscarGrupoPorUsuario(usuario.getId());
                        if (grupo != null) {
                            session.setAttribute("idGrupo", grupo.getId()); // Exemplo
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    response.sendRedirect("dashboard/dashboard.jsp");
                } else {
                    erroLogin(request, response);
                }
            } else {
                erroLogin(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void erroLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("error", "Email ou senha inválidos.");
        RequestDispatcher dispatcher = request.getRequestDispatcher("tela_login.jsp");
        dispatcher.forward(request, response);
    }
}