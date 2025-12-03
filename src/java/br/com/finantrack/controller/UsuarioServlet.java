package br.com.finantrack.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/UsuarioServlet") // A URL que o formulário vai chamar
public class UsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("userId");

        if (usuarioId == null) {
            response.sendRedirect(request.getContextPath() + "/tela_login.jsp");
            return;
        }

        // Pega os dados enviados pelo formulário
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        double salario = Double.parseDouble(request.getParameter("salario"));

        // Cria um objeto Usuario com os novos dados
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSalario(salario);

        try {
            UsuarioDAO dao = new UsuarioDAO();
            dao.atualizar(usuario);
            
            // Atualiza o nome do usuário na sessão para refletir a mudança na navbar
            session.setAttribute("usuarioLogado", nome);
            
            // Adiciona uma mensagem de sucesso para exibir na página
            session.setAttribute("successMessage", "Dados atualizados com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Erro ao atualizar os dados no banco de dados.");
        }

        // Redireciona o usuário de volta para a mesma página
        response.sendRedirect(request.getContextPath() + "/dashboard/dados_usuario.jsp");
    }
}