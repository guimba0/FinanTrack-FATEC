package br.com.finantrack.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Esta é a classe responsável por gerenciar a conexão com o banco de dados SQLite.
 * Ela fornece um método estático para obter uma conexão, o que é mais seguro e
 * eficiente para uma aplicação web.
 */
public class database {

    // Define o "endereço" do nosso banco de dados. Para o SQLite, é o nome do arquivo.
    private static final String URL = "jdbc:sqlite:finantrack.db";

    /**
     * O método principal que os Servlets chamarão para obter uma conexão com o banco.
     * @return Uma conexão ativa com o banco de dados.
     */
    public static Connection getConnection() {
        try {
            // Passo 1: Carrega o driver JDBC do SQLite. Se o .jar não estiver nas
            // bibliotecas, esta linha dará um erro de "ClassNotFoundException".
            Class.forName("org.sqlite.JDBC");

            // Passo 2: Tenta se conectar ao banco de dados. Se o arquivo 'finantrack.db'
            // não existir, ele será criado automaticamente nesta etapa.
            Connection conn = DriverManager.getConnection(URL);

            // Passo 3: Garante que a tabela de usuários exista e tenha dados de teste.
            // Este método é chamado para configurar o banco na primeira execução.
            inicializarBanco(conn);

            // Retorna a conexão com sucesso.
            return conn;

        } catch (ClassNotFoundException | SQLException e) {
            // Se qualquer passo acima falhar, a aplicação vai parar e mostrar o erro
            // detalhado no console do servidor (NetBeans), o que nos ajuda a depurar.
            e.printStackTrace();
            throw new RuntimeException("Falha crítica na conexão com o banco de dados SQLite.", e);
        }
    }

    /**
     * Método auxiliar privado para criar a tabela 'usuarios' e inserir um usuário
     * de teste se o banco de dados estiver completamente vazio.
     * @param conn A conexão ativa com o banco.
     * @throws SQLException
     */
    private static void inicializarBanco(Connection conn) throws SQLException {
        // 'try-with-resources' garante que o 'Statement' seja fechado automaticamente
        // após a execução, evitando vazamento de recursos.
        try (Statement stmt = conn.createStatement()) {
            
            // SQL para criar a tabela 'usuarios' apenas se ela ainda não existir.
            String sqlCriarTabela = "CREATE TABLE IF NOT EXISTS usuarios (" +
                                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                    "nome TEXT NOT NULL," +
                                    "email TEXT UNIQUE NOT NULL," +
                                    "senha TEXT NOT NULL)";
            stmt.execute(sqlCriarTabela);

            // SQL para inserir um usuário padrão, mas apenas se a tabela estiver vazia.
            String sqlInserirUsuario = "INSERT INTO usuarios (nome, email, senha) " +
                                       "SELECT 'Usuario Teste', 'teste@email.com', '123' " +
                                       "WHERE NOT EXISTS (SELECT 1 FROM usuarios)";
            stmt.execute(sqlInserirUsuario);
        }
    }
}