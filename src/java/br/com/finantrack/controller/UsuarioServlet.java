package br.com.finantrack.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/* Controller para atualização de dados cadastrais (Perfil) */

@WebServlet("/UsuarioServlet")
public class UsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("userId");

        if (usuarioId == null) {
            response.sendRedirect(request.getContextPath() + "/tela_login.jsp");
            return;
        }

        // Coleta dados
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        double salario;
        try {
            salario = Double.parseDouble(request.getParameter("salario").replace(",", "."));
        } catch (Exception e) { salario = 0.0; }

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSalario(salario);

        try {
            UsuarioDAO dao = new UsuarioDAO();
            dao.atualizar(usuario);
            
            // Atualiza sessão para refletir mudanças imediatamente
            session.setAttribute("usuarioLogado", nome);
            session.setAttribute("usuario", usuario);
            session.setAttribute("successMessage", "Perfil atualizado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Erro ao atualizar dados.");
        }
        
        // Retorna para a aba de perfil no modal/página
        response.sendRedirect(request.getContextPath() + "/navbar/dados_usuario.jsp");
    }
}