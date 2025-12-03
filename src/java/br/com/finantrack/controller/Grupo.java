package br.com.finantrack.controller;

public class Grupo {
    private int id;
    private String nome;
    private int adminId; // Campo novo que estava faltando
    private String tipo;

    // Construtores
    public Grupo() {}

    public Grupo(int id, String nome, int adminId) {
        this.id = id;
        this.nome = nome;
        this.adminId = adminId;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}