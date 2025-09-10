// Local: src/java/br/com/finantrack/util/database.java
package br.com.finantrack.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class database {

    private static String URL;

    public static void init(String webInfPath) {
        try {
            Class.forName("org.sqlite.JDBC");
            // Lógica de Plano A / Plano B para o caminho do DB... (continua a mesma)
            String projectDbPath = webInfPath + "finantrack.db";
            URL = "jdbc:sqlite:" + projectDbPath;
            try {
                try (Connection conn = getConnection()) {}
            } catch (SQLException e) {
                String userHomeDbPath = System.getProperty("user.home") + File.separator + "finantrack.db";
                URL = "jdbc:sqlite:" + userHomeDbPath;
            }

            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                // Tabela de usuários (sem alteração)
                stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, email TEXT NOT NULL UNIQUE, senha TEXT NOT NULL);");
                // Tabela de transações (sem alteração)
                stmt.execute("CREATE TABLE IF NOT EXISTS transacoes (id INTEGER PRIMARY KEY AUTOINCREMENT, usuario_id INTEGER NOT NULL, descricao TEXT NOT NULL, valor REAL NOT NULL, data DATE NOT NULL, tipo TEXT NOT NULL, categoria TEXT NOT NULL, FOREIGN KEY (usuario_id) REFERENCES usuarios(id));");

                // *** NOVA TABELA DE CONTAS RECORRENTES ***
                stmt.execute("CREATE TABLE IF NOT EXISTS contas_recorrentes (" +
                             "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                             "usuario_id INTEGER NOT NULL," +
                             "descricao TEXT NOT NULL," +
                             "valor REAL NOT NULL," +
                             "categoria TEXT NOT NULL," +
                             "dia_vencimento INTEGER NOT NULL," +
                             "data_inicio DATE NOT NULL," +
                             "data_fim DATE," + // Pode ser nulo (para contas sem fim)
                             "salario REAL DEFAULT 0.0," +
                             "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)" +
                             ");");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Falha crítica ao inicializar o banco de dados.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (URL == null) {
            throw new SQLException("A conexão com o banco de dados não foi inicializada.");
        }
        return DriverManager.getConnection(URL);
    }
}