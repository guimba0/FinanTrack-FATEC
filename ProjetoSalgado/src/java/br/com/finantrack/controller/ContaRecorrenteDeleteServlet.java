package br.com.finantrack.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet; // Verifique se esta importação está correta
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// A anotação abaixo é crucial e deve ser exatamente esta.
@WebServlet("/conta-recorrente-deletar")
public class ContaRecorrenteDeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int contaId = Integer.parseInt(request.getParameter("id"));

            ContaRecorrenteDAO dao = new ContaRecorrenteDAO();
            dao.excluir(contaId);
            
            // Adiciona uma mensagem de sucesso para ser exibida no dashboard
            request.getSession().setAttribute("formSuccess", "Conta recorrente excluída com sucesso!");

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("formError", "ID da conta inválido.");
            e.printStackTrace();
        } catch (SQLException e) {
            request.getSession().setAttribute("formError", "Erro ao excluir a conta do banco de dados.");
            // Lançar a exceção é importante para o log do servidor
            throw new ServletException("Erro de SQL ao excluir conta recorrente", e);
        }

        // Redireciona de volta para o dashboard em qualquer cenário
        response.sendRedirect(request.getContextPath() + "/dashboard/dashboard.jsp");
    }
}