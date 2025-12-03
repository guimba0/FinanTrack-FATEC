package br.com.finantrack.controller;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "GrupoServlet", urlPatterns = {"/GrupoServlet"})
public class GrupoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String acao = request.getParameter("acao");
        
        // Recupera a sessão (Compatível com o LoginServlet restaurado)
        HttpSession session = request.getSession();
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuario");
        
        // Fallback de segurança: se o objeto usuario for nulo, tenta recuperar pelo ID antigo
        if (usuarioLogado == null && session.getAttribute("userId") != null) {
             usuarioLogado = new Usuario();
             usuarioLogado.setId((Integer) session.getAttribute("userId"));
        }

        if (usuarioLogado == null) {
            response.sendRedirect("tela_login.jsp");
            return;
        }

        try {
            if ("criar".equals(acao)) {
                // 1. Pega apenas o NOME (versão simples, sem tipo/emails)
                String nome = request.getParameter("nome");
                
                // 2. Chama o método SIMPLES do DAO (que restauramos)
                GrupoDAO grupoDAO = new GrupoDAO();
                grupoDAO.criarGrupo(nome, usuarioLogado.getId());
                
                // 3. Atualiza a sessão para o usuário ver o grupo sem precisar relogar
                // (Como voltamos para "um grupo por usuário", buscamos ele direto)
                Grupo novoGrupo = grupoDAO.buscarGrupoPorUsuario(usuarioLogado.getId());
                if (novoGrupo != null) {
                    session.setAttribute("idGrupo", novoGrupo.getId());
                    // Se você estiver usando a lista "meusGrupos" no cabeçalho, podemos atualizar ela também:
                    // List<Grupo> lista = new ArrayList<>();
                    // lista.add(novoGrupo);
                    // session.setAttribute("meusGrupos", lista);
                }

                // Redireciona para o Dashboard (fluxo padrão antigo)
                response.sendRedirect("dashboard/dashboard.jsp");
            
            } else {
                response.sendRedirect("dashboard/dashboard.jsp");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Erro ao criar grupo: " + e.getMessage());
        }
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { processRequest(req, resp); }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { processRequest(req, resp); }
}