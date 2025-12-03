package br.com.finantrack.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet("/conta-recorrente-editar")
public class ContaRecorrenteEditServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("userId");

        if (usuarioId == null) {
            response.sendRedirect(request.getContextPath() + "/tela_login.jsp");
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String descricao = request.getParameter("descricao");
            double valor = Double.parseDouble(request.getParameter("valor"));
            String categoria = request.getParameter("categoria");
            String tipoPagamento = request.getParameter("tipoPagamento");
            String diaVencimentoStr = request.getParameter("diaVencimento");
            LocalDate dataInicio = LocalDate.parse(request.getParameter("dataInicio"));
            String dataFimStr = request.getParameter("dataFim");

            ContaRecorrente conta = new ContaRecorrente();
            conta.setId(id);
            conta.setUsuarioId(usuarioId);
            conta.setDescricao(descricao);
            conta.setValor(valor);
            conta.setCategoria(categoria);
            conta.setTipoPagamento(tipoPagamento);
            conta.setDataInicio(dataInicio);

            Integer diaVencimento = null;
            if (diaVencimentoStr != null && !diaVencimentoStr.trim().isEmpty()) {
                diaVencimento = Integer.parseInt(diaVencimentoStr);
            }
            conta.setDiaVencimento(diaVencimento);

            LocalDate dataFim = null;
            if (dataFimStr != null && !dataFimStr.trim().isEmpty()) {
                dataFim = LocalDate.parse(dataFimStr);
            }
            conta.setDataFim(dataFim);

            ContaRecorrenteDAO dao = new ContaRecorrenteDAO();
            dao.atualizar(conta);
            
            session.setAttribute("formSuccess", "Conta recorrente atualizada com sucesso!");

        } catch (SQLException | NumberFormatException e) {
            session.setAttribute("formError", "Erro ao atualizar a conta recorrente.");
            throw new ServletException("Erro ao atualizar conta recorrente", e);
        }

        response.sendRedirect(request.getContextPath() + "/dashboard/dashboard.jsp");
    }
}