package br.com.hexagonal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.hexagonal.domain.ports.in.FaturaService;

@SpringBootTest
class HexagonalApplicationTests {

  @Autowired
  private FaturaService faturaService;

  @Test
  void contextLoads() {
  }

  @Test
  void deveFaturarPedidoComSucesso() {
    UUID faturaId = faturaService.faturarPedido(100L, new BigDecimal("250.00"));
    assertNotNull(faturaId);
  }

}
