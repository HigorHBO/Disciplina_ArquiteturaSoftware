package br.com.empresa.vendas.model;

public enum StatusPedido {
    ABERTO("Aberto"),
    FECHADO("Fechado"),
    FATURADO("Faturado no Hub"),
    ERRO_FATURAMENTO("Erro no Faturamento");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
