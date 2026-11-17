package br.com.empresa.vendas.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Pedido {

    private Long id;
    private String cliente;
    private BigDecimal valorTotal;
    private StatusPedido status;
    private LocalDateTime criadoEm;
    private UUID faturaId; // ID da fatura devolvido pelo Hub Hexagonal

    public Pedido() {
        this.status = StatusPedido.ABERTO;
        this.criadoEm = LocalDateTime.now();
    }

    public Pedido(Long id, String cliente, BigDecimal valorTotal) {
        this.id = id;
        this.cliente = cliente;
        this.valorTotal = valorTotal;
        this.status = StatusPedido.ABERTO;
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public UUID getFaturaId() {
        return faturaId;
    }

    public void setFaturaId(UUID faturaId) {
        this.faturaId = faturaId;
    }
}
