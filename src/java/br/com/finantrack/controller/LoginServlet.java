package br.com.finantrack.controller;

import br.com.finantrack.util.DatabaseConnection; // Importando a nova classe de conexão
import br.com.finantrack.controller.Usuario;
import br.com.finantrack.controller.Grupo;
import br.com.finantrack.controller.GrupoDAO;

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
import java.util.ArrayList;
import java.util.List;

/*  Controller de Autenticação.
     Verifica credenciais e carrega dados iniciais do usuário na sessão.
 */

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        
        // SQL para buscar usuário (senha em texto puro por enquanto)
        String sql = "SELECT id, nome, email, senha, salario FROM usuarios WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && senha.equals(rs.getString("senha"))) {
                // --- LOGIN SUCESSO ---
                HttpSession session = request.getSession();
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSalario(rs.getDouble("salario"));
                
                // Salva usuário na sessão
                session.setAttribute("usuario", usuario);
                session.setAttribute("userId", usuario.getId()); // Compatibilidade legada
                session.setAttribute("usuarioLogado", usuario.getNome()); // Compatibilidade legada

                // Tenta carregar o grupo principal para atalhos do menu
                try {
                    GrupoDAO grupoDao = new GrupoDAO();
                    Grupo grupo = grupoDao.buscarGrupoPorUsuario(usuario.getId());
                    List<Grupo> listaGrupos = new ArrayList<>();
                    if (grupo != null) {
                        listaGrupos.add(grupo);
                        session.setAttribute("grupoAtual", grupo); 
                    }
                    session.setAttribute("meusGrupos", listaGrupos);
                } catch (Exception e) { 
                    System.out.println("Aviso: Erro ao carregar grupos no login: " + e.getMessage());
                }

                response.sendRedirect("dashboard/dashboard.jsp");
            } else {
                // --- LOGIN FALHA ---
                request.setAttribute("error", "Email ou senha inválidos.");
                request.getRequestDispatcher("tela_login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Erro de banco de dados no login.", e);
        }
    }
}