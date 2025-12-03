package br.com.finantrack.controller;

import br.com.finantrack.util.DatabaseConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/* usado para garantir que o banco de dados (SQLite) seja criado corretamente antes que qualquer usuário tente acessar o sistema. */

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Obtém o caminho físico da pasta WEB-INF
        String path = sce.getServletContext().getRealPath("/WEB-INF/");
        System.out.println(">>> FinanTrack: Inicializando sistema...");
        
        try {
            // Chama o inicializador do banco
            DatabaseConnection.init(path);
            System.out.println(">>> FinanTrack: Banco de dados conectado/criado em " + path);
        } catch (Exception e) {
            System.err.println(">>> FinanTrack: FALHA FATAL AO INICIAR BANCO.");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Opcional: Limpeza de recursos ao desligar o servidor
    }
}