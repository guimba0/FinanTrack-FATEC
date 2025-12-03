package br.com.finantrack.controller;

import br.com.finantrack.util.DatabaseConnection;
import java.sql.*;

public class UsuarioDAO {
    
    /**
     * Verifica login (email e senha).
     */
    public Usuario autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String senhaBanco = rs.getString("senha");
                if (senha.equals(senhaBanco)) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /**
     * Busca um usuário pelo ID (Método que estava faltando).
     */
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /**
     * Atualiza dados do usuário (Nome, Email, Salário).
     */
    public void atualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuarios SET nome=?, email=?, salario=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getEmail());
            stmt.setDouble(3, u.getSalario());
            stmt.setInt(4, u.getId());
            
            stmt.executeUpdate();
        }
    }

    /**
     * Método auxiliar para converter ResultSet em Objeto Usuario.
     */
    private Usuario map(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setSenha(rs.getString("senha")); // Necessário para revalidar sessões se preciso
        u.setSalario(rs.getDouble("salario"));
        return u;
    }
}