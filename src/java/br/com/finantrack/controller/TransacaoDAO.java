package br.com.finantrack.controller;

import br.com.finantrack.util.DatabaseConnection; // Novo import
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*DAO responsável por listar, inserir, editar e excluir Transações (Também mescla as contas recorrentes na listagem mensal.)*/

public class TransacaoDAO {

    public List<Transacao> listarLancamentosDoMes(int usuarioId, int ano, int mes) throws SQLException {
        List<Transacao> lista = new ArrayList<>();
        lista.addAll(listarTransacoesBanco(usuarioId, ano, mes));
        lista.addAll(projetarRecorrentes(usuarioId, ano, mes));
        lista.sort(Comparator.comparing(Transacao::getData));
        return lista;
    }

    private List<Transacao> listarTransacoesBanco(int usuarioId, int ano, int mes) throws SQLException {
        List<Transacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM transacoes WHERE usuario_id = ? AND strftime('%Y', data) = ? AND strftime('%m', data) = ?";
        
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setString(2, String.valueOf(ano));
            stmt.setString(3, String.format("%02d", mes));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Transacao t = new Transacao();
                t.setId(rs.getInt("id"));
                t.setUsuarioId(rs.getInt("usuario_id"));
                t.setDescricao(rs.getString("descricao"));
                t.setValor(rs.getDouble("valor"));
                t.setData(LocalDate.parse(rs.getString("data")));
                t.setTipo(rs.getString("tipo")); 
                t.setCategoria(rs.getString("categoria"));
                t.setPago(rs.getInt("pago") == 1);
                t.setOrigem("Mensal");
                lista.add(t);
            }
        }
        return lista;
    }

    private List<Transacao> projetarRecorrentes(int usuarioId, int ano, int mes) throws SQLException {
        List<Transacao> projetadas = new ArrayList<>();
        List<ContaRecorrente> recorrentes = new ContaRecorrenteDAO().listar(usuarioId);
        
        LocalDate inicioMes = LocalDate.of(ano, mes, 1);
        LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());
        String mesAnoAtual = String.format("%d-%02d", ano, mes);

        for (ContaRecorrente c : recorrentes) {
            // Verifica vigência
            boolean ativa = c.getDataInicio().isBefore(fimMes.plusDays(1)) &&
                            (c.getDataFim() == null || c.getDataFim().isAfter(inicioMes.minusDays(1)));

            if (ativa) {
                Transacao t = new Transacao();
                t.setId(-c.getId()); // ID Negativo para diferenciar
                t.setUsuarioId(usuarioId);
                t.setDescricao(c.getDescricao());
                t.setValor(c.getValor());
                t.setCategoria(c.getCategoria());
                t.setTipo("saida"); 
                t.setOrigem("Recorrente");
                
                // Verifica se já foi paga neste mês específico
                t.setPago(mesAnoAtual.equals(c.getUltimoMesPago()));

                // Ajusta dia de vencimento (ex: dia 31 em mês de 30 dias)
                int dia = c.getDiaVencimento();
                if (dia > fimMes.getDayOfMonth()) dia = fimMes.getDayOfMonth();
                t.setData(LocalDate.of(ano, mes, dia));
                
                projetadas.add(t);
            }
        }
        return projetadas;
    }

    public void inserir(Transacao t) throws SQLException {
        String sql = "INSERT INTO transacoes(usuario_id, descricao, valor, data, tipo, categoria, pago) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, t.getUsuarioId());
            stmt.setString(2, t.getDescricao());
            stmt.setDouble(3, t.getValor());
            stmt.setString(4, t.getData().toString());
            stmt.setString(5, t.getTipo());
            stmt.setString(6, t.getCategoria());
            stmt.setInt(7, t.isPago() ? 1 : 0);
            stmt.executeUpdate();
        }
    }
    
    public void atualizar(Transacao t) throws SQLException {
        String sql = "UPDATE transacoes SET descricao=?, valor=?, data=?, tipo=?, categoria=?, pago=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getDescricao());
            stmt.setDouble(2, t.getValor());
            stmt.setString(3, t.getData().toString());
            stmt.setString(4, t.getTipo());
            stmt.setString(5, t.getCategoria());
            stmt.setInt(6, t.isPago() ? 1 : 0);
            stmt.setInt(7, t.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM transacoes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void atualizarStatus(int id, boolean pago) throws SQLException {
        String sql = "UPDATE transacoes SET pago = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pago ? 1 : 0);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
}