package br.com.finantrack.controller;

import br.com.finantrack.controller.ContaRecorrenteDAO;
import br.com.finantrack.controller.ContaRecorrente;
import br.com.finantrack.controller.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controller de Contas Recorrentes (Assinaturas).
    Centraliza criação, exclusão e a complexa lógica de edição com "Split" 
    (dividir histórico vs futuro).
 */

@WebServlet("/ContaRecorrenteController")
public class ContaRecorrenteController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Usuario u = (Usuario) session.getAttribute("usuario");

        if (u == null) {
            response.sendRedirect("tela_login.jsp");
            return;
        }

        String acao = request.getParameter("acao");
        if (acao == null) acao = "listar";

        try {
            switch (acao) {
                case "salvar":
                    salvar(request, u);
                    break;
                case "editar":
                    editar(request, u);
                    break;
                case "excluir":
                    excluir(request);
                    break;
            }
            
            // Redirecionamento
            String mes = request.getParameter("mes");
            String ano = request.getParameter("ano");
            if (mes != null && ano != null) {
                response.sendRedirect("dashboard/detalhes_mes.jsp?mes=" + mes + "&ano=" + ano);
            } else {
                response.sendRedirect("dashboard/dashboard.jsp");
            }

        } catch (Exception e) {
            // Em caso de erro, salva mensagem na sessão para exibir alerta ao usuário
            session.setAttribute("formError", "Erro ao processar conta: " + e.getMessage());
            response.sendRedirect("dashboard/dashboard.jsp");
            e.printStackTrace();
        }
    }

    /**
     * Cria nova assinatura.
     */
    private void salvar(HttpServletRequest request, Usuario u) throws SQLException {
        ContaRecorrente c = new ContaRecorrente();
        c.setUsuarioId(u.getId());
        c.setDescricao(request.getParameter("descricao"));
        c.setValor(Double.parseDouble(request.getParameter("valor").replace(",", ".")));
        c.setCategoria(request.getParameter("categoria"));
        c.setDataInicio(LocalDate.parse(request.getParameter("dataInicio")));
        
        String diaStr = request.getParameter("diaVencimento");
        if (diaStr != null && !diaStr.isEmpty()) c.setDiaVencimento(Integer.parseInt(diaStr));
        else c.setDiaVencimento(c.getDataInicio().getDayOfMonth());
        
        String fimStr = request.getParameter("dataFim");
        if (fimStr != null && !fimStr.isEmpty()) c.setDataFim(LocalDate.parse(fimStr));
        
        c.setTipoPagamento("Outros");
        new ContaRecorrenteDAO().inserir(c);
        request.getSession().setAttribute("formSuccess", "Conta criada com sucesso!");
    }

    /**
     * Edita assinatura. Suporta o modo "Split" (alterar apenas daqui para frente).
     */
    private void editar(HttpServletRequest request, Usuario u) throws SQLException {
        // ID vem negativo na view, converte para positivo
        int idOriginal = Math.abs(Integer.parseInt(request.getParameter("id")));
        
        // Verifica se o usuário escolheu o modo de divisão (preservar histórico)
        boolean isSplit = "true".equals(request.getParameter("split"));
        ContaRecorrenteDAO dao = new ContaRecorrenteDAO();
        
        // Captura dados do form
        String descricao = request.getParameter("descricao");
        double valor = Double.parseDouble(request.getParameter("valor").replace(",", "."));
        String categoria = "Outro".equals(request.getParameter("categoria")) ? request.getParameter("categoriaOutro") : request.getParameter("categoria");
        LocalDate dataRef = LocalDate.parse(request.getParameter("data")); 

        if (isSplit) {
            // === MODO SPLIT ===
            ContaRecorrente contaAntiga = dao.buscarPorId(idOriginal);
            if (contaAntiga != null) {
                // 1. Encerra a conta antiga no mês passado
                contaAntiga.setDataFim(dataRef.withDayOfMonth(1).minusDays(1));
                dao.atualizar(contaAntiga);
                
                // 2. Cria a nova conta a partir deste mês
                ContaRecorrente nova = new ContaRecorrente();
                nova.setUsuarioId(u.getId());
                nova.setDescricao(descricao);
                nova.setValor(valor);
                nova.setCategoria(categoria);
                nova.setDiaVencimento(dataRef.getDayOfMonth());
                nova.setDataInicio(dataRef.withDayOfMonth(1));
                nova.setTipoPagamento(contaAntiga.getTipoPagamento());
                
                String fimStr = request.getParameter("dataFim");
                if (fimStr != null && !fimStr.isEmpty()) nova.setDataFim(LocalDate.parse(fimStr));
                
                dao.inserir(nova);
            }
        } else {
            // === EDIÇÃO SIMPLES (Corrige tudo) ===
            ContaRecorrente c = new ContaRecorrente();
            c.setId(idOriginal);
            c.setDescricao(descricao);
            c.setValor(valor);
            c.setCategoria(categoria);
            c.setDiaVencimento(dataRef.getDayOfMonth());
            String fimStr = request.getParameter("dataFim");
            if (fimStr != null && !fimStr.isEmpty()) c.setDataFim(LocalDate.parse(fimStr));
            
            dao.atualizar(c);
        }
    }

    /**
     * Exclui (cancela) a assinatura definitivamente.
     */
    private void excluir(HttpServletRequest request) throws SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        new ContaRecorrenteDAO().excluir(id);
        request.getSession().setAttribute("formSuccess", "Conta excluída!");
    }
}