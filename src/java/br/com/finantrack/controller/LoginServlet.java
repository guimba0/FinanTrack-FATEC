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
import java.util.ArrayList;
import java.util.List;

// Imports das classes do projeto
import br.com.finantrack.controller.Usuario;
import br.com.finantrack.controller.Grupo;
import br.com.finantrack.controller.GrupoDAO;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Define codificação para evitar erros de acentuação
        request.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        // SQL atualizado para buscar todos os dados necessários do Objeto Usuario
        String sql = "SELECT id, nome, email, senha, salario FROM usuarios WHERE email = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String senhaDoBanco = rs.getString("senha");

                // Compara a senha digitada com a senha do banco
                if (senha.equals(senhaDoBanco)) {
                    HttpSession session = request.getSession();

                    // --- 1. CRIAÇÃO DO OBJETO USUÁRIO ---
                    // Criamos o objeto completo para usar em outras partes do sistema
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSalario(rs.getDouble("salario"));
                    
                    // Salva na sessão com o nome "usuario" (Objeto completo)
                    session.setAttribute("usuario", usuario);
                    
                    // (Opcional) Mantém compatibilidade com códigos antigos que usam esses nomes soltos
                    session.setAttribute("userId", usuario.getId());
                    session.setAttribute("usuarioLogado", usuario.getNome());

                    // --- 2. CARREGAR GRUPOS PARA O MENU LATERAL ---
                    try {
                        GrupoDAO grupoDao = new GrupoDAO();
                        
                        // Busca o grupo principal deste usuário (o método que criamos no GrupoDAO)
                        Grupo grupo = grupoDao.buscarGrupoPorUsuario(usuario.getId());
                        
                        // O menu lateral (cabecalho.jsp) geralmente espera uma LISTA para fazer o "forEach"
                        // Então criamos uma lista vazia e adicionamos o grupo nela se ele existir.
                        List<Grupo> listaGrupos = new ArrayList<>();
                        if (grupo != null) {
                            listaGrupos.add(grupo);
                            // Opcional: Salvar também o grupo atual individualmente se precisar
                            session.setAttribute("grupoAtual", grupo); 
                        }
                        
                        // Salva a lista na sessão para o menu lateral exibir
                        session.setAttribute("meusGrupos", listaGrupos);
                        
                    } catch (SQLException e) {
                        // Se der erro ao carregar grupos, apenas imprime no log e segue o login normal
                        System.out.println("Erro ao carregar grupos no login: " + e.getMessage());
                        e.printStackTrace();
                    }

                    // Login com sucesso -> Redireciona para o Dashboard
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