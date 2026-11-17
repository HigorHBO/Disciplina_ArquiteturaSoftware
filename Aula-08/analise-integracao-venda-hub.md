# Análise da Integração: Aplicação de Vendas MVC e Hub de Faturamento Hexagonal

> **Disciplina:** Arquitetura de Software  
> **Professor:** Prof. Dr. Pedro Henrique Nogueira Pizzutti  
> **Atividade:** Integração da Venda ao Hub de Faturamento e Análise de Falhas na Comunicação pela Rede

---

## 1. Contexto da Implementação

Na aplicação de vendas em MVC ([`mvc-vendas/`](mvc-vendas/)), foi implementada a rotina de fechamento de pedido com integração síncrona via HTTP/REST ao Hub de Faturamento Hexagonal:
1. O usuário/vendedor clica em **"Fechar Pedido"** (`POST /pedidos/{id}/fechar`).
2. O serviço de vendas invoca o cliente HTTP ([`HubFaturamentoClient.java`](mvc-vendas/src/main/java/br/com/empresa/vendas/client/HubFaturamentoClient.java)), enviando um `POST http://localhost:8081/fatura` com o `pedidoId` e o `valorTotal`.
3. O Hub de Faturamento (em arquitetura hexagonal) processa as regras de negócio, emite boleto por fila, emite nota fiscal via SOAP, envia e-mail via SMTP e salva no banco de dados.
4. O Hub retorna o identificador único da fatura criada (`faturaId` em formato UUID).
5. A aplicação de vendas **guarda o ID da fatura devolvido no registro do pedido** e altera seu status para `FATURADO`.

Abaixo analisamos **dois problemas reais e críticos** inerentes a essa comunicação pela rede, o impacto no negócio e as soluções arquiteturais estudadas.

---

## 2. Problema 1: Atraso na Comunicação e Falhas em Cascata

### O que acontece com o pedido?
A chamada REST é **síncrona e bloqueante**. Quando o vendedor solicita o fechamento do pedido:
- O servidor de Vendas despacha a requisição HTTP e bloqueia a thread do Tomcat/Web aguardando o retorno.
- Para responder, o Hub Hexagonal depende de terceiros externos (emissão de boleto, serviço SOAP da SEFAZ para emitir a NF-e e servidor SMTP).
- Se a SEFAZ estiver com sobrecarga e demorar 30 segundos para autorizar a nota fiscal:
  - O Hub de Faturamento fica 30 segundos bloqueado;
  - A aplicação de Vendas fica 30 segundos bloqueada;
  - Com múltiplos vendedores tentando fechar pedidos simultaneamente, **o pool de conexões e threads do servidor de Vendas se esgota completamente**;
  - O pedido não conclui, o navegador estoura com *HTTP 504 Gateway Timeout* e o pedido fica em estado incerto/travado.

### O que o negócio percebe?
- **Paralisia da operação de vendas:** O vendedor no balcão ou na central de televendas não consegue cadastrar novas vendas porque a tela congela carregando.
- **Abandono de compras:** Clientes em canais digitais desistem da compra achando que o sistema caiu.
- **Efeito Dominó (Falha em Cascata):** A instabilidade de um órgão externo (SEFAZ) ou do Hub derruba o sistema comercial da empresa, gerando prejuízo financeiro direto e imediato.

### Solução Arquitetural: Timeout Estrito e Padrão Circuit Breaker (com Fallback)
* **Onde atuaria:** Na camada de integração HTTP da aplicação de Vendas ([`HubFaturamentoClient`](mvc-vendas/src/main/java/br/com/empresa/vendas/client/HubFaturamentoClient.java)).
* **Como sana o problema:**
  1. **Timeout Estrito:** Define um limite rígido de tempo de espera (ex.: 2 segundos para conexão e 3 segundos para leitura). Se o Hub não responder nesse intervalo, a conexão é interrompida imediatamente, liberando a thread do servidor de vendas.
  2. **Circuit Breaker:** O disjuntor monitora a taxa de erros e timeouts. Se o Hub estiver degradado, o circuito muda para **ABERTO** e passa a falhar imediatamente (*Fail-Fast*) sem sequer tentar abrir conexão na rede.
  3. **Ação de Fallback:** Em vez de travar o vendedor com erro, o sistema salva o pedido com status `PENDENTE_FATURAMENTO`, libera o operador instantaneamente e agenda uma retentativa assíncrona para quando o Hub restabelecer sua saúde (circuito **MEIO-ABERTO** $\rightarrow$ **FECHADO**).

---

## 3. Problema 2: Falha na Resposta de Rede e Faturamento Duplicado (Falta de Transação Distribuída)

### O que acontece com o pedido?
A rede não é confiável (*Falácias da Computação Distribuída*). O seguinte cenário ocorre com frequência:
1. A aplicação de Vendas envia a requisição `POST /fatura` para o Hub com sucesso.
2. O Hub recebe o pedido, cria a fatura, gera o boleto, emite a NF-e na SEFAZ e persiste tudo no banco com sucesso.
3. No exato instante em que o Hub envia a resposta `HTTP 201 Created` de volta com o `faturaId`, **ocorre uma oscilação na rede** (perda de pacotes TCP ou timeout na ponta cliente).
4. A aplicação de Vendas não recebe a confirmação e assume que a requisição falhou. O pedido permanece como `ABERTO` ou vai para `ERRO_FATURAMENTO`.
5. Diante da mensagem de erro na tela, o vendedor clica novamente em **"Fechar Pedido"**, disparando uma **segunda chamada REST idêntica** para o mesmo pedido.

### O que o negócio percebe?
- **Faturamento Duplicado:** O Hub gera uma **segunda fatura**, um **segundo boleto** e uma **segunda nota fiscal** para uma mesma compra.
- **Atrito com o Cliente:** O cliente recebe dois e-mails de cobrança com dois códigos de barras diferentes para pagar, gerando desconfiança e reclamações no SAC/Suporte.
- **Prejuízos Fiscais e Contábeis:** O financeiro precisa lidar com duplicidade contábil de receitas e arcar com custos operacionais para cancelar notas fiscais emitidas indevidamente junto à Receita/Prefeitura, correndo risco de multas fiscais.

### Solução Arquitetural: Idempotência com Chave de Negócio e Padrão Transactional Outbox
* **Onde atuaria:** De forma coordenada entre o cliente de Vendas e o serviço do Hub de Faturamento:
  1. **Na Aplicação de Vendas:** O cliente REST envia um identificador único de negócio imutável (`pedidoId` no payload ou no cabeçalho HTTP `Idempotency-Key: pedido-123`).
  2. **No Hub de Faturamento:** Antes de instanciar uma nova fatura e acionar as portas de boleto e nota fiscal, o serviço consulta o repositório (`faturaRepository.buscarPorPedidoId(pedidoId)`).
     - Se a fatura já existir: o Hub não reemite boletos nem notas; ele simplesmente recupera a fatura previamente salva e devolve o mesmo `faturaId` original com status `200 OK`.
     - Se não existir: executa o faturamento normal.
  3. **Transactional Outbox / Mensageria:** Para evitar faturas geradas sem notificação em caso de quedas parciais, o Hub utiliza o padrão *Transactional Outbox*, garantindo que banco e eventos fiquem sempre atômicos.
* **Como sana o problema:**
  Garante o princípio da **Idempotência**: submeter a mesma operação 1 ou 10 vezes produz **exatamente o mesmo resultado**, eliminando cobranças duplicadas e protegendo o negócio contra instabilidades transitórias de rede.

---

## 4. Quadro Comparativo das Soluções

| Problema na Rede | Comportamento no Pedido | Impacto no Negócio | Solução Arquitetural | Onde Atua |
|---|---|---|---|---|
| **Atraso / Latência Crítica** | Thread travada esperando o Hub; estouro de timeout; pedido fica congelado | Vendas paralisadas; vendedores não atendem; queda em cascata do sistema | **Timeout Estrito + Circuit Breaker** | Na camada cliente HTTP de Vendas (`HubFaturamentoClient`) |
| **Oscilação de Rede no Retorno** | Venda não recebe o ID retornado e tenta novamente; requisição reenviada | Faturamento duplicado; cliente recebe 2 boletos e 2 notas; problemas fiscais | **Idempotência (Chave de Negócio)** | No Hub Hexagonal ao validar o `pedidoId` antes de faturar |
