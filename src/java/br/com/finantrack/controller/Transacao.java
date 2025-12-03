package br.com.finantrack.controller;

import java.time.LocalDate;

/*Modelo que representa uma movimentação financeira (Receita ou Despesa).*/

public class Transacao {

    private int id;
    private int usuarioId;
    private String descricao;
    private double valor;
    private LocalDate data;
    private String tipo;      // 'receita' ou 'saida'
    private String categoria; // Ex: 'Alimentação', 'Transporte'
    private boolean pago;     // Status do pagamento
    private String origem;    // "Mensal" (banco) ou "Recorrente" (memória)

    // --- Getters e Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public boolean isPago() { return pago; }
    public void setPago(boolean pago) { this.pago = pago; }
    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }
}