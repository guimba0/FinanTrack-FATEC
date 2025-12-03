package br.com.finantrack.controller;

import br.com.finantrack.util.DatabaseConnection; // Import da classe de conexão
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável pela persistência de Grupos e Finanças Compartilhadas.
 */
public class GrupoDAO {

    // --- MÉTODOS DE CRIAÇÃO ---

    /**
     * Cria um novo grupo e adiciona o criador como administrador/membro.
     */
    public void criarGrupo(String nome, int adminId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false); // Início da transação
            
            // 1. Cria o grupo
            String sqlGrupo = "INSERT INTO grupos (nome, admin_id, data_criacao) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sqlGrupo, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, nome);
            stmt.setInt(2, adminId);
            stmt.setString(3, LocalDate.now().toString());
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            int grupoId = 0;
            if(rs.next()) grupoId = rs.getInt(1);
            
            // 2. Adiciona o criador como membro
            String sqlMembro = "INSERT INTO grupo_membros (grupo_id, usuario_id, status, data_entrada) VALUES (?, ?, 'ACEITO', ?)";
            PreparedStatement stmt2 = conn.prepareStatement(sqlMembro);
            stmt2.setInt(1, grupoId);
            stmt2.setInt(2, adminId);
            stmt2.setString(3, LocalDate.now().toString());
            stmt2.executeUpdate();
            
            conn.commit(); // Confirma transação
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    // --- MÉTODOS DE BUSCA E LISTAGEM (Os que faltavam!) ---

    /**
     * Busca um único grupo para o login (atalho).
     */
    public Grupo buscarGrupoPorUsuario(int usuarioId) throws SQLException {
        String sql = "SELECT g.* FROM grupos g " +
                     "JOIN grupo_membros gm ON g.id = gm.grupo_id " +
                     "WHERE gm.usuario_id = ? AND gm.status = 'ACEITO' LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Grupo(rs.getInt("id"), rs.getString("nome"), rs.getInt("admin_id"));
            }
        }
        return null;
    }

    /**
     * Lista TODOS os grupos que o usuário participa.
     * (Este é o método que estava faltando e gerando o erro no JSP)
     */
    public List<Grupo> listarMeusGrupos(int usuarioId) throws SQLException {
        List<Grupo> lista = new ArrayList<>();
        String sql = "SELECT g.* FROM grupos g " +
                     "JOIN grupo_membros gm ON g.id = gm.grupo_id " +
                     "WHERE gm.usuario_id = ? AND gm.status = 'ACEITO'";
                     
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
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

    /**
     * Lista os convites pendentes que o usuário recebeu.
     * (Este também estava faltando)
     */
    public List<Grupo> listarConvitesPendentes(int usuarioId) throws SQLException {
        List<Grupo> lista = new ArrayList<>();
        String sql = "SELECT g.* FROM grupos g " +
                     "JOIN grupo_membros gm ON g.id = gm.grupo_id " +
                     "WHERE gm.usuario_id = ? AND gm.status = 'PENDENTE'";
                     
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
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
        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Busca usuário pelo email
            PreparedStatement stmt = conn.prepareStatement("SELECT id FROM usuarios WHERE email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                
                // 2. Verifica se já é membro
                PreparedStatement stmtCheck = conn.prepareStatement("SELECT id FROM grupo_membros WHERE grupo_id=? AND usuario_id=?");
                stmtCheck.setInt(1, grupoId);
                stmtCheck.setInt(2, userId);
                if(stmtCheck.executeQuery().next()) return false; // Já existe

                // 3. Insere convite
                PreparedStatement stmtIns = conn.prepareStatement("INSERT INTO grupo_membros (grupo_id, usuario_id, status, data_entrada) VALUES (?, ?, 'PENDENTE', ?)");
                stmtIns.setInt(1, grupoId);
                stmtIns.setInt(2, userId);
                stmtIns.setString(3, LocalDate.now().toString());
                stmtIns.executeUpdate();
                return true;
            }
            return false; // Email não encontrado
        }
    }

    public void responderConvite(int grupoId, int usuarioId, String resposta) throws SQLException {
        String sql = "UPDATE grupo_membros SET status = ? WHERE grupo_id = ? AND usuario_id = ?";
        if ("RECUSADO".equals(resposta)) {
            sql = "DELETE FROM grupo_membros WHERE grupo_id = ? AND usuario_id = ?"; // Remove se recusar
        }
        
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
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

    // --- MÉTODOS DA PLANILHA (TRANSAÇÕES DO GRUPO) ---

    public void adicionarDespesaGrupo(int grupoId, String descricao, double valor, String data, String categoria) throws SQLException {
        String sql = "INSERT INTO transacoes_grupo (grupo_id, descricao, valor, data_vencimento, categoria, status_pagamento) VALUES (?, ?, ?, ?, ?, 'PENDENTE')";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = "SELECT tg.*, u.nome as nome_pagante FROM transacoes_grupo tg " +
                     "LEFT JOIN usuarios u ON tg.usuario_pagante_id = u.id " +
                     "WHERE tg.grupo_id = ? ORDER BY tg.data_vencimento";
                     
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                t.setOrigem(rs.getString("nome_pagante")); // Nome de quem pagou
                
                lista.add(t);
            }
        }
        return lista;
    }

    public void pagarItemGrupo(int transacaoGrupoId, int usuarioPaganteId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Pega dados da despesa
            PreparedStatement stmtGet = conn.prepareStatement("SELECT * FROM transacoes_grupo WHERE id = ?");
            stmtGet.setInt(1, transacaoGrupoId);
            ResultSet rs = stmtGet.executeQuery();
            
            if (rs.next()) {
                String descricaoOriginal = rs.getString("descricao");
                double valor = rs.getDouble("valor");
                String categoria = rs.getString("categoria");
                
                // 2. Atualiza grupo (Pago)
                PreparedStatement stmtUp = conn.prepareStatement("UPDATE transacoes_grupo SET status_pagamento='PAGO', usuario_pagante_id=? WHERE id=?");
                stmtUp.setInt(1, usuarioPaganteId);
                stmtUp.setInt(2, transacaoGrupoId);
                stmtUp.executeUpdate();

                // 3. Debita do usuário (Saída Pessoal)
                PreparedStatement stmtP = conn.prepareStatement("INSERT INTO transacoes (usuario_id, descricao, valor, data, tipo, categoria, pago) VALUES (?, ?, ?, ?, 'saida', ?, 1)");
                stmtP.setInt(1, usuarioPaganteId);
                stmtP.setString(2, "Despesas Grupo - " + descricaoOriginal); 
                stmtP.setDouble(3, valor);
                stmtP.setString(4, LocalDate.now().toString()); 
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