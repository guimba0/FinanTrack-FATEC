package br.com.finantrack.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/pagamento-recorrente")
public class PagamentoRecorrenteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // O ID aqui é o da conta recorrente (que passamos como negativo no JSP)
            // Por isso, usamos Math.abs() para garantir que seja positivo
            int contaRecorrenteId = Math.abs(Integer.parseInt(request.getParameter("id")));
            int mes = Integer.parseInt(request.getParameter("mes"));
            int ano = Integer.parseInt(request.getParameter("ano"));
            
            // Formata o mês/ano para o padrão "AAAA-MM" que salvamos no banco
            String mesAno = String.format("%d-%02d", ano, mes);

            ContaRecorrenteDAO dao = new ContaRecorrenteDAO();
            dao.marcarComoPaga(contaRecorrenteId, mesAno);
            
        } catch (NumberFormatException | SQLException e) {
            throw new ServletException("Erro ao marcar conta recorrente como paga", e);
        }

        String mes = request.getParameter("mes");
        String ano = request.getParameter("ano");
        response.sendRedirect(request.getContextPath() + "/dashboard/detalhes_mes.jsp?mes=" + mes + "&ano=" + ano);
    }
}