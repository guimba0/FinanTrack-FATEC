package br.com.finantrack.controller;

import java.time.LocalDate;

/**
 * Esta classe representa o "molde" (Model) para uma transação financeira.
 * Ela armazena todas as informações de uma receita ou despesa.
 */
public class Transacao {

    private int id;
    private int usuarioId;
    private String descricao;
    private double valor;
    private LocalDate data;
    private String tipo; // "entrada" (receita) ou "saida" (despesa)
    private String categoria;

    // Getters e Setters para todos os campos

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}