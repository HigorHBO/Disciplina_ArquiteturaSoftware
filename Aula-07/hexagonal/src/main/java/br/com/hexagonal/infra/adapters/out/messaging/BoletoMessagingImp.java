package br.com.hexagonal.infra.adapters.out.messaging;

import java.util.Random;

import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.out.messaging.BoletoMessaging;

@Component
public class BoletoMessagingImp implements BoletoMessaging {

  @Override
  public String emitirBoleto(Fatura fatura) {
    // Simulação de publicação na fila de pedidos e geração do código de barras
    long sequencial = 10000000000L + (long) (new Random().nextDouble() * 89999999999L);
    return "34191.79001 01043.510047 91020.150008 8 " + sequencial;
  }

}
