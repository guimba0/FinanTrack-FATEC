package br.com.finantrack.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/TransacaoStatusServlet")
public class TransacaoStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Pega os parâmetros
            int id = Integer.parseInt(request.getParameter("id"));
            boolean pago = Boolean.parseBoolean(request.getParameter("pago"));
            
            // Parâmetros para redirecionamento (manter a tela onde estava)
            String mes = request.getParameter("mes");
            String ano = request.getParameter("ano");

            if (id < 0) {
                // --- É UMA CONTA RECORRENTE (ID Negativo) ---
                int idReal = Math.abs(id);
                ContaRecorrenteDAO dao = new ContaRecorrenteDAO();
                
                if (pago) {
                    // Se marcou como pago, salvamos "AAAA-MM" no banco
                    String mesAno = String.format("%s-%02d", ano, Integer.parseInt(mes));
                    dao.marcarComoPaga(idReal, mesAno);
                } else {
                    // Se desmarcou, limpamos o campo (null)
                    dao.marcarComoPaga(idReal, null);
                }
                
            } else {
                // --- É UMA TRANSAÇÃO NORMAL (ID Positivo) ---
                TransacaoDAO dao = new TransacaoDAO();
                dao.atualizarStatus(id, pago);
            }

            // Redireciona de volta para Detalhes do Mês
            response.sendRedirect(request.getContextPath() + "/dashboard/detalhes_mes.jsp?mes=" + mes + "&ano=" + ano);

        } catch (Exception e) {
            throw new ServletException("Erro ao atualizar status.", e);
        }
    }
}