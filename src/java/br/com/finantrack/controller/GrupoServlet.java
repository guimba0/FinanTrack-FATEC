package br.com.finantrack.controller;

import br.com.finantrack.util.database;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "GrupoServlet", urlPatterns = {"/GrupoServlet"})
public class GrupoServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuario");

        if (usuarioLogado == null) {
            response.sendRedirect("tela_login.jsp");
            return;
        }

        try {
            if ("criar".equals(action)) {
                // Passo 1 e 2 vêm juntos no formulário final
                String nomeGrupo = request.getParameter("nome");
                String tipoGrupo = request.getParameter("tipoGrupo"); // Vaquinha ou Gestão
                String emailConvidado = request.getParameter("emailConvidado");
                String motivoConvite = request.getParameter("motivoConvite");

                criarGrupoEConvidar(usuarioLogado.getId(), nomeGrupo, tipoGrupo, emailConvidado, motivoConvite);
                
                // Redireciona de volta para o dashboard com mensagem de sucesso
                session.setAttribute("msgSucesso", "Grupo criado e convite enviado!");
                response.sendRedirect("dashboard/dashboard.jsp"); 

            } else if ("aceitarConvite".equals(action)) {
                // Lógica para aceitar (seria implementada aqui)
            } else {
                // Se não for ação específica, apenas redireciona
                response.sendRedirect("dashboard/dashboard.jsp");
            }

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void criarGrupoEConvidar(int criadorId, String nome, String tipo, String emailConvidado, String motivo) throws SQLException {
        try (Connection conn = database.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // 1. Criar Grupo
                int grupoId = -1;
                String sqlGrupo = "INSERT INTO grupos (nome, criador_id, tipo) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlGrupo, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, nome);
                    stmt.setInt(2, criadorId);
                    stmt.setString(3, tipo);
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) grupoId = rs.getInt(1);
                    }
                }

                // 2. Adicionar Criador como Membro ACEITO
                String sqlMembro = "INSERT INTO membros_grupo (grupo_id, usuario_id, status) VALUES (?, ?, 'ACEITO')";
                try (PreparedStatement stmt = conn.prepareStatement(sqlMembro)) {
                    stmt.setInt(1, grupoId);
                    stmt.setInt(2, criadorId);
                    stmt.executeUpdate();
                }

                // 3. Buscar ID do Convidado pelo Email
                int convidadoId = -1;
                String sqlBusca = "SELECT id FROM usuarios WHERE email = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlBusca)) {
                    stmt.setString(1, emailConvidado);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) convidadoId = rs.getInt("id");
                    }
                }

                // 4. Criar Notificação de Convite (Se usuário existir)
                if (convidadoId != -1) {
                    String sqlNotif = "INSERT INTO notificacoes (usuario_destino_id, mensagem, tipo, id_referencia) VALUES (?, ?, 'CONVITE_GRUPO', ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sqlNotif)) {
                        stmt.setInt(1, convidadoId);
                        // Junta o motivo com o nome do grupo na mensagem
                        String msgCompleta = "Convite para o grupo '" + nome + "'. Motivo: " + motivo;
                        stmt.setString(2, msgCompleta);
                        stmt.setInt(3, grupoId);
                        stmt.executeUpdate();
                    }
                    
                    // Adiciona na tabela de membros como PENDENTE
                    String sqlPendente = "INSERT INTO membros_grupo (grupo_id, usuario_id, status) VALUES (?, ?, 'PENDENTE')";
                    try (PreparedStatement stmt = conn.prepareStatement(sqlPendente)) {
                        stmt.setInt(1, grupoId);
                        stmt.setInt(2, convidadoId);
                        stmt.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Métodos doGet e doPost padrão...
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { processRequest(req, resp); }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { processRequest(req, resp); }
}