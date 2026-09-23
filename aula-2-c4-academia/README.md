# C4 — Sistema de Academia

Modelo C4 do sistema escolhido: gestão de uma academia (alunos, personal trainers e recepção).

- `c1-context.mmd` — Diagrama de Contexto: quem usa o sistema (aluno, personal trainer e recepção) e o que cada um faz.
- `c2-container.mmd` — Diagrama de Contêineres: app do aluno, painel da recepção/personal, API e banco de dados.
- `c3-component.mmd` — Diagrama de Componentes: divisão da API em Matrículas, Treinos e Notificação.
- `c4-code.mmd` — Diagrama de classes: a classe `Treino` e a interface `Notificador`, implementada por `NotificadorEmail`.

Para visualizar, abra o conteúdo de cada `.mmd` em um editor com suporte a Mermaid (ex: extensão Mermaid do VS Code ou o [Mermaid Live Editor](https://mermaid.live)).
