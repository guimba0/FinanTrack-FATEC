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

@WebServlet("/conta-recorrente")
public class ContaRecorrenteServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("userId");

        // 1. Garante que o usuário está logado
        if (usuarioId == null) {
            response.sendRedirect(request.getContextPath() + "/tela_login.jsp");
            return;
        }

        // 2. Coloca toda a lógica dentro de um bloco try-catch
        try {
            // Pega os dados do formulário
            String descricao = request.getParameter("descricao");
            String categoria = request.getParameter("categoria");
            String valorStr = request.getParameter("valor");
            String diaVencimentoStr = request.getParameter("diaVencimento");
            String dataInicioStr = request.getParameter("dataInicio");
            String dataFimStr = request.getParameter("dataFim");

            // Valida se os campos principais não estão vazios
            if (valorStr == null || valorStr.trim().isEmpty() ||
                dataInicioStr == null || dataInicioStr.trim().isEmpty() ||
                descricao == null || descricao.trim().isEmpty() ||
                categoria == null || categoria.trim().isEmpty()) {
                
                session.setAttribute("formError", "Por favor, preencha todos os campos obrigatórios.");
                response.sendRedirect(request.getContextPath() + "/dashboard/dashboard.jsp");
                return;
            }

            // Converte os dados para os tipos corretos
            double valor = Double.parseDouble(valorStr);
            LocalDate dataInicio = LocalDate.parse(dataInicioStr);
            LocalDate dataFim = (dataFimStr != null && !dataFimStr.isEmpty()) ? LocalDate.parse(dataFimStr) : null;

            // Lida com o dia de vencimento opcional
            Integer diaVencimento = null;
            if (diaVencimentoStr != null && !diaVencimentoStr.trim().isEmpty()) {
                diaVencimento = Integer.parseInt(diaVencimentoStr);
            }

            // Cria o objeto ContaRecorrente
            ContaRecorrente conta = new ContaRecorrente();
            conta.setUsuarioId(usuarioId);
            conta.setDescricao(descricao);
            conta.setValor(valor);
            conta.setCategoria(categoria);
            conta.setDiaVencimento(diaVencimento);
            conta.setDataInicio(dataInicio);
            conta.setDataFim(dataFim);

            // Salva no banco de dados
            ContaRecorrenteDAO dao = new ContaRecorrenteDAO();
            dao.inserir(conta);
            
            session.setAttribute("formSuccess", "Conta recorrente adicionada com sucesso!");

        } catch (NumberFormatException e) {
            session.setAttribute("formError", "O campo 'valor' deve ser um número válido.");
            e.printStackTrace();
        } catch (SQLException e) {
            session.setAttribute("formError", "Erro ao salvar a conta no banco de dados.");
            throw new ServletException("Erro ao inserir conta recorrente", e);
        }

        // 3. Redireciona de volta para o dashboard
        response.sendRedirect(request.getContextPath() + "/dashboard/dashboard.jsp");
    }
}