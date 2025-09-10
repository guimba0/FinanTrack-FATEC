package br.com.finantrack.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A ÚNICA classe responsável por gerenciar a conexão com o banco de dados SQLite.
 * Nome corrigido para "Database" (com D maiúsculo) seguindo as convenções do Java.
 */
public class database {

    private static final String URL = "jdbc:sqlite:finantrack.db";

    public static Connection getConnection() {
        try {
            // Passo 1: Carrega o driver JDBC do SQLite.
            Class.forName("org.sqlite.JDBC");

            // Passo 2: Conecta ao banco de dados.
            Connection conn = DriverManager.getConnection(URL);

            // Passo 3: Garante que as tabelas existam.
            inicializarBanco(conn);

            return conn;

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Falha crítica na conexão com o banco de dados SQLite.", e);
        }
    }

    private static void inicializarBanco(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // SQL para criar a tabela 'usuarios'
            String sqlCriarTabelaUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                            "nome TEXT NOT NULL," +
                                            "email TEXT UNIQUE NOT NULL," +
                                            "senha TEXT NOT NULL)";
            stmt.execute(sqlCriarTabelaUsuarios);

            // SQL para inserir um usuário de teste
            String sqlInserirUsuario = "INSERT INTO usuarios (nome, email, senha) " +
                                       "SELECT 'Usuario Teste', 'teste@email.com', '123' " +
                                       "WHERE NOT EXISTS (SELECT 1 FROM usuarios)";
            stmt.execute(sqlInserirUsuario);
            
            // SQL para criar a tabela 'transacoes'
            String sqlCriarTabelaTransacoes = "CREATE TABLE IF NOT EXISTS transacoes (" +
                                              "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                              "descricao TEXT NOT NULL," +
                                              "valor REAL NOT NULL," +
                                              "data TEXT NOT NULL," +
                                              "tipo TEXT NOT NULL," +
                                              "id_usuario INTEGER NOT NULL," +
                                              "FOREIGN KEY(id_usuario) REFERENCES usuarios(id)" +
                                              ")";
            stmt.execute(sqlCriarTabelaTransacoes);
        }
    }
}