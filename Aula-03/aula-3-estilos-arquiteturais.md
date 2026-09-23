1. Cliente-Servidor
Conceito e definição:
Arquitetura em que o cliente (interface do usuário) faz requisições e o servidor centraliza o processamento e o acesso aos dados, comunicando-se pela rede.
Casos de uso comuns:
Indicado para sistemas com múltiplos usuários que precisam de dados centralizados e atualizados em tempo real. Exemplos reais: aplicativos de academia que consultam a API do servidor para exibir treinos e horários de aula, e sistemas bancários de internet banking.
Principais vantagens:
Centralização e consistência dos dados, manutenção facilitada (lógica concentrada no servidor) e possibilidade de múltiplos clientes (web, mobile) para o mesmo servidor.
Principais desvantagens:
Servidor pode virar gargalo de desempenho, dependência de conexão de rede e escalabilidade limitada pela capacidade do servidor central.

2. Pipes e Filtros
Conceito e definição:
Estilo em que o processamento é dividido em etapas independentes (filtros) conectadas por canais (pipes), onde a saída de um filtro é a entrada do próximo.
Casos de uso comuns:
Indicado para processamento de dados em etapas sequenciais e reutilizáveis. Exemplos reais: pipelines de compiladores (análise léxica, sintática e geração de código) e processamento de imagens em ferramentas de edição.
Principais vantagens:
Alta reutilização e substituição independente dos filtros, facilidade para testar cada etapa isoladamente e paralelização de etapas quando possível.
Principais desvantagens:
Overhead de transformação de dados entre filtros, dificuldade para tratar estados compartilhados entre etapas e complexidade para tratamento de erros ao longo do pipeline.
