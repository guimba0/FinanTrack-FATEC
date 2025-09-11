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

@WebServlet("/transacao") // URL que recebe os dados do formulário
public class TransacaoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("userId");

        // Se o usuário não estiver logado, não faz nada
        if (usuarioId == null) {
            response.sendRedirect(request.getContextPath() + "/tela_login.jsp");
            return;
        }

        // 1. Pega todos os dados enviados pelo formulário do modal
        String descricao = request.getParameter("descricao");
        double valor = Double.parseDouble(request.getParameter("valor"));
        LocalDate data = LocalDate.parse(request.getParameter("data"));
        String tipo = request.getParameter("tipo");
        String categoria = request.getParameter("categoria");

        // Se a categoria for "Outro", pega o valor do campo de texto
        if ("Outro".equals(categoria)) {
            categoria = request.getParameter("categoriaOutro");
        }

        // 2. Cria um objeto Transacao com esses dados
        Transacao novaTransacao = new Transacao();
        novaTransacao.setUsuarioId(usuarioId);
        novaTransacao.setDescricao(descricao);
        novaTransacao.setValor(valor);
        novaTransacao.setData(data);
        novaTransacao.setTipo(tipo);
        novaTransacao.setCategoria(categoria);

        // 3. Usa o DAO para inserir a nova transação no banco
        try {
            TransacaoDAO dao = new TransacaoDAO();
            dao.inserir(novaTransacao);
        } catch (SQLException e) {
            // Em caso de erro, lança uma exceção para o servidor
            throw new ServletException("Erro ao inserir transação no banco de dados", e);
        }

        // 4. Redireciona o usuário de volta para a página de detalhes do mês
        String mes = request.getParameter("mes");
        String ano = request.getParameter("ano");
        response.sendRedirect(request.getContextPath() + "/dashboard/detalhes_mes.jsp?mes=" + mes + "&ano=" + ano);
    }
}