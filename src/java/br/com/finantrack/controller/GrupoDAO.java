package br.com.finantrack.controller;

import br.com.finantrack.util.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAO {

    // Cria um grupo e já adiciona o criador como membro
    public void criarGrupo(String nome, int criadorId) throws SQLException {
        // Agora salva o tipo padrão se não for passado (para compatibilidade)
        criarGrupo(nome, criadorId, "Gestão Mensal");
    }

    // Método sobrecarregado para suportar o Tipo
    public void criarGrupo(String nome, int criadorId, String tipo) throws SQLException {
        String sqlGrupo = "INSERT INTO grupos (nome, criador_id, tipo) VALUES (?, ?, ?)";
        String sqlMembro = "INSERT INTO membros_grupo (grupo_id, usuario_id, status) VALUES (?, ?, 'ACEITO')";
        
        try (Connection conn = database.getConnection()) {
            conn.setAutoCommit(false); 
            
            try {
                // 1. Insere o Grupo
                int grupoId = -1;
                try (PreparedStatement stmt = conn.prepareStatement(sqlGrupo, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, nome);
                    stmt.setInt(2, criadorId);
                    stmt.setString(3, tipo);
                    stmt.executeUpdate();
                    
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            grupoId = rs.getInt(1);
                        }
                    }
                }

                // 2. Adiciona o Criador na tabela de membros
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

    // Busca o grupo que o usuário participa (CORRIGIDO PARA LER O TIPO)
    public Grupo buscarGrupoPorUsuario(int usuarioId) throws SQLException {
        String sql = "SELECT g.id, g.nome, g.criador_id, g.tipo FROM grupos g " +
                     "JOIN membros_grupo mg ON g.id = mg.grupo_id " +
                     "WHERE mg.usuario_id = ? AND mg.status = 'ACEITO'"; // Só pega grupos aceitos
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Grupo(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("criador_id"),
                        rs.getString("tipo") // Agora lê o tipo corretamente
                    );
                }
            }
        }
        return null;
    }

    public void atualizarNome(int grupoId, String novoNome) throws SQLException {
        String sql = "UPDATE grupos SET nome = ? WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoNome);
            stmt.setInt(2, grupoId);
            stmt.executeUpdate();
        }
    }

    public boolean adicionarMembroPorEmail(int grupoId, String email) throws SQLException {
        String sqlBuscaUser = "SELECT id FROM usuarios WHERE email = ?";
        int novoMembroId = -1;
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBuscaUser)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    novoMembroId = rs.getInt("id");
                } else {
                    return false;
                }
            }
        }

        // Insere como PENDENTE
        String sqlInserir = "INSERT INTO membros_grupo (grupo_id, usuario_id, status) VALUES (?, ?, 'PENDENTE')";
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlInserir)) {
            stmt.setInt(1, grupoId);
            stmt.setInt(2, novoMembroId);
            stmt.executeUpdate();
        }
        return true;
    }
    
    public List<String> listarNomesMembros(int grupoId) throws SQLException {
        List<String> nomes = new ArrayList<>();
        String sql = "SELECT u.nome FROM usuarios u " +
                     "JOIN membros_grupo mg ON u.id = mg.usuario_id " +
                     "WHERE mg.grupo_id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grupoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    nomes.add(rs.getString("nome"));
                }
            }
        }
        return nomes;
    }
}