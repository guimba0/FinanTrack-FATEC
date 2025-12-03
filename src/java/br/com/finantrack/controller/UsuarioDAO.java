package br.com.finantrack.controller;

import br.com.finantrack.util.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    /**
     * Busca um usuário no banco de dados pelo seu ID.
     * @param usuarioId O ID do usuário a ser buscado.
     * @return um objeto Usuario com os dados encontrados, ou null se não encontrar.
     * @throws SQLException
     */
    public Usuario buscarPorId(int usuarioId) throws SQLException {
        String sql = "SELECT id, nome, email, salario FROM usuarios WHERE id = ?";
        Usuario usuario = null;
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSalario(rs.getDouble("salario"));
                }
            }
        }
        return usuario;
    }

    /**
     * Atualiza os dados de um usuário no banco de dados.
     * @param usuario O objeto Usuario com as informações a serem atualizadas.
     * @throws SQLException
     */
    public void atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ?, email = ?, salario = ? WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setDouble(3, usuario.getSalario());
            stmt.setInt(4, usuario.getId());
            stmt.executeUpdate();
        }
    }
}