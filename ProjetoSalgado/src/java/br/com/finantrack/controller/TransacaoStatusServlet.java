package br.com.finantrack.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/transacao-status")
public class TransacaoStatusServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int transacaoId = Integer.parseInt(request.getParameter("id"));
            // O valor do checkbox vem como "on" se marcado, e nulo se desmarcado
            boolean pago = request.getParameter("pago") != null;

            TransacaoDAO dao = new TransacaoDAO();
            dao.atualizarStatus(transacaoId, pago);
            
        } catch (NumberFormatException | SQLException e) {
            throw new ServletException("Erro ao atualizar status da transação", e);
        }

        String mes = request.getParameter("mes");
        String ano = request.getParameter("ano");
        response.sendRedirect(request.getContextPath() + "/dashboard/detalhes_mes.jsp?mes=" + mes + "&ano=" + ano);
    }
}