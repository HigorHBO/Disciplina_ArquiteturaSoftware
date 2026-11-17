package br.com.hexagonal.infra.adapters.out.soap;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.out.soap.NotaFiscalSoap;

@Component
public class NotaFiscalSoapImp implements NotaFiscalSoap {

  @Override
  public String emitirNota(Fatura fatura) {
    // Simulação de chamada SOAP (XML) para o emissor fiscal / prefeitura
    return "RPS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }

}
