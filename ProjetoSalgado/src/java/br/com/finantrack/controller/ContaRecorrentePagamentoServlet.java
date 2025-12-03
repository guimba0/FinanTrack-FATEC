package br.com.finantrack.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@WebServlet("/conta-recorrente-pagar")
public class ContaRecorrentePagamentoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int contaId = Integer.parseInt(request.getParameter("id"));
            // Pega o mês atual para marcar como pago
            String mesAnoAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

            ContaRecorrenteDAO dao = new ContaRecorrenteDAO();
            dao.marcarComoPaga(contaId, mesAnoAtual);

        } catch (NumberFormatException | SQLException e) {
            throw new ServletException("Erro ao marcar conta como paga", e);
        }

        response.sendRedirect(request.getContextPath() + "/dashboard/dashboard.jsp");
    }
}