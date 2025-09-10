package br.com.finantrack.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Versão da classe de conexão feita no estilo do exemplo ConectaBanco.java,
 * adaptada para SQLite.
 */
public class DatabaseManager {

    // A conexão ficará guardada aqui depois que o método conectar() for chamado.
    public Connection conn;

    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String PATH = "jdbc:sqlite:finantrack.db";

    /**
     * Método que abre a conexão com o banco de dados.
     * Deve ser chamado no início de cada operação no Servlet.
     */
    public void conectar() {
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(PATH);
            
            // Garante que a tabela de usuários exista
            inicializarBanco();

        } catch (ClassNotFoundException e) {
            System.err.println("ERRO CRÍTICO: Driver do SQLite não encontrado!");
            throw new RuntimeException("Driver do SQLite não encontrado. Verifique a pasta de Bibliotecas.", e);
        } catch (SQLException e) {
            System.err.println("ERRO CRÍTICO: Falha ao conectar ao banco de dados SQLite.");
            throw new RuntimeException("Falha na conexão com o banco de dados.", e);
        }
    }

    /**
     * Método que fecha a conexão.
     * Deve ser chamado no final de cada operação no Servlet.
     */
    public void desconectar() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexão com o banco.");
            e.printStackTrace();
        }
    }
    
    /**
     * Prepara o banco de dados, criando a tabela se necessário.
     */
    private void inicializarBanco() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String sqlTabela = "CREATE TABLE IF NOT EXISTS usuarios (" +
                               "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                               "nome TEXT NOT NULL," +
                               "email TEXT UNIQUE NOT NULL," +
                               "senha TEXT NOT NULL)";
            stmt.execute(sqlTabela);

            String sqlUsuario = "INSERT INTO usuarios (nome, email, senha) " +
                                "SELECT 'Usuario Teste', 'teste@email.com', '123' " +
                                "WHERE NOT EXISTS (SELECT 1 FROM usuarios)";
            stmt.execute(sqlUsuario);
        }
    }
}