package br.com.finantrack.controller;

/**
 * Classe Modelo que representa um Grupo de Finanças Compartilhadas.
 * Mapeia os dados da tabela 'grupos'.
 * O sistema permite que um usuário (admin) crie grupos e convide outros membros
 * para gerenciar despesas em conjunto (ex: República, Viagem, Família).
 */
public class Grupo {
    
    private int id;
    private String nome;
    
    /**
     * ID do usuário que criou o grupo. 
     * Ele possui privilégios administrativos (como convidar ou remover membros).
     */
    private int adminId; 
    
    private String tipo; // Campo para categorização futura (ex: "Casa", "Trabalho")

    // --- Construtores ---
    
    public Grupo() {
    }

    public Grupo(int id, String nome, int adminId) {
        this.id = id;
        this.nome = nome;
        this.adminId = adminId;
    }

    // --- Getters e Setters ---
    
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