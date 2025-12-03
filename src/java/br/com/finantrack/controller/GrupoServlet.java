package br.com.finantrack.controller;

import br.com.finantrack.controller.GrupoDAO; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/* responsável pelo módulo de Finanças Compartilhadas (Grupos).
    Gerencia: Criação de grupo, Convites, Respostas e Adição/Pagamento de despesas comuns.
 */

@WebServlet("/GrupoServlet")
public class GrupoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("userId");
        
        if (usuarioId == null) { 
            response.sendRedirect("tela_login.jsp"); 
            return; 
        }

        String action = request.getParameter("action");
        GrupoDAO dao = new GrupoDAO();

        try {
            // === AÇÃO 1: CRIAR NOVO GRUPO ===
            if ("criar".equals(action)) {
                String nome = request.getParameter("nome");
                dao.criarGrupo(nome, usuarioId);
                response.sendRedirect("dashboard/meus_grupos.jsp");
                
            // === AÇÃO 2: ENVIAR CONVITE POR EMAIL ===
            } else if ("convidar".equals(action)) {
                int grupoId = Integer.parseInt(request.getParameter("grupoId"));
                String email = request.getParameter("email");
                
                boolean achou = dao.convidarPorEmail(grupoId, email);
                if(achou) session.setAttribute("msgGrupo", "Convite enviado para " + email);
                else session.setAttribute("errGrupo", "Email não encontrado no sistema.");
                
                response.sendRedirect("dashboard/visualizar_grupo.jsp?id=" + grupoId);
                
            // === AÇÃO 3: RESPONDER CONVITE (ACEITAR/RECUSAR) ===
            } else if ("responder".equals(action)) {
                int grupoId = Integer.parseInt(request.getParameter("grupoId"));
                String resposta = request.getParameter("resposta"); 
                dao.responderConvite(grupoId, usuarioId, resposta);
                response.sendRedirect("dashboard/meus_grupos.jsp");
                
            // === AÇÃO 4: ADICIONAR DESPESA NA PLANILHA ===
            } else if ("adicionarDespesa".equals(action)) {
                int grupoId = Integer.parseInt(request.getParameter("grupoId"));
                String desc = request.getParameter("descricao");
                double valor = Double.parseDouble(request.getParameter("valor"));
                String data = request.getParameter("data");
                String cat = request.getParameter("categoria");
                
                dao.adicionarDespesaGrupo(grupoId, desc, valor, data, cat);
                response.sendRedirect("dashboard/visualizar_grupo.jsp?id=" + grupoId);
                
            // === AÇÃO 5: PAGAR CONTA (BAIXA + DÉBITO PESSOAL) ===
            } else if ("pagar".equals(action)) {
                int tId = Integer.parseInt(request.getParameter("transacaoId"));
                int grupoId = Integer.parseInt(request.getParameter("grupoId"));
                
                // Marca como pago no grupo e lança débito na conta pessoal de quem pagou
                dao.pagarItemGrupo(tId, usuarioId);
                response.sendRedirect("dashboard/visualizar_grupo.jsp?id=" + grupoId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard/meus_grupos.jsp?erro=true");
        }
    }
}