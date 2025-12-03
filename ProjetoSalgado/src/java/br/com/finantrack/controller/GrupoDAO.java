package br.com.finantrack.controller;

import br.com.finantrack.util.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GrupoDAO {

    // Método ORIGINAL simples
    public void criarGrupo(String nome, int criadorId) throws SQLException {
        String sqlGrupo = "INSERT INTO grupos (nome, criador_id) VALUES (?, ?)";
        String sqlMembro = "INSERT INTO membros_grupo (grupo_id, usuario_id, status) VALUES (?, ?, 'ACEITO')";
        
        try (Connection conn = database.getConnection()) {
            conn.setAutoCommit(false); 
            try {
                int grupoId = -1;
                try (PreparedStatement stmt = conn.prepareStatement(sqlGrupo, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, nome);
                    stmt.setInt(2, criadorId);
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) grupoId = rs.getInt(1);
                    }
                }
                if (grupoId != -1) {
                    try (PreparedStatement stmt = conn.prepareStatement(sqlMembro)) {
                        stmt.setInt(1, grupoId);
                        stmt.setInt(2, criadorId);
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

    // Método ORIGINAL (Sem buscar 'tipo')
    public Grupo buscarGrupoPorUsuario(int usuarioId) throws SQLException {
        String sql = "SELECT g.id, g.nome, g.criador_id FROM grupos g " +
                     "JOIN membros_grupo mg ON g.id = mg.grupo_id " +
                     "WHERE mg.usuario_id = ?"; // Removido filtro de status para garantir retorno
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Grupo g = new Grupo();
                    g.setId(rs.getInt("id"));
                    g.setNome(rs.getString("nome"));
                    g.setCriadorId(rs.getInt("criador_id"));
                    return g;
                }
            }
        }
        return null;
    }
}