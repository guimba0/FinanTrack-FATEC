package br.com.finantrack.controller;

// Imports necessários (Assumindo que os DAOs e Models estão neste pacote ou foram importados corretamente)
import br.com.finantrack.controller.ContaRecorrenteDAO;
import br.com.finantrack.controller.TransacaoDAO;
import br.com.finantrack.controller.ContaRecorrente;
import br.com.finantrack.controller.Transacao;
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

/*Este Servlet substitui múltiplos arquivos pequenos:
    Salvar nova transação (Receita/Despesa)
    Editar transação existente
    Excluir transação
    Alterar status (Pago/Pendente)
 */

@WebServlet("/TransacaoController")
public class TransacaoController extends HttpServlet {

    /**
     * Método único 'service' que intercepta tanto requisições GET quanto POST.
     * Redireciona para a lógica específica baseada no parâmetro 'acao' vindo da URL ou formulário.
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Define codificação UTF-8 para evitar problemas com acentos (R$ e descrições)
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        Usuario u = (Usuario) session.getAttribute("usuario");
        
        // Validação de Segurança: Se não estiver logado, manda para o login
        if (u == null) {
            response.sendRedirect("tela_login.jsp");
            return;
        }

        // Recupera a ação desejada (ex: salvar, excluir). Se nula, define 'listar' como padrão.
        String acao = request.getParameter("acao");
        if (acao == null) acao = "listar"; 

        try {
            // Roteador de comandos (Switch Case substitui múltiplos Servlets)
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
                case "status": // Ação de marcar como Pago ou Pendente (Check)
                    alterarStatus(request);
                    break;
                default:
                    // Ação desconhecida, volta para o dashboard
                    response.sendRedirect("dashboard/dashboard.jsp");
                    return;
            }
            
            // Lógica de Redirecionamento Inteligente:
            // Tenta manter o usuário no mês/ano que ele estava visualizando na tabela
            String mes = request.getParameter("mes");
            String ano = request.getParameter("ano");
            
            if (mes != null && ano != null) {
                response.sendRedirect("dashboard/detalhes_mes.jsp?mes=" + mes + "&ano=" + ano);
            } else {
                response.sendRedirect("dashboard/dashboard.jsp");
            }

        } catch (Exception e) {
            // Em caso de erro, lança exceção para o servidor tratar (ou exibe página de erro)
            throw new ServletException("Erro crítico no TransacaoController: " + e.getMessage(), e);
        }
    }

    /**
     * Lógica para salvar uma NOVA transação.
     * Decide se salva na tabela 'transacoes' ou 'contas_recorrentes' baseada no checkbox.
     */
    private void salvar(HttpServletRequest request, Usuario u) throws SQLException {
        // Coleta dados do formulário
        String descricao = request.getParameter("descricao");
        // Trata moeda: troca vírgula por ponto para o Java entender
        double valor = Double.parseDouble(request.getParameter("valor").replace(",", "."));
        LocalDate data = LocalDate.parse(request.getParameter("data"));
        String tipo = request.getParameter("tipo");
        
        // Verifica se é uma categoria pré-definida ou uma nova digitada ("Outro")
        String categoria = "Outro".equals(request.getParameter("categoria")) 
                         ? request.getParameter("categoriaOutro") : request.getParameter("categoria");
        
        // Checkbox: O usuário marcou como "Conta Fixa/Recorrente"?
        boolean isRecorrente = request.getParameter("recorrente") != null;

        if (isRecorrente) {
            // --- CAMINHO A: Salvar como Assinatura Mensal ---
            ContaRecorrente conta = new ContaRecorrente();
            conta.setUsuarioId(u.getId());
            conta.setDescricao(descricao);
            conta.setValor(valor);
            conta.setCategoria(categoria);
            conta.setDataInicio(data);
            conta.setDiaVencimento(data.getDayOfMonth()); // Vence todo dia X
            conta.setTipoPagamento("Boleto/Outros");
            
            // Data Fim é opcional
            String dataFimStr = request.getParameter("dataFim");
            if(dataFimStr != null && !dataFimStr.isEmpty()) conta.setDataFim(LocalDate.parse(dataFimStr));
            
            new ContaRecorrenteDAO().inserir(conta);
        } else {
            // --- CAMINHO B: Salvar como Transação Única ---
            Transacao t = new Transacao();
            t.setUsuarioId(u.getId());
            t.setDescricao(descricao);
            t.setValor(valor);
            t.setData(data);
            t.setTipo(tipo);
            t.setCategoria(categoria);
            // Verifica se já foi paga no ato do cadastro
            t.setPago(request.getParameter("pago") != null);
            
            new TransacaoDAO().inserir(t);
        }
    }

    /**
     * Lógica para editar uma transação existente.
     */
    private void editar(HttpServletRequest request, Usuario u) throws SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        
        Transacao t = new Transacao();
        t.setId(id);
        t.setUsuarioId(u.getId());
        t.setDescricao(request.getParameter("descricao"));
        t.setValor(Double.parseDouble(request.getParameter("valor").replace(",", ".")));
        t.setData(LocalDate.parse(request.getParameter("data")));
        t.setTipo(request.getParameter("tipo"));
        t.setCategoria(request.getParameter("categoria"));
        t.setPago(request.getParameter("pago") != null); // Checkbox de pagamento

        new TransacaoDAO().atualizar(t);
    }

    /**
     * Lógica para excluir uma transação.
     */
    private void excluir(HttpServletRequest request) throws SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        new TransacaoDAO().excluir(id);
    }

    /**
     * Lógica para alternar o status (Pago/Pendente) rapidamente ao clicar no ícone.
     * Lida com a complexidade de IDs negativos (Contas Recorrentes simuladas).
     */
    private void alterarStatus(HttpServletRequest request) throws SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean pago = Boolean.parseBoolean(request.getParameter("pago"));
        String mes = request.getParameter("mes");
        String ano = request.getParameter("ano");

        if (id < 0) {
            // É UMA CONTA RECORRENTE (ID Negativo)
            // Não alteramos a transação, mas marcamos na recorrência que "este mês foi pago"
            int idReal = Math.abs(id);
            ContaRecorrenteDAO dao = new ContaRecorrenteDAO();
            
            // Se pago, salva "2023-10". Se não, salva null.
            String mesAno = pago ? String.format("%s-%02d", ano, Integer.parseInt(mes)) : null;
            dao.marcarComoPaga(idReal, mesAno);
        } else {
            // É UMA TRANSAÇÃO NORMAL
            // Apenas atualiza o booleano no banco
            new TransacaoDAO().atualizarStatus(id, pago);
        }
    }
}