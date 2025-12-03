package br.com.finantrack.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static String URL;

    public static void init(String webInfPath) {
        try {
            Class.forName("org.sqlite.JDBC");
            
            String projectDbPath = webInfPath + "finantrack.db";
            URL = "jdbc:sqlite:" + projectDbPath;
            try {
                try (Connection conn = getConnection()) {}
            } catch (SQLException e) {
                String userHomeDbPath = System.getProperty("user.home") + File.separator + "finantrack.db";
                URL = "jdbc:sqlite:" + userHomeDbPath;
            }

            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                
                // 1. USUÁRIOS
                stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nome TEXT NOT NULL, " +
                        "email TEXT NOT NULL UNIQUE, " +
                        "senha TEXT NOT NULL, " +
                        "salario REAL DEFAULT 0.0);");
   
                // 2. TRANSAÇÕES PESSOAIS
                stmt.execute("CREATE TABLE IF NOT EXISTS transacoes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "usuario_id INTEGER NOT NULL, " +
                        "descricao TEXT NOT NULL, " +
                        "valor REAL NOT NULL, " +
                        "data TEXT NOT NULL, " + 
                        "tipo TEXT NOT NULL, " +
                        "categoria TEXT NOT NULL, " +
                        "pago INTEGER DEFAULT 1, " + 
                        "FOREIGN KEY (usuario_id) REFERENCES usuarios(id));");
   
                // 3. CONTAS RECORRENTES
                stmt.execute("CREATE TABLE IF NOT EXISTS contas_recorrentes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "usuario_id INTEGER NOT NULL," +
                        "descricao TEXT NOT NULL," +
                        "valor REAL NOT NULL," +
                        "categoria TEXT NOT NULL," +
                        "dia_vencimento INTEGER," +
                        "data_inicio TEXT NOT NULL," +
                        "data_fim TEXT," +
                        "tipo_pagamento TEXT," +
                        "ultimo_mes_pago TEXT," +
                        "FOREIGN KEY (usuario_id) REFERENCES usuarios(id));");
                

                // 4. GRUPOS
                // admin_id: Dono grupo
                stmt.execute("CREATE TABLE IF NOT EXISTS grupos (" +
                             "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                             "nome TEXT NOT NULL, " +
                             "admin_id INTEGER NOT NULL, " +
                             "data_criacao TEXT, " +
                             "FOREIGN KEY (admin_id) REFERENCES usuarios(id));");

                // 5. MEMBROS (Grupo)
                // status: 'PENDENTE' (convidado), 'ACEITO' (membro), 'RECUSADO'
                stmt.execute("CREATE TABLE IF NOT EXISTS grupo_membros (" +
                             "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                             "grupo_id INTEGER NOT NULL, " +
                             "usuario_id INTEGER NOT NULL, " +
                             "status TEXT DEFAULT 'PENDENTE', " + 
                             "data_entrada TEXT, " +
                             "FOREIGN KEY (grupo_id) REFERENCES grupos(id), " +
                             "FOREIGN KEY (usuario_id) REFERENCES usuarios(id));");

                // 6. TRANSAÇÕES DO GRUPO 
                // usuario_pagante_id: Fica NULL enquanto ninguém pagar. Quando alguém pagar, preenchemos.
                stmt.execute("CREATE TABLE IF NOT EXISTS transacoes_grupo (" +
                             "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                             "grupo_id INTEGER NOT NULL, " +
                             "descricao TEXT NOT NULL, " +
                             "valor REAL NOT NULL, " +
                             "data_vencimento TEXT NOT NULL, " +
                             "categoria TEXT, " +
                             "usuario_pagante_id INTEGER, " +
                             "status_pagamento TEXT DEFAULT 'PENDENTE', " +
                             "FOREIGN KEY (grupo_id) REFERENCES grupos(id), " +
                             "FOREIGN KEY (usuario_pagante_id) REFERENCES usuarios(id));");
                
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