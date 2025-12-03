package br.com.finantrack.controller;

public class Grupo {
    private int id;
    private String nome;
    private int criadorId;
    private String tipo; // NOVO CAMPO ADICIONADO

    // Construtor vazio
    public Grupo() {}

    // Construtor completo
    public Grupo(int id, String nome, int criadorId, String tipo) {
        this.id = id;
        this.nome = nome;
        this.criadorId = criadorId;
        this.tipo = tipo;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getCriadorId() { return criadorId; }
    public void setCriadorId(int criadorId) { this.criadorId = criadorId; }

    // NOVOS MÉTODOS OBRIGATÓRIOS
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}