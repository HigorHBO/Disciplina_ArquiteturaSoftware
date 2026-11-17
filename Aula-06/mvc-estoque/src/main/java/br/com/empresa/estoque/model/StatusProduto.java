package br.com.empresa.estoque.model;

public enum StatusProduto {
    DISPONIVEL("Disponível"),
    INDISPONIVEL("Indisponível"),
    DESCONTINUADO("Descontinuado");

    private final String descricao;

    StatusProduto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
