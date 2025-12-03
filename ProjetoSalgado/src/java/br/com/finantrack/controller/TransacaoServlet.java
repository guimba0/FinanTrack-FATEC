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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Usuario u = (Usuario) session.getAttribute("usuario");
        // ... verificações de login ... (mantenha seu código seguro)

        // Dados comuns
        String descricao = request.getParameter("descricao");
        double valor = Double.parseDouble(request.getParameter("valor").replace(",", "."));
        LocalDate data = LocalDate.parse(request.getParameter("data"));
        String tipo = request.getParameter("tipo");
        String categoria = "Outro".equals(request.getParameter("categoria")) ? request.getParameter("categoriaOutro") : request.getParameter("categoria");
        
        boolean isRecorrente = request.getParameter("recorrente") != null;

        try {
            if (isRecorrente) {
                // CONTA RECORRENTE
                ContaRecorrente conta = new ContaRecorrente();
                conta.setUsuarioId(u.getId());
                conta.setDescricao(descricao);
                conta.setValor(valor);
                conta.setCategoria(categoria);
                conta.setDataInicio(data);
                conta.setDiaVencimento(data.getDayOfMonth());
                conta.setTipoPagamento("Boleto/Outros");

                // LÓGICA NOVA: DATA FIM
                String dataFimStr = request.getParameter("dataFim");
                if(dataFimStr != null && !dataFimStr.isEmpty()) {
                    conta.setDataFim(LocalDate.parse(dataFimStr));
                } else {
                    conta.setDataFim(null); // Até cancelar
                }

                new ContaRecorrenteDAO().inserir(conta);
            } else {
                // TRANSAÇÃO NORMAL
                Transacao t = new Transacao();
                t.setUsuarioId(u.getId());
                t.setDescricao(descricao);
                t.setValor(valor);
                t.setData(data);
                t.setTipo(tipo);
                t.setCategoria(categoria);
                t.setPago(request.getParameter("pago") != null);
                new TransacaoDAO().inserir(t);
            }
        } catch (SQLException e) { throw new ServletException(e); }

        // Redireciona de volta
        response.sendRedirect(request.getContextPath() + "/dashboard/detalhes_mes.jsp?mes=" + request.getParameter("mes") + "&ano=" + request.getParameter("ano"));
    }
}