package br.com.hexagonal.infra.adapters.out.soap;

import java.util.Random;

import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.out.soap.NotaFiscalSoap;

@Component
public class NotaFiscalSoapImp implements NotaFiscalSoap {

  /**
   * RF03 - Ao faturar um pedido, o hub deve emitir a nota fiscal, por SOAP.
   * Restrição: A nota emitida deve gerar uma chave de acesso de 44 dígitos, guardada na fatura.
   */
  @Override
  public String emitirNota(Fatura fatura) {
    // Simulação de chamada SOAP/XML para o serviço fiscal emissor de NF-e
    // Padrão da Chave de Acesso da NF-e (44 dígitos numéricos):
    // cUF (2) + AAMM (4) + CNPJ (14) + mod (2) + serie (3) + nNF (9) + tpEmis (1) + cNF (8) + cDV (1) = 44 dígitos
    StringBuilder chave = new StringBuilder();
    chave.append("41"); // cUF: Paraná (2)
    chave.append("2611"); // AAMM: 2026/11 (4)
    chave.append("12345678000195"); // CNPJ: 14 dígitos (14)
    chave.append("55"); // mod: Modelo 55 (2)
    chave.append("001"); // serie: Série 001 (3)

    // nNF: 9 dígitos para o número da nota / pedido (9)
    String pedidoFormatado = String.format("%09d", fatura.getPedidoId() != null ? fatura.getPedidoId() : 1);
    chave.append(pedidoFormatado);

    chave.append("1"); // tpEmis: Emissão normal (1)

    // cNF: 8 dígitos de código aleatório (8)
    Random random = new Random();
    int codigoAleatorio = 10000000 + random.nextInt(90000000);
    chave.append(codigoAleatorio);

    // cDV: 1 dígito verificador (1)
    chave.append(random.nextInt(10));

    // Validação de segurança: garantir exatamente 44 dígitos
    String resultado = chave.toString();
    if (resultado.length() != 44) {
      throw new IllegalStateException("Erro interno ao gerar chave da NF-e: tamanho " + resultado.length());
    }

    return resultado;
  }

}
