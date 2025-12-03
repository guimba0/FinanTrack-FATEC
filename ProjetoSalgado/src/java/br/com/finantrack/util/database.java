// Local: src/java/br/com/finantrack/util/database.java
package br.com.finantrack.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class database {

    private static String URL;

    public static void init(String webInfPath) {
        try {
            Class.forName("org.sqlite.JDBC");
            
            // Sua lógica original para encontrar o caminho do banco de dados (mantida)
            String projectDbPath = webInfPath + "finantrack.db";
            URL = "jdbc:sqlite:" + projectDbPath;
            try {
                try (Connection conn = getConnection()) {}
            } catch (SQLException e) {
                String userHomeDbPath = System.getProperty("user.home") + File.separator + "finantrack.db";
                URL = "jdbc:sqlite:" + userHomeDbPath;
            }

            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                
                // Criação das tabelas (seu código original)
                stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                     "nome TEXT NOT NULL, " +
                                     "email TEXT NOT NULL UNIQUE, " +
                                     "senha TEXT NOT NULL, " +
                                     "salario REAL DEFAULT 0.0);");
                
                stmt.execute("CREATE TABLE IF NOT EXISTS transacoes (" +
                                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                     "usuario_id INTEGER NOT NULL, " +
                                     "descricao TEXT NOT NULL, " +
                                     "valor REAL NOT NULL, " +
                                     "data TEXT NOT NULL, " + 
                                     "tipo TEXT NOT NULL, " +
                                     "categoria TEXT NOT NULL, " +
                                     "FOREIGN KEY (usuario_id) REFERENCES usuarios(id));");

                stmt.execute("CREATE TABLE IF NOT EXISTS contas_recorrentes (" +
                                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                     "usuario_id INTEGER NOT NULL," +
                                     "descricao TEXT NOT NULL," +
                                     "valor REAL NOT NULL," +
                                     "categoria TEXT NOT NULL," +
                                     "dia_vencimento INTEGER," +
                                     "data_inicio TEXT NOT NULL," +
                                     "data_fim TEXT," +
                                     "FOREIGN KEY (usuario_id) REFERENCES usuarios(id));");

                // Chamada para o método que verifica e atualiza as colunas
                verificarEAdicionarColunas(conn);
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
    
    private static void verificarEAdicionarColunas(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();

        // 1. Verifica e adiciona a coluna 'tipo_pagamento' em contas_recorrentes
        try (ResultSet rs = meta.getColumns(null, null, "contas_recorrentes", "tipo_pagamento")) {
            if (!rs.next()) {
                System.out.println("EXECUTANDO ATUALIZAÇÃO: Adicionando coluna 'tipo_pagamento'...");
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE contas_recorrentes ADD COLUMN tipo_pagamento TEXT");
                }
                System.out.println("Coluna 'tipo_pagamento' adicionada com sucesso.");
            }
        }

        // 2. Verifica e adiciona a coluna 'ultimo_mes_pago' em contas_recorrentes
        try (ResultSet rs = meta.getColumns(null, null, "contas_recorrentes", "ultimo_mes_pago")) {
            if (!rs.next()) {
                System.out.println("EXECUTANDO ATUALIZAÇÃO: Adicionando coluna 'ultimo_mes_pago'...");
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE contas_recorrentes ADD COLUMN ultimo_mes_pago TEXT");
                }
                System.out.println("Coluna 'ultimo_mes_pago' adicionada com sucesso.");
            }
        }

        // ****** NOVA MODIFICAÇÃO ADICIONADA AQUI ******
        // 3. Verifica e adiciona a coluna 'pago' na tabela de transações
        try (ResultSet rs = meta.getColumns(null, null, "transacoes", "pago")) {
            if (!rs.next()) { // Se não encontrar a coluna...
                System.out.println("EXECUTANDO ATUALIZAÇÃO: Adicionando coluna 'pago'...");
                try (Statement stmt = conn.createStatement()) {
                    // INTEGER 1 = Pago (true), 0 = Pendente (false). O padrão é 1 (pago).
                    stmt.execute("ALTER TABLE transacoes ADD COLUMN pago INTEGER DEFAULT 1");
                }
                System.out.println("Coluna 'pago' adicionada com sucesso.");
            }
        }
    }
}