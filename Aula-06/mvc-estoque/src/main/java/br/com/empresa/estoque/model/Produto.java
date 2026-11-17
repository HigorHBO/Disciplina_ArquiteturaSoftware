package br.com.empresa.estoque.model;

public class Produto {

    private Long id;
    private String nome;
    private double preco;
    private int quantidadeEstoque;
    private StatusProduto status;

    public Produto() {
        this.quantidadeEstoque = 0;
        this.status = StatusProduto.INDISPONIVEL;
    }

    public Produto(Long id, String nome, double preco) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = 0;
        this.status = StatusProduto.INDISPONIVEL;
    }

    /**
     * Rotina de entrada de estoque:
     * - Não pode ser realizada se o produto estiver DESCONTINUADO.
     * - Quando o produto sai de zero (estoque > 0), seu status passa para DISPONÍVEL.
     */
    public void darEntrada(int quantidade) {
        if (this.status == StatusProduto.DESCONTINUADO) {
            throw new IllegalStateException("Produto descontinuado não pode ser manipulado no estoque!");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade para entrada de estoque deve ser maior que zero!");
        }

        this.quantidadeEstoque += quantidade;

        if (this.quantidadeEstoque > 0) {
            this.status = StatusProduto.DISPONIVEL;
        }
    }

    /**
     * Rotina de saída de estoque:
     * - Não permite saída de um produto DESCONTINUADO.
     * - Não permite saída se a quantidade solicitada for maior que o estoque disponível.
     * - Quando a quantidade zera, o status do produto passa para INDISPONÍVEL.
     */
    public void darSaida(int quantidade) {
        if (this.status == StatusProduto.DESCONTINUADO) {
            throw new IllegalStateException("Produto descontinuado não pode ser manipulado no estoque!");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade para saída de estoque deve ser maior que zero!");
        }
        if (quantidade > this.quantidadeEstoque) {
            throw new IllegalStateException("Estoque insuficiente! Estoque disponível: " + this.quantidadeEstoque + ", Solicitado: " + quantidade);
        }

        this.quantidadeEstoque -= quantidade;

        if (this.quantidadeEstoque == 0) {
            this.status = StatusProduto.INDISPONIVEL;
        }
    }

    /**
     * Transição para DESCONTINUADO:
     * - Ação exclusiva do Administrador (ADM).
     * - Um produto descontinuado não poderá mais sofrer movimentação de estoque.
     */
    public void descontinuarPorAdmin(boolean isAdmin) {
        if (!isAdmin) {
            throw new SecurityException("Apenas o administrador (ADM) tem permissão para descontinuar um produto!");
        }
        this.status = StatusProduto.DESCONTINUADO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public StatusProduto getStatus() {
        return status;
    }

    public void setStatus(StatusProduto status) {
        this.status = status;
    }
}
