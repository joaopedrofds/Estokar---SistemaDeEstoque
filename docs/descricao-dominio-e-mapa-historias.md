# Descrição do domínio e mapa de histórias do usuário

## Linguagem onipresente

O Estokar controla o ciclo comercial e operacional de uma distribuidora. Os
termos abaixo são usados no código, nas telas, nos cenários BDD e na conversa
com os usuários:

- **Produto**: item comercializável com custo, preço e saldo.
- **Movimentação de estoque**: entrada ou saída que altera o saldo do produto.
- **Pedido**: venda composta por itens, cliente, cupom e estado de pagamento.
- **Cancelamento**: encerramento de pedido com estorno idempotente do estoque.
- **Inventário**: sessão de contagem física que identifica divergências.
- **Ajuste de estoque**: correção automática ou sujeita à aprovação gerencial.
- **Ponto de pedido**: consumo durante o lead time acrescido da margem de segurança.
- **Ordem de compra**: sugestão de reposição que nasce como rascunho.
- **Remessa**: agendamento logístico sujeito à capacidade e disponibilidade da doca.
- **Cotação de frete**: cálculo obtido por API, cache ou tabela de contingência.
- **Fatura**: obrigação financeira do cliente com vencimento e status.
- **Acordo de pagamento**: negociação que pode liberar uma venda bloqueada.
- **Faixa de fidelidade**: classificação por frequência de compra.
- **Ação de retenção**: benefício gerado para cliente em risco.
- **Simulação de preço**: cálculo auditável de custo, margem e preço sugerido.
- **Perfil de acesso**: conjunto de permissões atribuídas a usuários.
- **Permissão**: concessão de uma operação sobre um recurso.
- **Template de relatório**: configuração das categorias e indicadores consolidados.
- **Meta de indicador**: objetivo operacional comparado ao valor calculado.
- **Snapshot de indicador**: registro imutável de um cálculo de KPI.
- **Alerta de indicador**: violação ativa de uma meta operacional.
- **Histórico de preço**: registro do valor anterior, novo valor e variação.
- **Estratégia de desconto/restituição**: algoritmo selecionado conforme o tipo da operação.

## Mapa de histórias por funcionalidade

### E-01 — Cancelamento de pedido com estorno

- Como operador, quero cancelar um pedido para interromper a venda.
- Como gestor, quero aprovar cancelamentos acima da alçada.
- Como sistema, quero impedir estorno duplicado do mesmo pedido.

### E-02 — Inventário periódico

- Como estoquista, quero abrir uma sessão de inventário por setor.
- Como operador, quero registrar a contagem física dos produtos.
- Como gestor, quero impedir sessões simultâneas no mesmo escopo.

### E-03 — Ajuste de estoque

- Como sistema, quero aplicar automaticamente diferenças dentro da tolerância.
- Como gestor, quero aprovar perdas acima da alçada.
- Como sistema, quero impedir ajustes que deixem saldo negativo.

### E-04 — Suprimentos e compras

- Como comprador, quero configurar lead time e margem de segurança.
- Como comprador, quero gerar ordens de compra em rascunho antes da ruptura.
- Como gestor, quero aprovar ou rejeitar uma ordem de compra.

### E-05 — Remessas e docas

- Como gestor logístico, quero agendar remessas em docas disponíveis.
- Como sistema, quero bloquear excesso de capacidade e choque de horário.
- Como operador, quero receber sugestões de janelas alternativas.

### E-06 — Precificação dinâmica

- Como gestor, quero configurar parâmetros e políticas de preço.
- Como gestor, quero simular preço preservando a margem mínima.
- Como gestor, quero aplicar uma simulação aprovada e manter seu histórico.

### E-07 — Cobrança e inadimplência

- Como gestor, quero registrar faturas e políticas de crédito.
- Como sistema, quero bloquear clientes com atraso superior ao limite.
- Como gestor, quero criar acordos que liberem clientes regularizados.

### E-08 — Engajamento e fidelidade

- Como gestor, quero classificar clientes pela frequência de compra.
- Como gestor, quero cadastrar benefícios por faixa.
- Como sistema, quero gerar ações de retenção para clientes em risco.

### E-09 — Cotação e expedição de fretes

- Como operador, quero cotar frete por peso, dimensões e CEP.
- Como sistema, quero reutilizar cache e contingência quando necessário.
- Como gestor, quero gerar uma ordem de despacho apenas para operação autorizada.

### E-10 — Autenticação e segurança

- Como usuário, quero autenticar com login e senha.
- Como sistema, quero proteger rotas e encerrar sessões com segurança.
- Como administrador, quero inativar credenciais sem remover auditoria.

### E-11 — Controle de acesso por perfil

- Como administrador, quero cadastrar perfis e usuários.
- Como administrador, quero conceder operações por recurso.
- Como auditor, quero consultar tentativas permitidas e negadas.

### E-12 — Relatório financeiro

- Como gestor, quero categorizar receitas, custos e ajustes.
- Como gestor, quero configurar templates de relatório.
- Como gestor, quero consolidar resultado, margem e comparação entre períodos.

### E-13 — Indicadores operacionais

- Como gestor, quero configurar metas para KPIs.
- Como sistema, quero gerar snapshots imutáveis e alertas de violação.
- Como gestor, quero resolver alertas com observação auditável.

### E-14 — Rastreabilidade de preços

- Como gestor, quero registrar toda alteração efetiva de preço.
- Como auditor, quero consultar valor anterior, novo e responsável.
- Como sistema, quero ignorar salvamentos que não alterem o preço.

### E-15 — Estratégias de desconto e restituição

- Como operador, quero aplicar desconto fixo ou percentual conforme o cupom.
- Como gestor, quero escolher crédito, troca ou estorno numa devolução.
- Como sistema, quero validar limites antes de executar a estratégia.

## Contextos

As funcionalidades são distribuídas nos bounded contexts descritos em
[`context-map.cml`](../context-map.cml): Vendas, Estoque, Clientes, Suprimentos,
Logística, Cobrança, Engajamento, Frete, Precificação, Segurança, Financeiro e
Indicadores.
