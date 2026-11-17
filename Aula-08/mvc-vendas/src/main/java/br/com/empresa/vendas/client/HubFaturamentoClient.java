package br.com.empresa.vendas.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@Component
public class HubFaturamentoClient {

    private final RestTemplate restTemplate;
    private final String hubUrl;

    public HubFaturamentoClient(
            RestTemplateBuilder builder,
            @Value("${hub.faturamento.url:http://localhost:8081/fatura}") String hubUrl) {
        // Configuração preventiva de Timeout (Conexão e Leitura)
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(3))
                .build();
        this.hubUrl = hubUrl;
    }

    /**
     * Implementação da chamada REST ao Hub de Integrações (Arquitetura Hexagonal).
     * Envia o pedido fechado e retorna o ID da fatura devolvido pelo Hub.
     */
    public UUID solicitarFaturamento(Long pedidoId, BigDecimal valor) {
        FaturaPostRequest request = new FaturaPostRequest(pedidoId, valor);
        ResponseEntity<FaturaPostResponse> response = restTemplate.postForEntity(
                hubUrl, request, FaturaPostResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return UUID.fromString(response.getBody().faturaId());
        }

        throw new RuntimeException("Erro ao comunicar com Hub de Faturamento: Status HTTP " + response.getStatusCode());
    }
}
