package br.com.empresa.vendas.client;

import java.math.BigDecimal;

public record FaturaPostRequest(Long pedidoId, BigDecimal valor) {
}
