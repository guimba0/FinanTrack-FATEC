package br.com.finantrack.controller;

public class Usuario {

    private int id;
    private String nome;
    private String email;
    private String senha; // Usado apenas para atualização, opcional
    private double salario;

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