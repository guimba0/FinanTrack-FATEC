package br.com.finantrack.controller;

import br.com.finantrack.util.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContaRecorrenteDAO {

    /**
     * Insere uma nova conta recorrente no banco.
     */
    public void inserir(ContaRecorrente conta) throws SQLException {
        String sql = "INSERT INTO contas_recorrentes(usuario_id, descricao, valor, categoria, dia_vencimento, data_inicio, data_fim, tipo_pagamento) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, conta.getUsuarioId());
            stmt.setString(2, conta.getDescricao());
            stmt.setDouble(3, conta.getValor());
            stmt.setString(4, conta.getCategoria());
            stmt.setInt(5, conta.getDiaVencimento());
            stmt.setString(6, conta.getDataInicio().toString());
            // Trata data_fim nula
            stmt.setString(7, conta.getDataFim() != null ? conta.getDataFim().toString() : null);
            stmt.setString(8, conta.getTipoPagamento());
            stmt.executeUpdate();
        }
    }

    /**
     * Atualiza os dados de uma conta recorrente existente.
     * (Usado pelo ContaRecorrenteEditServlet)
     */
    public void atualizar(ContaRecorrente conta) throws SQLException {
        String sql = "UPDATE contas_recorrentes SET descricao=?, valor=?, categoria=?, dia_vencimento=?, data_fim=? WHERE id=?";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, conta.getDescricao());
            stmt.setDouble(2, conta.getValor());
            stmt.setString(3, conta.getCategoria());
            stmt.setInt(4, conta.getDiaVencimento());
            stmt.setString(5, conta.getDataFim() != null ? conta.getDataFim().toString() : null);
            stmt.setInt(6, conta.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Busca uma conta recorrente específica pelo ID.
     * (Necessário para a lógica de "Split" / editar deste mês em diante)
     */
    public ContaRecorrente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM contas_recorrentes WHERE id = ?";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ContaRecorrente c = mapResultSetToConta(rs);
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * Lista todas as contas recorrentes de um usuário.
     */
    public List<ContaRecorrente> listar(int usuarioId) throws SQLException {
        List<ContaRecorrente> contas = new ArrayList<>();
        String sql = "SELECT * FROM contas_recorrentes WHERE usuario_id = ?";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                contas.add(mapResultSetToConta(rs));
            }
        }
        return contas;
    }

    /**
     * Exclui uma conta recorrente (Cancelar assinatura).
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM contas_recorrentes WHERE id = ?";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Marca ou desmarca o pagamento de uma conta recorrente num mês específico.
     * Atualiza o campo 'ultimo_mes_pago' com "AAAA-MM" ou NULL.
     */
    public void marcarComoPaga(int id, String mesAno) throws SQLException {
        String sql = "UPDATE contas_recorrentes SET ultimo_mes_pago = ? WHERE id = ?";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mesAno);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    // Método auxiliar para não repetir código de mapeamento
    private ContaRecorrente mapResultSetToConta(ResultSet rs) throws SQLException {
        ContaRecorrente c = new ContaRecorrente();
        c.setId(rs.getInt("id"));
        c.setUsuarioId(rs.getInt("usuario_id"));
        c.setDescricao(rs.getString("descricao"));
        c.setValor(rs.getDouble("valor"));
        c.setCategoria(rs.getString("categoria"));
        c.setDiaVencimento(rs.getInt("dia_vencimento"));
        
        // Conversão segura de datas
        String inicio = rs.getString("data_inicio");
        if (inicio != null && !inicio.isEmpty()) c.setDataInicio(LocalDate.parse(inicio));
        
        String fim = rs.getString("data_fim");
        if (fim != null && !fim.isEmpty()) c.setDataFim(LocalDate.parse(fim));
        
        c.setTipoPagamento(rs.getString("tipo_pagamento"));
        c.setUltimoMesPago(rs.getString("ultimo_mes_pago"));
        return c;
    }
}