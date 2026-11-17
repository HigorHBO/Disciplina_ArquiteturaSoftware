package br.com.empresa.vendas;

import br.com.empresa.vendas.client.HubFaturamentoClient;
import br.com.empresa.vendas.model.Pedido;
import br.com.empresa.vendas.model.StatusPedido;
import br.com.empresa.vendas.service.PedidoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private HubFaturamentoClient hubFaturamentoClient;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    @DisplayName("Ao fechar um pedido, deve enviar ao hub via REST e guardar o ID da fatura devolvido")
    void deveFecharPedidoEGuardarIdFatura() {
        // Cenário: Pedido criado
        Pedido pedido = pedidoService.criarPedido("Cliente Exemplo", new BigDecimal("750.00"));
        assertEquals(StatusPedido.ABERTO, pedido.getStatus());
        assertNull(pedido.getFaturaId());

        // Mock da resposta do Hub Hexagonal
        UUID idFaturaEsperado = UUID.randomUUID();
        when(hubFaturamentoClient.solicitarFaturamento(eq(pedido.getId()), eq(new BigDecimal("750.00"))))
                .thenReturn(idFaturaEsperado);

        // Ação: Fechar pedido
        UUID idFaturaDevolvido = pedidoService.fecharPedido(pedido.getId());

        // Verificações
        assertEquals(idFaturaEsperado, idFaturaDevolvido);
        assertEquals(idFaturaEsperado, pedido.getFaturaId(), "O ID da fatura devolvido pelo hub DEVE ser guardado no pedido!");
        assertEquals(StatusPedido.FATURADO, pedido.getStatus());
    }

    @Test
    @DisplayName("Se o Hub falhar, deve marcar pedido com ERRO_FATURAMENTO")
    void deveMarcarErroQuandoHubFalhar() {
        Pedido pedido = pedidoService.criarPedido("Cliente Instável", new BigDecimal("300.00"));

        when(hubFaturamentoClient.solicitarFaturamento(any(), any()))
                .thenThrow(new RuntimeException("Timeout na conexão com o Hub"));

        assertThrows(RuntimeException.class, () -> pedidoService.fecharPedido(pedido.getId()));
        assertEquals(StatusPedido.ERRO_FATURAMENTO, pedido.getStatus());
        assertNull(pedido.getFaturaId());
    }

    @Test
    @DisplayName("Não deve permitir faturar duas vezes o mesmo pedido")
    void naoDevePermitirFaturarNovamente() {
        Pedido pedido = pedidoService.criarPedido("Cliente Duplicado", new BigDecimal("100.00"));
        UUID idFatura = UUID.randomUUID();
        when(hubFaturamentoClient.solicitarFaturamento(any(), any())).thenReturn(idFatura);

        pedidoService.fecharPedido(pedido.getId());

        assertThrows(IllegalStateException.class, () -> pedidoService.fecharPedido(pedido.getId()));
    }
}
