package br.com.finantrack.controller;

import java.time.LocalDate;

public class ContaRecorrente {
    private int id;
    private int usuarioId;
    private String descricao;
    private double valor;
    private String categoria;
    private Integer diaVencimento;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    // NOVOS CAMPOS ADICIONADOS
    private String tipoPagamento; // "Boleto" ou "Debito Automatico"
    private String ultimoMesPago; // Formato "AAAA-MM"

    // Getters e Setters para todos os campos, incluindo os novos...

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
    public Integer getDiaVencimento() { return diaVencimento; }
    public void setDiaVencimento(Integer diaVencimento) { this.diaVencimento = diaVencimento; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    // GETTERS E SETTERS PARA OS NOVOS CAMPOS
    public String getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(String tipoPagamento) { this.tipoPagamento = tipoPagamento; }
    public String getUltimoMesPago() { return ultimoMesPago; }
    public void setUltimoMesPago(String ultimoMesPago) { this.ultimoMesPago = ultimoMesPago; }
}