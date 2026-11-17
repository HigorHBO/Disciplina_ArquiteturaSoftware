# Aula 6 — Estruturas Dinâmicas & Módulo de Estoque

> **Disciplina:** Arquitetura de Software  
> **Professor:** Prof. Dr. Pedro Henrique Nogueira Pizzutti  
> **Atividade:** Implementação do Módulo de Estoque no padrão MVC e Modelagem Dinâmica (Sequência, Atividades e Estados).

---

## 1. Atividade: Módulo de Estoque (Implementação MVC)

### Descrição e Regras de Negócio
No sistema de vendas e estoque:
1. **Status do Produto:** O produto possui os status `DISPONIVEL`, `INDISPONIVEL` e `DESCONTINUADO`.
2. **Criação:** Ao criar um produto, ele começa com estoque 0 e status **`INDISPONIVEL`**.
3. **Exclusividade do Administrador:** Apenas o **ADM** pode descontinuar um produto.
4. **Bloqueio de Manipulação:** Um produto com status **`DESCONTINUADO`** **não pode** ser manipulado no estoque (rejeita entrada e saída).
5. **Transições de Estoque:**
   - **Entrada:** Quando o estoque sai de zero (estoque > 0), o status passa para **`DISPONIVEL`**.
   - **Saída:** Quando o estoque zera (estoque == 0), o status passa para **`INDISPONIVEL`**.
   - **Validação de Saldo:** Não é permitida saída sem estoque disponível suficiente.

### Projeto MVC Implementado
O código-fonte completo está localizado na pasta [`mvc-estoque/`](mvc-estoque/):
- **Modelo:** [`Produto.java`](mvc-estoque/src/main/java/br/com/empresa/estoque/model/Produto.java) e [`StatusProduto.java`](mvc-estoque/src/main/java/br/com/empresa/estoque/model/StatusProduto.java) contendo as regras de negócio e validações de invariantes.
- **Serviço:** [`ProdutoService.java`](mvc-estoque/src/main/java/br/com/empresa/estoque/service/ProdutoService.java) gerenciando operações de catálogo e movimentação.
- **Controllers:**
  - [`ProdutoController.java`](mvc-estoque/src/main/java/br/com/empresa/estoque/controller/ProdutoController.java): Tela e rotina de produtos (`/produtos`), cadastro inicial e descontinuação por ADM.
  - [`EstoqueController.java`](mvc-estoque/src/main/java/br/com/empresa/estoque/controller/EstoqueController.java): Tela e rotina de movimentação de estoque (`/estoque`).
- **Telas Thymeleaf:**
  - [`produtos.html`](mvc-estoque/src/main/resources/templates/produtos.html): Listagem com badges de status, botão de descontinuar exclusivo de ADM e formulário de cadastro.
  - [`estoque.html`](mvc-estoque/src/main/resources/templates/estoque.html): Formulário de lançamento de entrada/saída com feedback visual de validações.
- **Testes Automatizados:** [`ProdutoTest.java`](mvc-estoque/src/test/java/br/com/empresa/estoque/ProdutoTest.java) cobrindo 100% das transições de status e regras de negócio.

---

## 2. Diagrama de Sequência: Rotina de Entrada e Saída de Estoque

Arquivo fonte: [`diagrama-sequencia-estoque.mermaid`](diagrama-sequencia-estoque.mermaid) / [`diagrama-sequencia-estoque.mmd`](diagrama-sequencia-estoque.mmd)

```mermaid
sequenceDiagram
    actor Usuário as Operador de Estoque
    participant Controller as EstoqueController
    participant Service as ProdutoService
    participant Produto as Produto (Entidade)

    %% Rotina de Entrada de Estoque
    rect rgb(240, 248, 255)
        Note over Usuário,Produto: 1. Rotina de Entrada de Estoque
        Usuário->>Controller: POST /estoque/movimentar (produtoId, "ENTRADA", quantidade)
        activate Controller
        Controller->>Service: darEntrada(produtoId, quantidade)
        activate Service
        Service->>Service: buscarPorId(produtoId)
        Service->>Produto: darEntrada(quantidade)
        activate Produto
        Note over Produto: Valida se NÃO é Descontinuado<br/>Soma quantidade<br/>Se estoque > 0 -> Status = DISPONÍVEL
        Produto-->>Service: confirmação
        deactivate Produto
        Service-->>Controller: void
        deactivate Service
        Controller-->>Usuário: redirect /estoque (com mensagem de sucesso)
        deactivate Controller
    end

    %% Rotina de Saída de Estoque
    rect rgb(255, 245, 245)
        Note over Usuário,Produto: 2. Rotina de Saída de Estoque
        Usuário->>Controller: POST /estoque/movimentar (produtoId, "SAIDA", quantidade)
        activate Controller
        Controller->>Service: darSaida(produtoId, quantidade)
        activate Service
        Service->>Service: buscarPorId(produtoId)
        Service->>Produto: darSaida(quantidade)
        activate Produto
        Note over Produto: Valida se NÃO é Descontinuado<br/>Valida se saldo >= quantidade<br/>Subtrai quantidade<br/>Se estoque == 0 -> Status = INDISPONÍVEL
        Produto-->>Service: confirmação
        deactivate Produto
        Service-->>Controller: void
        deactivate Service
        Controller-->>Usuário: redirect /estoque (com mensagem de sucesso)
        deactivate Controller
    end
```

---

## 3. Diagrama de Atividades: Fluxo de Saída de Estoque

Validação restritiva: impede a saída se o produto estiver descontinuado ou se a quantidade solicitada for maior que o saldo em estoque.

Arquivo fonte: [`diagrama-atividade-saida-estoque.mermaid`](diagrama-atividade-saida-estoque.mermaid) / [`diagrama-atividade-saida-estoque.mmd`](diagrama-atividade-saida-estoque.mmd)

```mermaid
flowchart TD
    Inicio(( )) --> Recebe[Recebe solicitação de saída de estoque: produtoId e quantidade]
    Recebe --> Busca[Busca produto no repositório]
    Busca --> ValidaDescontinuado{Produto está DESCONTINUADO?}
    
    ValidaDescontinuado -- [Sim] --> ErroDescontinuado[Recusa: Produto descontinuado não pode ser manipulado no estoque]
    ErroDescontinuado --> Fim(( ))

    ValidaDescontinuado -- [Não] --> ValidaSaldo{Quantidade <= Estoque disponível?}
    
    ValidaSaldo -- [Não] --> ErroSaldo[Recusa: Sem estoque disponível suficiente]
    ErroSaldo --> Fim

    ValidaSaldo -- [Sim] --> Subtrai[Subtrai quantidade do estoque atual]
    Subtrai --> VerificaZero{Estoque restante == 0?}

    VerificaZero -- [Sim] --> MudaIndisponivel[Atualiza status para INDISPONÍVEL]
    VerificaZero -- [Não] --> MantemDisponivel[Mantém status como DISPONÍVEL]

    MudaIndisponivel --> Sucesso[Confirma saída de estoque com sucesso]
    MantemDisponivel --> Sucesso
    Sucesso --> Fim
```

---

## 4. Diagrama de Estados: Ciclo de Vida do Produto

Mapeamento completo dos status `DISPONIVEL`, `INDISPONIVEL` e `DESCONTINUADO`:
- Transição inicial para `INDISPONIVEL` com estoque 0.
- Transições de estoque saindo de zero e zerando.
- Transição exclusiva de Administrador para `DESCONTINUADO`.

Arquivo fonte: [`diagrama-estados-produto.mermaid`](diagrama-estados-produto.mermaid) / [`diagrama-estados-produto.mmd`](diagrama-estados-produto.mmd)

```mermaid
stateDiagram-v2
    [*] --> INDISPONIVEL : Cadastrar produto [estoque inicial = 0]

    INDISPONIVEL --> DISPONIVEL : darEntrada [quantidade > 0] / estoque sai de zero
    
    DISPONIVEL --> DISPONIVEL : darEntrada / incrementa estoque
    DISPONIVEL --> DISPONIVEL : darSaida [saldo restante > 0] / decrementa estoque

    DISPONIVEL --> INDISPONIVEL : darSaida [saldo restante == 0] / estoque zera

    INDISPONIVEL --> DESCONTINUADO : descontinuarPorAdmin() [Exclusivo do Administrador]
    DISPONIVEL --> DESCONTINUADO : descontinuarPorAdmin() [Exclusivo do Administrador]

    note right of DESCONTINUADO
        Bloqueio total:
        Um produto descontinuado
        NÃO pode ser manipulado no estoque
        (rejeita entrada e saída).
    end note

    DESCONTINUADO --> [*]
```

---

## 5. Como Executar o Módulo de Estoque

```bash
cd Aula-06/mvc-estoque
./mvnw test               # Executa os 7 testes automatizados
./mvnw spring-boot:run     # Inicia o servidor na porta 8080
```
- Acessar Catálogo de Produtos: `http://localhost:8080/produtos`
- Acessar Movimentação de Estoque: `http://localhost:8080/estoque`
