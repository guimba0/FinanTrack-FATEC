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
    
    public List<ContaRecorrente> listar(int usuarioId) throws SQLException {
        List<ContaRecorrente> contas = new ArrayList<>();
        String sql = "SELECT id, usuario_id, descricao, valor, categoria, dia_vencimento, data_inicio, data_fim, tipo_pagamento, ultimo_mes_pago FROM contas_recorrentes WHERE usuario_id = ?";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ContaRecorrente c = new ContaRecorrente();
                c.setId(rs.getInt("id"));
                c.setUsuarioId(rs.getInt("usuario_id"));
                c.setDescricao(rs.getString("descricao"));
                c.setValor(rs.getDouble("valor"));
                c.setCategoria(rs.getString("categoria"));
                
                Integer diaVencimento = rs.getInt("dia_vencimento");
                if (rs.wasNull()) { 
                    diaVencimento = null; 
                }
                c.setDiaVencimento(diaVencimento);

                c.setDataInicio(LocalDate.parse(rs.getString("data_inicio")));
                
                String dataFimStr = rs.getString("data_fim");
                if (dataFimStr != null) { 
                    c.setDataFim(LocalDate.parse(dataFimStr)); 
                } else { 
                    c.setDataFim(null); 
                }
                
                c.setTipoPagamento(rs.getString("tipo_pagamento"));
                c.setUltimoMesPago(rs.getString("ultimo_mes_pago"));
                
                contas.add(c);
            }
        }
        return contas;
    }
    
    public void inserir(ContaRecorrente conta) throws SQLException {
        String sql = "INSERT INTO contas_recorrentes(usuario_id, descricao, categoria, valor, dia_vencimento, data_inicio, data_fim, tipo_pagamento, ultimo_mes_pago) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, conta.getUsuarioId());
            stmt.setString(2, conta.getDescricao());
            stmt.setString(3, conta.getCategoria());
            stmt.setDouble(4, conta.getValor());
            
            if (conta.getDiaVencimento() != null) { 
                stmt.setInt(5, conta.getDiaVencimento()); 
            } else { 
                stmt.setNull(5, java.sql.Types.INTEGER); 
            }
            
            stmt.setString(6, conta.getDataInicio().toString());

            if (conta.getDataFim() != null) { 
                stmt.setString(7, conta.getDataFim().toString()); 
            } else { 
                stmt.setNull(7, java.sql.Types.DATE); 
            }

            stmt.setString(8, conta.getTipoPagamento());
            stmt.setString(9, conta.getUltimoMesPago());
            
            stmt.executeUpdate();
        }
    }

    public void atualizar(ContaRecorrente conta) throws SQLException {
        String sql = "UPDATE contas_recorrentes SET descricao = ?, valor = ?, categoria = ?, " +
                     "dia_vencimento = ?, data_inicio = ?, data_fim = ?, tipo_pagamento = ? " +
                     "WHERE id = ? AND usuario_id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, conta.getDescricao());
            stmt.setDouble(2, conta.getValor());
            stmt.setString(3, conta.getCategoria());

            if (conta.getDiaVencimento() != null) { 
                stmt.setInt(4, conta.getDiaVencimento()); 
            } else { 
                stmt.setNull(4, java.sql.Types.INTEGER); 
            }

            stmt.setString(5, conta.getDataInicio().toString());

            if (conta.getDataFim() != null) { 
                stmt.setString(6, conta.getDataFim().toString()); 
            } else { 
                stmt.setNull(6, java.sql.Types.DATE); 
            }
            
            stmt.setString(7, conta.getTipoPagamento());
            stmt.setInt(8, conta.getId());
            stmt.setInt(9, conta.getUsuarioId());

            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM contas_recorrentes WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void marcarComoPaga(int contaId, String mesAno) throws SQLException {
        String sql = "UPDATE contas_recorrentes SET ultimo_mes_pago = ? WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mesAno);
            stmt.setInt(2, contaId);
            stmt.executeUpdate();
        }
    }
}