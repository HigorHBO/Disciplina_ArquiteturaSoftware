package br.com.empresa.estoque.service;

import br.com.empresa.estoque.model.Produto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProdutoService {

    private final Map<Long, Produto> repositorio = new LinkedHashMap<>();
    private final AtomicLong geradorId = new AtomicLong(1);

    public ProdutoService() {
        // Dados iniciais didáticos para visualização e testes
        Produto p1 = cadastrarProduto("Notebook Dell Inspiron", 3500.00);
        darEntrada(p1.getId(), 10); // Nasce indisponível, ganha estoque e passa para DISPONÍVEL

        Produto p2 = cadastrarProduto("Teclado Mecânico RGB", 249.90);
        // p2 permanece com estoque 0 -> INDISPONÍVEL

        Produto p3 = cadastrarProduto("Mouse Gamer Antigo", 89.90);
        darEntrada(p3.getId(), 2);
        descontinuar(p3.getId(), true); // Descontinuado pelo ADM -> Não pode movimentar
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(repositorio.values());
    }

    public Produto buscarPorId(Long id) {
        Produto produto = repositorio.get(id);
        if (produto == null) {
            throw new NoSuchElementException("Produto com id " + id + " não encontrado.");
        }
        return produto;
    }

    public Produto cadastrarProduto(String nome, double preco) {
        Long id = geradorId.getAndIncrement();
        Produto novoProduto = new Produto(id, nome, preco);
        repositorio.put(id, novoProduto);
        return novoProduto;
    }

    public void darEntrada(Long id, int quantidade) {
        Produto produto = buscarPorId(id);
        produto.darEntrada(quantidade);
    }

    public void darSaida(Long id, int quantidade) {
        Produto produto = buscarPorId(id);
        produto.darSaida(quantidade);
    }

    public void descontinuar(Long id, boolean isAdmin) {
        Produto produto = buscarPorId(id);
        produto.descontinuarPorAdmin(isAdmin);
    }
}
