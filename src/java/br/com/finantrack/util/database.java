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
   
                // 2. TRANSAÇÕES
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
                
                
                // --- NOVAS TABELAS DO SISTEMA DE GRUPOS ---

                // 4. GRUPOS (Com a coluna 'tipo' correta)
                stmt.execute("CREATE TABLE IF NOT EXISTS grupos (" +
                             "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                             "nome TEXT NOT NULL, " +
                             "tipo TEXT, " + 
                             "criador_id INTEGER NOT NULL);");

                // 5. MEMBROS DO GRUPO (Com a coluna 'status' adicionada)
                stmt.execute("CREATE TABLE IF NOT EXISTS membros_grupo (" +
                             "grupo_id INTEGER NOT NULL, " +
                             "usuario_id INTEGER NOT NULL, " +
                             "status TEXT DEFAULT 'PENDENTE', " + // Adicionado para convites
                             "PRIMARY KEY (grupo_id, usuario_id), " +
                             "FOREIGN KEY (grupo_id) REFERENCES grupos(id), " +
                             "FOREIGN KEY (usuario_id) REFERENCES usuarios(id));");

                // 6. NOTIFICAÇÕES (Para os convites)
                stmt.execute("CREATE TABLE IF NOT EXISTS notificacoes (" +
                             "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                             "usuario_destino_id INTEGER NOT NULL, " +
                             "mensagem TEXT NOT NULL, " +
                             "tipo TEXT NOT NULL, " +
                             "id_referencia INTEGER, " +
                             "lida INTEGER DEFAULT 0, " +
                             "FOREIGN KEY (usuario_destino_id) REFERENCES usuarios(id));");
                
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