package br.com.finantrack.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet("/transacao-editar")
public class TransacaoEditServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String descricao = request.getParameter("descricao");
            double valor = Double.parseDouble(request.getParameter("valor"));
            LocalDate data = LocalDate.parse(request.getParameter("data"));
            String tipo = request.getParameter("tipo");
            String categoria = request.getParameter("categoria");
            // ****** NOVA LINHA AQUI ******
            boolean pago = request.getParameter("pago") != null;

            Integer usuarioId = (Integer) request.getSession().getAttribute("userId");
            if (usuarioId == null) {
                response.sendRedirect(request.getContextPath() + "/tela_login.jsp");
                return;
            }

            Transacao transacaoAtualizada = new Transacao();
            transacaoAtualizada.setId(id);
            transacaoAtualizada.setUsuarioId(usuarioId);
            transacaoAtualizada.setDescricao(descricao);
            transacaoAtualizada.setValor(valor);
            transacaoAtualizada.setData(data);
            transacaoAtualizada.setTipo(tipo);
            transacaoAtualizada.setCategoria(categoria);
            // ****** NOVA LINHA AQUI ******
            transacaoAtualizada.setPago(pago);

            TransacaoDAO dao = new TransacaoDAO();
            dao.atualizar(transacaoAtualizada);

        } catch (SQLException | NumberFormatException e) {
            throw new ServletException("Erro ao atualizar transação no banco de dados", e);
        }

        String mes = request.getParameter("mes");
        String ano = request.getParameter("ano");
        response.sendRedirect(request.getContextPath() + "/dashboard/detalhes_mes.jsp?mes=" + mes + "&ano=" + ano);
    }
}