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

@WebServlet("/transacao")
public class TransacaoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("userId");

        if (usuarioId == null) {
            response.sendRedirect(request.getContextPath() + "/tela_login.jsp");
            return;
        }

        String descricao = request.getParameter("descricao");
        double valor = Double.parseDouble(request.getParameter("valor"));
        LocalDate data = LocalDate.parse(request.getParameter("data"));
        String tipo = request.getParameter("tipo");
        String categoria = request.getParameter("categoria");
        // ****** NOVA LINHA AQUI ******
        // Lê o status do checkbox. Se não for marcado, o valor é nulo.
        boolean pago = request.getParameter("pago") != null;

        if ("Outro".equals(categoria)) {
            categoria = request.getParameter("categoriaOutro");
        }

        Transacao novaTransacao = new Transacao();
        novaTransacao.setUsuarioId(usuarioId);
        novaTransacao.setDescricao(descricao);
        novaTransacao.setValor(valor);
        novaTransacao.setData(data);
        novaTransacao.setTipo(tipo);
        novaTransacao.setCategoria(categoria);
        // ****** NOVA LINHA AQUI ******
        novaTransacao.setPago(pago); // Define o status no objeto

        try {
            TransacaoDAO dao = new TransacaoDAO();
            dao.inserir(novaTransacao);
        } catch (SQLException e) {
            throw new ServletException("Erro ao inserir transação no banco de dados", e);
        }

        String mes = request.getParameter("mes");
        String ano = request.getParameter("ano");
        response.sendRedirect(request.getContextPath() + "/dashboard/detalhes_mes.jsp?mes=" + mes + "&ano=" + ano);
    }
}