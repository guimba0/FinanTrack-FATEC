package br.com.finantrack.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/GrupoServlet")
public class GrupoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("userId");
        if (usuarioId == null) { response.sendRedirect("tela_login.jsp"); return; }

        String action = request.getParameter("action");
        GrupoDAO dao = new GrupoDAO();

        try {
            if ("criar".equals(action)) {
                String nome = request.getParameter("nome");
                dao.criarGrupo(nome, usuarioId);
                response.sendRedirect("dashboard/meus_grupos.jsp");
                
            } else if ("convidar".equals(action)) {
                int grupoId = Integer.parseInt(request.getParameter("grupoId"));
                String email = request.getParameter("email");
                boolean achou = dao.convidarPorEmail(grupoId, email);
                if(achou) session.setAttribute("msgGrupo", "Convite enviado para " + email);
                else session.setAttribute("errGrupo", "Email não encontrado.");
                response.sendRedirect("dashboard/visualizar_grupo.jsp?id=" + grupoId);
                
            } else if ("responder".equals(action)) {
                int grupoId = Integer.parseInt(request.getParameter("grupoId"));
                String resposta = request.getParameter("resposta"); // ACEITO ou RECUSADO
                dao.responderConvite(grupoId, usuarioId, resposta);
                response.sendRedirect("dashboard/meus_grupos.jsp");
                
            } else if ("adicionarDespesa".equals(action)) {
                int grupoId = Integer.parseInt(request.getParameter("grupoId"));
                String desc = request.getParameter("descricao");
                double valor = Double.parseDouble(request.getParameter("valor"));
                String data = request.getParameter("data");
                String cat = request.getParameter("categoria");
                dao.adicionarDespesaGrupo(grupoId, desc, valor, data, cat);
                response.sendRedirect("dashboard/visualizar_grupo.jsp?id=" + grupoId);
                
            } else if ("pagar".equals(action)) {
                int tId = Integer.parseInt(request.getParameter("transacaoId"));
                int grupoId = Integer.parseInt(request.getParameter("grupoId"));
                
                // Mágica de pagar e debitar do pessoal
                dao.pagarItemGrupo(tId, usuarioId);
                
                response.sendRedirect("dashboard/visualizar_grupo.jsp?id=" + grupoId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard/meus_grupos.jsp");
        }
    }
}