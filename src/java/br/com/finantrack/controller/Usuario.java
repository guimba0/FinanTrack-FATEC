package br.com.finantrack.controller;

import java.io.Serializable;

/**
 * Representa um usuário do sistema (POJO).
 * Implementa Serializable para boas práticas de sessão.
 */
public class Usuario implements Serializable {

    private int id;
    private String nome;
    private String email;
    private String senha;
    private double salario;

    public Usuario() {
    }
    
    // Construtor utilitário
    public Usuario(int id, String nome, String email, double salario) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.salario = salario;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }
}