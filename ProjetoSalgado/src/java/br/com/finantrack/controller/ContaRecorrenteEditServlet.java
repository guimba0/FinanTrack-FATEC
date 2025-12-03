package br.com.finantrack.controller;

import java.io.IOException;
import java.time.LocalDate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ContaRecorrenteEditServlet")
public class ContaRecorrenteEditServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        try {
            // O ID vem negativo se for clicado na tabela (-5), então pegamos o absoluto
            int idOriginal = Math.abs(Integer.parseInt(request.getParameter("id")));
            
            String descricao = request.getParameter("descricao");
            double valor = Double.parseDouble(request.getParameter("valor").replace(",", "."));
            String categoria = "Outro".equals(request.getParameter("categoria")) ? request.getParameter("categoriaOutro") : request.getParameter("categoria");
            LocalDate dataRef = LocalDate.parse(request.getParameter("data")); // Data escolhida no form
            
            // Verifica se é modo SPLIT (Edição a partir deste mês)
            String splitParam = request.getParameter("split");
            boolean isSplit = "true".equals(splitParam);

            ContaRecorrenteDAO dao = new ContaRecorrenteDAO();

            if (isSplit) {
                // --- LÓGICA DE DIVISÃO (MUDAR VALOR DAQUI P/ FRENTE) ---
                
                // 1. Busca a conta original para pegar dados que não mudaram (ex: inicio original)
                ContaRecorrente contaAntiga = dao.buscarPorId(idOriginal);
                
                if (contaAntiga != null) {
                    // 2. Encerra a conta antiga no mês anterior à data escolhida
                    // Define fim para o último dia do mês anterior
                    LocalDate fimAntigo = dataRef.withDayOfMonth(1).minusDays(1);
                    contaAntiga.setDataFim(fimAntigo);
                    dao.atualizar(contaAntiga); // Atualiza no banco
                    
                    // 3. Cria a nova conta a partir do dia 1 deste mês
                    ContaRecorrente novaConta = new ContaRecorrente();
                    novaConta.setUsuarioId(contaAntiga.getUsuarioId());
                    novaConta.setDescricao(descricao); // Novos dados
                    novaConta.setValor(valor);         // Novos dados
                    novaConta.setCategoria(categoria); // Novos dados
                    novaConta.setDiaVencimento(dataRef.getDayOfMonth());
                    novaConta.setDataInicio(dataRef.withDayOfMonth(1)); // Começa dia 1 deste mês
                    novaConta.setTipoPagamento(contaAntiga.getTipoPagamento());
                    
                    // Se tinha data fim definida no form, usa ela, senão null
                    String dataFimStr = request.getParameter("dataFim");
                    if (dataFimStr != null && !dataFimStr.isEmpty()) {
                        novaConta.setDataFim(LocalDate.parse(dataFimStr));
                    }
                    
                    dao.inserir(novaConta);
                }

            } else {
                // --- EDIÇÃO SIMPLES (CORREÇÃO DE CADASTRO) ---
                String dataFimStr = request.getParameter("dataFim");
                LocalDate dataFim = (dataFimStr != null && !dataFimStr.isEmpty()) ? LocalDate.parse(dataFimStr) : null;

                ContaRecorrente conta = new ContaRecorrente();
                conta.setId(idOriginal);
                conta.setDescricao(descricao);
                conta.setValor(valor);
                conta.setCategoria(categoria);
                conta.setDiaVencimento(dataRef.getDayOfMonth());
                conta.setDataFim(dataFim);

                dao.atualizar(conta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Mantém a navegação
        String mes = request.getParameter("mes");
        String ano = request.getParameter("ano");
        response.sendRedirect(request.getContextPath() + "/dashboard/detalhes_mes.jsp?mes=" + mes + "&ano=" + ano);
    }
}