package br.com.finantrack.controller;

public class Grupo {
    private int id;
    private String nome;
    private int criadorId;
    // Removido 'tipo' e 'descricao'

    public Grupo() {}

    public Grupo(int id, String nome, int criadorId) {
        this.id = id;
        this.nome = nome;
        this.criadorId = criadorId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getCriadorId() { return criadorId; }
    public void setCriadorId(int criadorId) { this.criadorId = criadorId; }
}