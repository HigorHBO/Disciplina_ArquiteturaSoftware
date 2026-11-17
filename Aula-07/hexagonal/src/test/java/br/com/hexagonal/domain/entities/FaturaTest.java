package br.com.hexagonal.domain.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FaturaTest {

  @Test
  @DisplayName("Deve criar fatura válida com dados obrigatórios")
  void deveCriarFaturaValida() {
    Fatura fatura = Fatura.criar(10L, new BigDecimal("100.50"));

    assertNotNull(fatura.getId());
    assertEquals(10L, fatura.getPedidoId());
    assertEquals(new BigDecimal("100.50"), fatura.getValor());
    assertNotNull(fatura.getEmitidoEm());
  }

  @Test
  @DisplayName("Deve lançar exceção ao criar fatura sem pedido ou com id inválido")
  void deveLancarExcecaoPedidoInvalido() {
    assertThrows(RuntimeException.class, () -> Fatura.criar(null, new BigDecimal("100.00")));
    assertThrows(RuntimeException.class, () -> Fatura.criar(0L, new BigDecimal("100.00")));
    assertThrows(RuntimeException.class, () -> Fatura.criar(-1L, new BigDecimal("100.00")));
  }

  @Test
  @DisplayName("Deve lançar exceção ao criar fatura com valor nulo ou menor igual a zero")
  void deveLancarExcecaoValorInvalido() {
    assertThrows(RuntimeException.class, () -> Fatura.criar(1L, null));
    assertThrows(RuntimeException.class, () -> Fatura.criar(1L, BigDecimal.ZERO));
    assertThrows(RuntimeException.class, () -> Fatura.criar(1L, new BigDecimal("-10.00")));
  }

  @Test
  @DisplayName("Deve registrar boleto, nota fiscal e hash de e-mail")
  void deveRegistrarIntegracoes() {
    Fatura fatura = Fatura.criar(1L, new BigDecimal("50.00"));

    fatura.inserirBoleto("34191.00000");
    fatura.inserirNotaFiscal("RPS-12345");
    fatura.inserirHashEmail("HASH-ABCDE");

    assertEquals("34191.00000", fatura.getCodigoBarras());
    assertEquals("RPS-12345", fatura.getRps());
    assertEquals("HASH-ABCDE", fatura.getHashEmail());
  }

}
