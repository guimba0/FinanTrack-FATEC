package br.com.finantrack.controller;

import br.com.finantrack.util.database;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContaRecorrenteDAO {

    /**
     * Insere uma nova conta recorrente no banco de dados.
     * Lida com o campo 'dia_vencimento' como opcional.
     * @param conta O objeto ContaRecorrente com os dados a serem inseridos.
     * @throws SQLException
     */
    public void inserir(ContaRecorrente conta) throws SQLException {
        String sql = "INSERT INTO contas_recorrentes(usuario_id, descricao, valor, categoria, dia_vencimento, data_inicio, data_fim) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, conta.getUsuarioId());
            stmt.setString(2, conta.getDescricao());
            stmt.setDouble(3, conta.getValor());
            stmt.setString(4, conta.getCategoria());

            // Verifica se o dia de vencimento foi preenchido
            if (conta.getDiaVencimento() != null) {
                stmt.setInt(5, conta.getDiaVencimento());
            } else {
                // Se não foi, salva como NULO no banco
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            stmt.setString(6, conta.getDataInicio().toString());

            // Verifica se a data de fim foi preenchida
            if (conta.getDataFim() != null) {
                stmt.setString(7, conta.getDataFim().toString());
            } else {
                // Se não foi, salva como NULO no banco
                stmt.setNull(7, java.sql.Types.DATE);
            }
            
            stmt.executeUpdate();
        }
    }

    /**
     * Lista todas as contas recorrentes de um usuário.
     * Lida com o campo 'dia_vencimento' que pode ser nulo no banco.
     * @param usuarioId O ID do usuário.
     * @return Uma lista de objetos ContaRecorrente.
     */
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
                
                // Pega o dia de vencimento e verifica se era nulo no banco
                int diaVenc = rs.getInt("dia_vencimento");
                if (rs.wasNull()) {
                    conta.setDiaVencimento(null);
                } else {
                    conta.setDiaVencimento(diaVenc);
                }
                
                conta.setDataInicio(LocalDate.parse(rs.getString("data_inicio")));
                
                String dataFimStr = rs.getString("data_fim");
                if (dataFimStr != null) {
                    conta.setDataFim(LocalDate.parse(dataFimStr));
                } else {
                    conta.setDataFim(null);
                }
                
                contas.add(conta);
            }
        } catch (SQLException e) {
            // Imprime o erro no console do servidor para ajudar a depurar
            e.printStackTrace();
        }
        
        
        return contas;
    }
}