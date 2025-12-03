package br.com.finantrack.controller;

import br.com.finantrack.util.DatabaseConnection; // Import da nova classe de conexão
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*Classe DAO (Data Access Object) para gerenciar Contas Recorrentes.
 * Responsável por todas as operações de banco de dados relacionadas a assinaturas e contas fixas (Inserir, Editar, Listar, Excluir e Controlar Pagamentos).
 */
public class ContaRecorrenteDAO {

    /* Insere uma nova conta recorrente no banco de dados.
     @param c objeto ContaRecorrente preenchido com os dados a salvar.
     @throws SQLException Em caso de erro na conexão
     */
    
    public void inserir(ContaRecorrente c) throws SQLException {
        String sql = "INSERT INTO contas_recorrentes(usuario_id, descricao, valor, categoria, dia_vencimento, data_inicio, data_fim, tipo_pagamento) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, c.getUsuarioId());
            stmt.setString(2, c.getDescricao());
            stmt.setDouble(3, c.getValor());
            stmt.setString(4, c.getCategoria());
            stmt.setInt(5, c.getDiaVencimento());
            stmt.setString(6, c.getDataInicio().toString());
            
            // Trata data_fim nula (caso a assinatura seja indeterminada)
            stmt.setString(7, c.getDataFim() != null ? c.getDataFim().toString() : null);
            stmt.setString(8, c.getTipoPagamento());
            
            stmt.executeUpdate();
        }
    }

    /* Atualiza os dados de uma conta recorrente existente.
        Usado principalmente para corrigir erros de digitação ou alterar valores.
        @param c O objeto com os dados atualizados e o ID correto.
     */
    
    public void atualizar(ContaRecorrente c) throws SQLException {
        String sql = "UPDATE contas_recorrentes SET descricao=?, valor=?, categoria=?, dia_vencimento=?, data_fim=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, c.getDescricao());
            stmt.setDouble(2, c.getValor());
            stmt.setString(3, c.getCategoria());
            stmt.setInt(4, c.getDiaVencimento());
            stmt.setString(5, c.getDataFim() != null ? c.getDataFim().toString() : null);
            stmt.setInt(6, c.getId());
            
            stmt.executeUpdate();
        }
    }

    /* Marca uma conta como "Paga" em um mês específico.
     Isso impede que o sistema mostre a conta como pendente na visualização daquele mês.
     @param id O ID da conta recorrente.
     @param mesAno A string identificadora do mês (ex: "2023-11") ou NULL para desmarcar.
     */
    
    public void marcarComoPaga(int id, String mesAno) throws SQLException {
        String sql = "UPDATE contas_recorrentes SET ultimo_mes_pago = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, mesAno);
            stmt.setInt(2, id);
            
            stmt.executeUpdate();
        }
    }

    /*Busca uma conta específica pelo ID.
        Essencial para a lógica de "Split" (edição parcial) no Controller.
     */
    public ContaRecorrente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM contas_recorrentes WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConta(rs);
                }
            }
        }
        return null;
    }

    /* Lista todas as contas recorrentes de um usuário.
        O filtro de data (se está ativa ou não) geralmente é feito na memória pelo TransacaoDAO.
     */
    public List<ContaRecorrente> listar(int usuarioId) throws SQLException {
        List<ContaRecorrente> lista = new ArrayList<>();
        String sql = "SELECT * FROM contas_recorrentes WHERE usuario_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                lista.add(mapResultSetToConta(rs));
            }
        }
        return lista;
    }

    /* Exclui permanentemente uma conta recorrente.*/
    
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM contas_recorrentes WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /* Método auxiliar privado para converter uma linha do banco (ResultSet) em um objeto Java.
         Evita repetição de código nos métodos de busca. */
    
    private ContaRecorrente mapResultSetToConta(ResultSet rs) throws SQLException {
        ContaRecorrente c = new ContaRecorrente();
        c.setId(rs.getInt("id"));
        c.setUsuarioId(rs.getInt("usuario_id"));
        c.setDescricao(rs.getString("descricao"));
        c.setValor(rs.getDouble("valor"));
        c.setCategoria(rs.getString("categoria"));
        c.setDiaVencimento(rs.getInt("dia_vencimento"));
        
        // Conversão segura de Strings de data para LocalDate
        String inicio = rs.getString("data_inicio");
        if (inicio != null && !inicio.isEmpty()) c.setDataInicio(LocalDate.parse(inicio));
        
        String fim = rs.getString("data_fim");
        if (fim != null && !fim.isEmpty()) c.setDataFim(LocalDate.parse(fim));
        
        c.setTipoPagamento(rs.getString("tipo_pagamento"));
        c.setUltimoMesPago(rs.getString("ultimo_mes_pago"));
        
        return c;
    }
}