package br.com.hexagonal.domain.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FaturaTest {

  @Test
  @DisplayName("Deve criar fatura válida com dados obrigatórios (RF01)")
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
  @DisplayName("Deve registrar boleto (RF02), chave de 44 dígitos da NF-e (RF03) e hash do e-mail (RF04)")
  void deveRegistrarIntegracoes() {
    Fatura fatura = Fatura.criar(1L, new BigDecimal("50.00"));

    // RF02: Código de barras do boleto
    fatura.inserirBoleto("34191.79001 01043.510047 91020.150008 8 98760000010");
    assertEquals("34191.79001 01043.510047 91020.150008 8 98760000010", fatura.getCodigoBarras());

    // RF03: Chave de acesso de exatamente 44 dígitos
    String chave44 = "41261112345678000195550010000000011234567890";
    fatura.inserirNotaFiscal(chave44);
    assertEquals(44, fatura.getRps().length());
    assertEquals(chave44, fatura.getChaveAcesso());

    // RF04: Hash do e-mail
    fatura.inserirHashEmail("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
    assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", fatura.getHashEmail());
  }

  @Test
  @DisplayName("Deve rejeitar chave de acesso que não tenha exatamente 44 dígitos numéricos (RF03)")
  void deveRejeitarChaveInvalida() {
    Fatura fatura = Fatura.criar(1L, new BigDecimal("50.00"));

    assertThrows(RuntimeException.class, () -> fatura.inserirNotaFiscal("123")); // Menos de 44
    assertThrows(RuntimeException.class, () -> fatura.inserirNotaFiscal("4126111234567800019555001000000001123456789A")); // Não numérico
  }

}
