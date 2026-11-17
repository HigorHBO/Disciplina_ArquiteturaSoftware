package br.com.hexagonal.infra.adapters.out.smtp;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.out.smtp.EmailSmtp;

@Component
public class EmailSmtpImp implements EmailSmtp {

  /**
   * RF04 - Ao faturar um pedido, o hub deve notificar o cliente por e-mail, por SMTP.
   * Restrição: O e-mail enviado deve gerar um hash do conteúdo, guardado na fatura.
   */
  @Override
  public String enviarEmail(Fatura fatura) {
    // Conteúdo textual do e-mail simulado via protocolo SMTP
    String conteudoEmail = "Prezado Cliente, seu pedido " + fatura.getPedidoId()
        + " foi faturado no valor de R$ " + fatura.getValor()
        + ". Boleto: " + fatura.getCodigoBarras()
        + ". Chave NF-e: " + fatura.getRps();

    try {
      // Geração do hash SHA-256 do conteúdo do e-mail
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(conteudoEmail.getBytes(StandardCharsets.UTF_8));
      
      StringBuilder hexString = new StringBuilder();
      for (byte b : hashBytes) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception e) {
      return "HASH-" + System.currentTimeMillis();
    }
  }

}
