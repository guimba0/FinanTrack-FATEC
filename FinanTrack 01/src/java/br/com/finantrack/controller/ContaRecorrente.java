// Local: src/java/br/com/finantrack/controller/ContaRecorrente.java
package br.com.finantrack.controller;

import java.time.LocalDate;

// Esta classe é apenas um "molde" para organizar os dados.
// Ela não tem lógica complexa, apenas armazena as informações.
public class ContaRecorrente {

    private int id;
    private int usuarioId;
    private String descricao;
    private double valor;
    private String categoria;
    private int diaVencimento;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    // Getters e Setters para todos os campos...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getDiaVencimento() { return diaVencimento; }
    public void setDiaVencimento(int diaVencimento) { this.diaVencimento = diaVencimento; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
}