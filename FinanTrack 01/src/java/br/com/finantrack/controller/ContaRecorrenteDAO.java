// Local: src/java/br/com/finantrack/controller/ContaRecorrenteDAO.java
package br.com.finantrack.controller;

import br.com.finantrack.util.database;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContaRecorrenteDAO {

    public void inserir(ContaRecorrente conta) throws SQLException {
        String sql = "INSERT INTO contas_recorrentes(usuario_id, descricao, valor, categoria, dia_vencimento, data_inicio, data_fim) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, conta.getUsuarioId());
            stmt.setString(2, conta.getDescricao());
            stmt.setDouble(3, conta.getValor());
            stmt.setString(4, conta.getCategoria());
            stmt.setInt(5, conta.getDiaVencimento());
            stmt.setString(6, conta.getDataInicio().toString());
            stmt.setString(7, conta.getDataFim() != null ? conta.getDataFim().toString() : null);
            stmt.executeUpdate();
        }
    }

    public List<ContaRecorrente> listar(int usuarioId) {
        List<ContaRecorrente> contas = new ArrayList<>();
        String sql = "SELECT * FROM contas_recorrentes WHERE usuario_id = ?";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ContaRecorrente conta = new ContaRecorrente();
                conta.setId(rs.getInt("id"));
                conta.setUsuarioId(rs.getInt("usuario_id"));
                conta.setDescricao(rs.getString("descricao"));
                conta.setValor(rs.getDouble("valor"));
                conta.setCategoria(rs.getString("categoria"));
                conta.setDiaVencimento(rs.getInt("dia_vencimento"));
                conta.setDataInicio(LocalDate.parse(rs.getString("data_inicio")));
                String dataFimStr = rs.getString("data_fim");
                if (dataFimStr != null) {
                    conta.setDataFim(LocalDate.parse(dataFimStr));
                }
                contas.add(conta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contas;
    }
}