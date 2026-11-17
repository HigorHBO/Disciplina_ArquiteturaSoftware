package br.com.empresa.vendas.service;

import br.com.empresa.vendas.client.HubFaturamentoClient;
import br.com.empresa.vendas.model.Pedido;
import br.com.empresa.vendas.model.StatusPedido;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PedidoService {

    private final Map<Long, Pedido> pedidos = new LinkedHashMap<>();
    private final AtomicLong geradorId = new AtomicLong(1);
    private final HubFaturamentoClient hubFaturamentoClient;

    public PedidoService(HubFaturamentoClient hubFaturamentoClient) {
        this.hubFaturamentoClient = hubFaturamentoClient;

        // Pedidos iniciais didáticos para demonstração
        criarPedido("Livraria Cultura Curitiba", new BigDecimal("450.00"));
        criarPedido("Distribuidora Alvorada", new BigDecimal("1890.50"));
    }

    public List<Pedido> listarTodos() {
        return new ArrayList<>(pedidos.values());
    }

    public Pedido buscarPorId(Long id) {
        Pedido pedido = pedidos.get(id);
        if (pedido == null) {
            throw new NoSuchElementException("Pedido com id " + id + " não encontrado.");
        }
        return pedido;
    }

    public Pedido criarPedido(String cliente, BigDecimal valorTotal) {
        if (valorTotal == null || valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do pedido deve ser maior que zero!");
        }
        Long id = geradorId.getAndIncrement();
        Pedido pedido = new Pedido(id, cliente, valorTotal);
        pedidos.put(id, pedido);
        return pedido;
    }

    /**
     * ATIVIDADE AULA 8:
     * Ao fechar um pedido, a venda envia o pedido ao hub e guarda o ID da fatura devolvido.
     */
    public UUID fecharPedido(Long pedidoId) {
        Pedido pedido = buscarPorId(pedidoId);

        if (pedido.getStatus() == StatusPedido.FATURADO) {
            throw new IllegalStateException("O pedido " + pedidoId + " já foi faturado anteriormente (Fatura ID: " + pedido.getFaturaId() + ")");
        }

        try {
            // Chamada REST ao Hub Hexagonal (POST /fatura)
            UUID faturaId = hubFaturamentoClient.solicitarFaturamento(pedido.getId(), pedido.getValorTotal());

            // Guarda o ID da fatura devolvido no pedido
            pedido.setFaturaId(faturaId);
            pedido.setStatus(StatusPedido.FATURADO);

            return faturaId;
        } catch (Exception e) {
            pedido.setStatus(StatusPedido.ERRO_FATURAMENTO);
            throw new RuntimeException("Falha ao fechar e faturar pedido junto ao Hub: " + e.getMessage(), e);
        }
    }
}
