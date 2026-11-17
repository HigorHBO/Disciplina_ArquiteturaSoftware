package br.com.empresa.estoque;

import br.com.empresa.estoque.model.Produto;
import br.com.empresa.estoque.model.StatusProduto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoTest {

    @Test
    @DisplayName("Ao criar produto, deve iniciar com estoque zero e status INDISPONÍVEL")
    void deveIniciarComoIndisponivel() {
        Produto p = new Produto(1L, "Fone de Ouvido", 99.00);
        assertEquals(0, p.getQuantidadeEstoque());
        assertEquals(StatusProduto.INDISPONIVEL, p.getStatus());
    }

    @Test
    @DisplayName("Quando o produto sai de zero no estoque, deve ficar DISPONÍVEL")
    void deveFicarDisponivelAoEntrarEstoque() {
        Produto p = new Produto(1L, "Mousepad", 40.00);
        p.darEntrada(5);

        assertEquals(5, p.getQuantidadeEstoque());
        assertEquals(StatusProduto.DISPONIVEL, p.getStatus());
    }

    @Test
    @DisplayName("Quando o estoque zera na saída, deve ficar INDISPONÍVEL")
    void deveFicarIndisponivelAoZerarEstoque() {
        Produto p = new Produto(1L, "Cabo HDMI", 30.00);
        p.darEntrada(3);
        assertEquals(StatusProduto.DISPONIVEL, p.getStatus());

        p.darSaida(3);
        assertEquals(0, p.getQuantidadeEstoque());
        assertEquals(StatusProduto.INDISPONIVEL, p.getStatus());
    }

    @Test
    @DisplayName("Não deve permitir saída maior que o estoque disponível")
    void naoDevePermitirSaidaSemEstoqueSuficiente() {
        Produto p = new Produto(1L, "Webcam", 200.00);
        p.darEntrada(2);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> p.darSaida(5));
        assertTrue(ex.getMessage().contains("Estoque insuficiente"));
    }

    @Test
    @DisplayName("Apenas o administrador (ADM) pode descontinuar o produto")
    void apenasAdminPodeDescontinuar() {
        Produto p = new Produto(1L, "Monitor CRT", 50.00);

        // Tentativa por usuário comum (não admin)
        assertThrows(SecurityException.class, () -> p.descontinuarPorAdmin(false));

        // Ação por administrador
        p.descontinuarPorAdmin(true);
        assertEquals(StatusProduto.DESCONTINUADO, p.getStatus());
    }

    @Test
    @DisplayName("Produto descontinuado não pode ser manipulado no estoque (nem entrada nem saída)")
    void produtoDescontinuadoNaoPodeSerManipuladoNoEstoque() {
        Produto p = new Produto(1L, "Disquete", 5.00);
        p.darEntrada(10);
        p.descontinuarPorAdmin(true);

        // Entrada bloqueada
        IllegalStateException exEntrada = assertThrows(IllegalStateException.class, () -> p.darEntrada(5));
        assertTrue(exEntrada.getMessage().contains("descontinuado"));

        // Saída bloqueada
        IllegalStateException exSaida = assertThrows(IllegalStateException.class, () -> p.darSaida(2));
        assertTrue(exSaida.getMessage().contains("descontinuado"));
    }
}
