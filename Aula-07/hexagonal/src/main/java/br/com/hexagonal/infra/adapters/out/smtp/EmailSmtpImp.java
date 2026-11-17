package br.com.hexagonal.infra.adapters.out.smtp;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.out.smtp.EmailSmtp;

@Component
public class EmailSmtpImp implements EmailSmtp {

  @Override
  public String enviarEmail(Fatura fatura) {
    // Simulação de envio de e-mail ao cliente com boleto e nota anexados via SMTP
    return "HASH-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
  }

}
