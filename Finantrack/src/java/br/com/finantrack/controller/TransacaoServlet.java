// Local: src/java/br/com/finantrack/controller/TransacaoServlet.java
package br.com.finantrack.controller;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class TransacaoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        int usuarioId = (Integer) session.getAttribute("userId");
        
        String descricao = request.getParameter("descricao");
        double valor = Double.parseDouble(request.getParameter("valor"));
        String data = request.getParameter("data");
        String tipo = request.getParameter("tipo");
        String categoria = request.getParameter("categoria"); // NOVO CAMPO
        
        // Lógica para categoria "Outro"
        if ("Outro".equals(categoria)) {
            categoria = request.getParameter("categoriaOutro");
        }

        String mes = request.getParameter("mes");
        String ano = request.getParameter("ano");

        try {
            TransacaoDAO dao = new TransacaoDAO();
            // Passando a categoria para o método de inserção
            dao.inserir(usuarioId, descricao, valor, data, tipo, categoria);
        } catch (SQLException e) {
            throw new ServletException("Erro ao inserir transação no banco de dados", e);
        }

        response.sendRedirect("dashboard/detalhes_mes.jsp?mes=" + mes + "&ano=" + ano);
    }
}