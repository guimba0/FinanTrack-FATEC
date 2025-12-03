package br.com.finantrack.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/transacao-deletar")
public class TransacaoDeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Pega o ID da transação a ser excluída a partir do formulário
        int transacaoId = Integer.parseInt(request.getParameter("id"));

        try {
            // Instancia o DAO e chama o novo método para excluir
            TransacaoDAO dao = new TransacaoDAO();
            dao.excluir(transacaoId);
        } catch (SQLException e) {
            // Em caso de erro, lança uma exceção
            throw new ServletException("Erro ao excluir transação do banco de dados", e);
        }

        // Redireciona de volta para a mesma página de detalhes do mês
        String mes = request.getParameter("mes");
        String ano = request.getParameter("ano");
        response.sendRedirect(request.getContextPath() + "/dashboard/detalhes_mes.jsp?mes=" + mes + "&ano=" + ano);
    }
}