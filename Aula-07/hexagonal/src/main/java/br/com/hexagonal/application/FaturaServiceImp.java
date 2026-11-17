package br.com.hexagonal.application;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.in.FaturaService;
import br.com.hexagonal.domain.ports.out.messaging.BoletoMessaging;
import br.com.hexagonal.domain.ports.out.persistence.FaturaRepository;
import br.com.hexagonal.domain.ports.out.smtp.EmailSmtp;
import br.com.hexagonal.domain.ports.out.soap.NotaFiscalSoap;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class FaturaServiceImp implements FaturaService {

  private final FaturaRepository faturaRepository;
  private final BoletoMessaging boletoMessaging;
  private final NotaFiscalSoap notaFiscalSoap;
  private final EmailSmtp emailSmtp;

  @Override
  public UUID faturarPedido(Long pedidoId, BigDecimal valor) {
    // 1. Cria a entidade e valida as regras de negócio de domínio
    Fatura fatura = Fatura.criar(pedidoId, valor);

    // 2. Emite o boleto através da porta de saída de mensageria
    String codigoBarras = boletoMessaging.emitirBoleto(fatura);
    fatura.inserirBoleto(codigoBarras);

    // 3. Emite a nota fiscal através da porta de saída SOAP
    String rps = notaFiscalSoap.emitirNota(fatura);
    fatura.inserirNotaFiscal(rps);

    // 4. Dispara e-mail com boleto e nota através da porta de saída SMTP
    String hashEmail = emailSmtp.enviarEmail(fatura);
    fatura.inserirHashEmail(hashEmail);

    // 5. Salva a fatura no banco através da porta de persistência
    faturaRepository.salvar(fatura);

    return fatura.getId();
  }
  
}
