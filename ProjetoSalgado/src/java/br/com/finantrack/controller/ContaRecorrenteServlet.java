// Local: src/java/br/com/finantrack/controller/ContaRecorrenteServlet.java
package br.com.finantrack.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/conta-recorrente") // Define a URL do servlet
public class ContaRecorrenteServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int usuarioId = (Integer) session.getAttribute("userId");

        String descricao = request.getParameter("descricao");
        double valor = Double.parseDouble(request.getParameter("valor"));
        String categoria = request.getParameter("categoria");
        int diaVencimento = Integer.parseInt(request.getParameter("diaVencimento"));
        LocalDate dataInicio = LocalDate.parse(request.getParameter("dataInicio"));
        String dataFimStr = request.getParameter("dataFim");
        LocalDate dataFim = (dataFimStr != null && !dataFimStr.isEmpty()) ? LocalDate.parse(dataFimStr) : null;

        ContaRecorrente conta = new ContaRecorrente();
        conta.setUsuarioId(usuarioId);
        conta.setDescricao(descricao);
        conta.setValor(valor);
        conta.setCategoria(categoria);
        conta.setDiaVencimento(diaVencimento);
        conta.setDataInicio(dataInicio);
        conta.setDataFim(dataFim);

        try {
            ContaRecorrenteDAO dao = new ContaRecorrenteDAO();
            dao.inserir(conta);
        } catch (SQLException e) {
            throw new ServletException("Erro ao inserir conta recorrente", e);
        }

        response.sendRedirect("dashboard/dashboard.jsp");
    }
}