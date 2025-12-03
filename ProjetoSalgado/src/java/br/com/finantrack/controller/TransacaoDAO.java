package br.com.finantrack.controller;

import br.com.finantrack.util.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TransacaoDAO {

    public List<Transacao> listarLancamentosDoMes(int usuarioId, int ano, int mes) throws SQLException {
        List<Transacao> lancamentos = new ArrayList<>();
        lancamentos.addAll(listarTransacoesNormais(usuarioId, ano, mes));
        lancamentos.addAll(gerarTransacoesRecorrentes(usuarioId, ano, mes));
        lancamentos.sort(Comparator.comparing(Transacao::getData));
        return lancamentos;
    }

    private List<Transacao> listarTransacoesNormais(int usuarioId, int ano, int mes) throws SQLException {
        List<Transacao> transacoes = new ArrayList<>();
        String sql = "SELECT id, usuario_id, descricao, valor, data, tipo, categoria, pago FROM transacoes WHERE usuario_id = ? AND strftime('%Y', data) = ? AND strftime('%m', data) = ?";
        
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                // Transações manuais não têm tipo de pagamento, então fica nulo
                t.setTipoPagamento(null); 
                transacoes.add(t);
            }
        }
        return transacoes;
    }

    private List<Transacao> gerarTransacoesRecorrentes(int usuarioId, int ano, int mes) throws SQLException {
        List<Transacao> transacoesGeradas = new ArrayList<>();
        ContaRecorrenteDAO contaDAO = new ContaRecorrenteDAO();
        List<ContaRecorrente> contasRecorrentes = contaDAO.listar(usuarioId);
        String mesAnoVisualizado = String.format("%d-%02d", ano, mes);

        LocalDate primeiroDiaDoMes = LocalDate.of(ano, mes, 1);
        LocalDate ultimoDiaDoMes = primeiroDiaDoMes.withDayOfMonth(primeiroDiaDoMes.lengthOfMonth());

        for (ContaRecorrente conta : contasRecorrentes) {
            LocalDate dataInicioConta = conta.getDataInicio();
            LocalDate dataFimConta = conta.getDataFim();
            boolean ativaNesteMes = dataInicioConta.isBefore(ultimoDiaDoMes.plusDays(1)) &&
                                    (dataFimConta == null || dataFimConta.isAfter(primeiroDiaDoMes.minusDays(1)));

            if (ativaNesteMes) {
                Transacao transacaoRecorrente = new Transacao();
                transacaoRecorrente.setId(-conta.getId()); 
                transacaoRecorrente.setUsuarioId(usuarioId);
                transacaoRecorrente.setDescricao(conta.getDescricao());
                transacaoRecorrente.setValor(conta.getValor());
                transacaoRecorrente.setCategoria(conta.getCategoria());
                transacaoRecorrente.setTipo("saida");
                transacaoRecorrente.setOrigem("Recorrente");
                
                // ****** LINHA ADICIONADA AQUI ******
                // Pega o tipo de pagamento da conta recorrente e passa para a transação
                transacaoRecorrente.setTipoPagamento(conta.getTipoPagamento());

                boolean pagoNesteMes = mesAnoVisualizado.equals(conta.getUltimoMesPago());
                transacaoRecorrente.setPago(pagoNesteMes);

                int diaDoLancamento = (conta.getDiaVencimento() != null) ? conta.getDiaVencimento() : 1;
                if (diaDoLancamento > ultimoDiaDoMes.getDayOfMonth()) {
                    diaDoLancamento = ultimoDiaDoMes.getDayOfMonth();
                }
                transacaoRecorrente.setData(LocalDate.of(ano, mes, diaDoLancamento));
                transacoesGeradas.add(transacaoRecorrente);
            }
        }
        return transacoesGeradas;
    }

    // Os métodos inserir, atualizar, excluir etc. continuam os mesmos
    public void inserir(Transacao transacao) throws SQLException {
        String sql = "INSERT INTO transacoes(usuario_id, descricao, valor, data, tipo, categoria, pago) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transacao.getUsuarioId());
            stmt.setString(2, transacao.getDescricao());
            stmt.setDouble(3, transacao.getValor());
            stmt.setString(4, transacao.getData().toString());
            stmt.setString(5, transacao.getTipo());
            stmt.setString(6, transacao.getCategoria());
            stmt.setInt(7, transacao.isPago() ? 1 : 0);
            stmt.executeUpdate();
        }
    }
    
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM transacoes WHERE id = ?";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public void atualizar(Transacao transacao) throws SQLException {
        String sql = "UPDATE transacoes SET descricao = ?, valor = ?, data = ?, tipo = ?, categoria = ?, pago = ? " +
                     "WHERE id = ? AND usuario_id = ?";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transacao.getDescricao());
            stmt.setDouble(2, transacao.getValor());
            stmt.setString(3, transacao.getData().toString());
            stmt.setString(4, transacao.getTipo());
            stmt.setString(5, transacao.getCategoria());
            stmt.setInt(6, transacao.isPago() ? 1 : 0);
            stmt.setInt(7, transacao.getId());
            stmt.setInt(8, transacao.getUsuarioId());
            stmt.executeUpdate();
        }
    }

    public void atualizarStatus(int transacaoId, boolean pago) throws SQLException {
        String sql = "UPDATE transacoes SET pago = ? WHERE id = ?";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pago ? 1 : 0);
            stmt.setInt(2, transacaoId);
            stmt.executeUpdate();
        }
    }
}