package br.com.finantrack.controller;

import br.com.finantrack.util.database;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAO {

    // --- MÉTODOS DE CRIAÇÃO E BUSCA ---

    /**
     * Cria o grupo e já coloca o criador como membro ACEITO
     */
    public void criarGrupo(String nome, int adminId) throws SQLException {
        Connection conn = database.getConnection();
        try {
            conn.setAutoCommit(false); // Transação atômica
            
            String sqlGrupo = "INSERT INTO grupos (nome, admin_id, data_criacao) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sqlGrupo, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, nome);
            stmt.setInt(2, adminId);
            stmt.setString(3, LocalDate.now().toString());
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            int grupoId = 0;
            if(rs.next()) grupoId = rs.getInt(1);
            
            // Adiciona o criador como membro automaticamente
            String sqlMembro = "INSERT INTO grupo_membros (grupo_id, usuario_id, status, data_entrada) VALUES (?, ?, 'ACEITO', ?)";
            PreparedStatement stmt2 = conn.prepareStatement(sqlMembro);
            stmt2.setInt(1, grupoId);
            stmt2.setInt(2, adminId);
            stmt2.setString(3, LocalDate.now().toString());
            stmt2.executeUpdate();
            
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    /**
     * Busca o primeiro grupo que o usuário participa (usado no LoginServlet).
     * Retorna null se não tiver grupo.
     */
    public Grupo buscarGrupoPorUsuario(int usuarioId) throws SQLException {
        String sql = "SELECT g.* FROM grupos g " +
                     "JOIN grupo_membros gm ON g.id = gm.grupo_id " +
                     "WHERE gm.usuario_id = ? AND gm.status = 'ACEITO' LIMIT 1";
        
        try (Connection conn = database.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Grupo g = new Grupo();
                g.setId(rs.getInt("id"));
                g.setNome(rs.getString("nome"));
                g.setAdminId(rs.getInt("admin_id"));
                return g;
            }
        }
        return null;
    }

    public List<Grupo> listarMeusGrupos(int usuarioId) throws SQLException {
        List<Grupo> lista = new ArrayList<>();
        String sql = "SELECT g.*, gm.status FROM grupos g " +
                     "JOIN grupo_membros gm ON g.id = gm.grupo_id " +
                     "WHERE gm.usuario_id = ? AND gm.status = 'ACEITO'";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Grupo g = new Grupo();
                g.setId(rs.getInt("id"));
                g.setNome(rs.getString("nome"));
                g.setAdminId(rs.getInt("admin_id"));
                lista.add(g);
            }
        }
        return lista;
    }

    // --- MÉTODOS DE CONVITE ---

    public boolean convidarPorEmail(int grupoId, String email) throws SQLException {
        try (Connection conn = database.getConnection()) {
            // 1. Achar ID do usuário pelo email
            String sqlUser = "SELECT id FROM usuarios WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sqlUser);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                
                // 2. Verificar se já não é membro (pendente ou aceito)
                String sqlCheck = "SELECT id FROM grupo_membros WHERE grupo_id=? AND usuario_id=?";
                PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck);
                stmtCheck.setInt(1, grupoId);
                stmtCheck.setInt(2, userId);
                if(stmtCheck.executeQuery().next()) return false; // Já está no grupo

                // 3. Inserir convite (status PENDENTE)
                String sqlInsert = "INSERT INTO grupo_membros (grupo_id, usuario_id, status, data_entrada) VALUES (?, ?, 'PENDENTE', ?)";
                PreparedStatement stmtIns = conn.prepareStatement(sqlInsert);
                stmtIns.setInt(1, grupoId);
                stmtIns.setInt(2, userId);
                stmtIns.setString(3, LocalDate.now().toString());
                stmtIns.executeUpdate();
                return true;
            }
            return false; // Email não existe no sistema
        }
    }

    public List<Grupo> listarConvitesPendentes(int usuarioId) throws SQLException {
        List<Grupo> lista = new ArrayList<>();
        String sql = "SELECT g.* FROM grupos g " +
                     "JOIN grupo_membros gm ON g.id = gm.grupo_id " +
                     "WHERE gm.usuario_id = ? AND gm.status = 'PENDENTE'";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Grupo g = new Grupo();
                g.setId(rs.getInt("id"));
                g.setNome(rs.getString("nome"));
                g.setAdminId(rs.getInt("admin_id"));
                lista.add(g);
            }
        }
        return lista;
    }

    public void responderConvite(int grupoId, int usuarioId, String resposta) throws SQLException {
        String sql = "UPDATE grupo_membros SET status = ? WHERE grupo_id = ? AND usuario_id = ?";
        if ("RECUSADO".equals(resposta)) {
            sql = "DELETE FROM grupo_membros WHERE grupo_id = ? AND usuario_id = ?"; // Remove se recusar
        }
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            if ("RECUSADO".equals(resposta)) {
                stmt.setInt(1, grupoId);
                stmt.setInt(2, usuarioId);
            } else {
                stmt.setString(1, resposta);
                stmt.setInt(2, grupoId);
                stmt.setInt(3, usuarioId);
            }
            stmt.executeUpdate();
        }
    }

    // --- MÉTODOS DE TRANSAÇÃO DO GRUPO (A "PLANILHA") ---

    public void adicionarDespesaGrupo(int grupoId, String descricao, double valor, String data, String categoria) throws SQLException {
        String sql = "INSERT INTO transacoes_grupo (grupo_id, descricao, valor, data_vencimento, categoria, status_pagamento) VALUES (?, ?, ?, ?, ?, 'PENDENTE')";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grupoId);
            stmt.setString(2, descricao);
            stmt.setDouble(3, valor);
            stmt.setString(4, data);
            stmt.setString(5, categoria);
            stmt.executeUpdate();
        }
    }

    public List<Transacao> listarTransacoesGrupo(int grupoId) throws SQLException {
        List<Transacao> lista = new ArrayList<>();
        // Traz também o nome de quem pagou (join com usuarios)
        String sql = "SELECT tg.*, u.nome as nome_pagante FROM transacoes_grupo tg " +
                     "LEFT JOIN usuarios u ON tg.usuario_pagante_id = u.id " +
                     "WHERE tg.grupo_id = ? ORDER BY tg.data_vencimento";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grupoId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Transacao t = new Transacao();
                t.setId(rs.getInt("id"));
                t.setDescricao(rs.getString("descricao"));
                t.setValor(rs.getDouble("valor"));
                t.setData(LocalDate.parse(rs.getString("data_vencimento")));
                t.setCategoria(rs.getString("categoria"));
                
                String status = rs.getString("status_pagamento");
                t.setPago("PAGO".equals(status));
                
                // Usamos o campo 'origem' para guardar o NOME de quem pagou (para exibir na tela)
                t.setOrigem(rs.getString("nome_pagante")); 
                
                lista.add(t);
            }
        }
        return lista;
    }

    /**
     * A MÁGICA ACONTECE AQUI:
     * 1. Marca como PAGO na tabela do grupo.
     * 2. Cria uma transação de SAÍDA na conta pessoal de quem clicou.
     */
    public void pagarItemGrupo(int transacaoGrupoId, int usuarioPaganteId) throws SQLException {
        Connection conn = database.getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Pega dados da despesa original
            String sqlGet = "SELECT * FROM transacoes_grupo WHERE id = ?";
            PreparedStatement stmtGet = conn.prepareStatement(sqlGet);
            stmtGet.setInt(1, transacaoGrupoId);
            ResultSet rs = stmtGet.executeQuery();
            
            if (rs.next()) {
                String descricaoOriginal = rs.getString("descricao");
                double valor = rs.getDouble("valor");
                String categoria = rs.getString("categoria");
                
                // Data de pagamento é HOJE (quem paga, paga hoje)
                String hoje = LocalDate.now().toString();

                // 2. Atualiza status no grupo para PAGO
                String sqlUp = "UPDATE transacoes_grupo SET status_pagamento='PAGO', usuario_pagante_id=? WHERE id=?";
                PreparedStatement stmtUp = conn.prepareStatement(sqlUp);
                stmtUp.setInt(1, usuarioPaganteId);
                stmtUp.setInt(2, transacaoGrupoId);
                stmtUp.executeUpdate();

                // 3. Insere na conta pessoal de quem pagou
                // Título: "Despesas Casa - [Item]" para ficar claro no extrato pessoal
                String sqlPessoal = "INSERT INTO transacoes (usuario_id, descricao, valor, data, tipo, categoria, pago) VALUES (?, ?, ?, ?, 'saida', ?, 1)";
                PreparedStatement stmtP = conn.prepareStatement(sqlPessoal);
                stmtP.setInt(1, usuarioPaganteId);
                stmtP.setString(2, "Despesas Casa - " + descricaoOriginal); 
                stmtP.setDouble(3, valor);
                stmtP.setString(4, hoje); 
                stmtP.setString(5, categoria);
                stmtP.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}