// Local: src/java/br/com/finantrack/controller/TransacaoDAO.java
package br.com.finantrack.controller;

import br.com.finantrack.util.database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransacaoDAO {

    // *** ALTERADO: Adicionado o parâmetro 'categoria' ***
    public void inserir(int usuarioId, String descricao, double valor, String data, String tipo, String categoria) throws SQLException {
        String sql = "INSERT INTO transacoes(usuario_id, descricao, valor, data, tipo, categoria) VALUES(?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setString(2, descricao);
            stmt.setDouble(3, valor);
            stmt.setString(4, data);
            stmt.setString(5, tipo);
            stmt.setString(6, categoria); // NOVO PARÂMETRO
            
            stmt.executeUpdate();
        }
    }

    // *** ALTERADO: Buscando a categoria e aumentando o array ***
    public List<String[]> buscarPorMes(int usuarioId, int mes, int ano) {
        List<String[]> transacoes = new ArrayList<>();
        // Adicionado 'categoria' no SELECT
        String sql = "SELECT id, descricao, valor, data, tipo, categoria FROM transacoes " +
                     "WHERE usuario_id = ? AND strftime('%m', data) = ? AND strftime('%Y', data) = ? " +
                     "ORDER BY data DESC";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.setString(2, String.format("%02d", mes));
            stmt.setString(3, String.valueOf(ano));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String[] transacao = new String[6]; // Array agora tem 6 posições
                transacao[0] = rs.getString("id");
                transacao[1] = rs.getString("descricao");
                transacao[2] = String.format("%.2f", rs.getDouble("valor"));
                transacao[3] = rs.getString("data");
                transacao[4] = rs.getString("tipo");
                transacao[5] = rs.getString("categoria"); // NOVA INFORMAÇÃO
                transacoes.add(transacao);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transacoes;
    }
}