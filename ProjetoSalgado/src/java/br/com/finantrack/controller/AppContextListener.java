// Local: src/java/br/com/finantrack/controller/AppContextListener.java
package br.com.finantrack.controller;

import br.com.finantrack.util.database;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // *** GARANTA QUE O CAMINHO AQUI SEJA "/WEB-INF/" ***
        String path = sce.getServletContext().getRealPath("/WEB-INF/");
        
        database.init(path);
        
        System.out.println("=====================================================");
        System.out.println("Banco de dados inicializado em: " + path);
        System.out.println("=====================================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}