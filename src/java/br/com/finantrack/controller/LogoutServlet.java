package br.com.finantrack.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*Controller  para Logout. Invalida a sessão HTTP e redireciona para o login*/

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false); // false = não cria nova se não existir
        if (session != null) {
            session.invalidate(); // Destrói dados do servidor
        }
        response.sendRedirect("tela_login.jsp");
    }
}