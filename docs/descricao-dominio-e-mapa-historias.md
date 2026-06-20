# Descricao do dominio e mapa de historias do usuario

## 1. Descricao do dominio usando linguagem onipresente

O projeto Estokar organiza o negocio em varios contextos de dominio que compartilham uma linguagem comum entre negocio e tecnologia.

### Termos principais do dominio

- `Produto`: item controlado no estoque, com custo, preco, saldo e historico.
- `Estoque`: controle de entrada, saida e disponibilidade dos produtos.
- `Pedido`: solicitacao de compra ou venda que movimenta o fluxo operacional.
- `Cliente`: entidade comercial que concentra dados cadastrais, comportamento e relacionamento.
- `Cupom`: beneficio promocional aplicado em vendas ou em estrategias de retencao.
- `Cobranca`: controle de faturas, acordos, politicas de credito e bloqueio de inadimplencia.
- `Engajamento`: classificacao de fidelidade, frequencia de compras e acoes de retencao.
- `Suprimentos`: reposicao inteligente, analise de necessidade e geracao de ordens de compra.
- `Remessa`: organizacao logistica de saida de mercadorias e ocupacao de docas.
- `Frete`: cotacao de transporte, contingencia de consulta e despacho autorizado.
- `Precificacao`: calculo do preco sugerido com base em custo, impostos, despesas e margem.

### Linguagem onipresente por contexto

#### Estoque

- Quando o usuario registra uma movimentacao, o saldo do produto e atualizado.
- Quando o saldo fica abaixo do minimo, o sistema gera alerta de reposicao.
- Quando ha ajuste de inventario, a quantidade fisica passa a ser a referencia.

#### Vendas

- Um pedido e composto por itens de pedido e pode usar cupom.
- O faturamento depende do ciclo do pedido e das regras de negocio da venda.

#### Clientes

- O cliente pode ser classificado por comportamento de compra e risco.
- O cliente inadimplente pode ser bloqueado para venda ate regularizacao.

#### Cobranca

- Uma politica de credito define limites e prazo para bloqueio.
- Um acordo de pagamento permite manter o cliente habilitado mesmo com atraso.
- Uma fatura registra valor, vencimento, status e historico de cobranca.

#### Engajamento

- A frequencia de compra determina a faixa de fidelidade.
- Uma faixa de fidelidade pode liberar beneficio.
- Uma acao de retencao e disparada quando o cliente entra em risco.

#### Suprimentos

- A ordem de compra nasce da necessidade de reposicao.
- O lead time e usado para prever o momento certo de comprar.
- O fornecedor influencia custo, prazo e disponibilidade.

#### Logistica e frete

- A remessa precisa respeitar capacidade de doca e janela de agendamento.
- A cotacao de frete pode usar contingencia quando a integracao externa falha.
- Uma ordem de despacho so pode ser gerada com validacao de autorizacao.

#### Precificacao

- O custo do produto serve de base para a simulacao.
- A margem de lucro desejada define o preco sugerido.
- A politica de precificacao pode variar por produto.
- O historico de simulacao registra quem calculou, aprovou e aplicou o preco.

## 2. Mapa de historias do usuario

### Epic 1 - Cadastro e operacao de estoque

- Como usuario, quero cadastrar produtos para manter o catalogo atualizado.
- Como usuario, quero registrar entradas e saidas para acompanhar o saldo em tempo real.
- Como usuario, quero visualizar o historico de movimentacoes para auditar o estoque.

### Epic 2 - Vendas e relacionamento com cliente

- Como usuario, quero criar pedidos com itens para registrar uma venda.
- Como usuario, quero aplicar cupons para conceder beneficios promocionais.
- Como usuario, quero consultar clientes para acompanhar o relacionamento comercial.

### Epic 3 - Cobranca e inadimplencia

- Como gestor, quero cadastrar politicas de credito para controlar bloqueios.
- Como gestor, quero registrar faturas em atraso para acompanhar pendencias.
- Como gestor, quero criar acordos de pagamento para liberar clientes com restricao.
- Como gestor, quero consultar o historico de cobranca para auditar as decisoes tomadas.

### Epic 4 - Engajamento e fidelizacao

- Como gestor, quero classificar clientes por faixa de fidelidade para premiar recorrencia.
- Como gestor, quero cadastrar beneficios por categoria para incentivar novas compras.
- Como gestor, quero disparar acoes de retencao quando o cliente estiver em risco.

### Epic 5 - Suprimentos e reposicao

- Como comprador, quero gerar ordens de compra para evitar ruptura de estoque.
- Como comprador, quero analisar fornecedores para escolher a melhor opcao.
- Como comprador, quero acompanhar a recomendacao de reposicao para agir antes da falta.

### Epic 6 - Logistica e frete

- Como usuario, quero cotar fretes para comparar o custo de envio.
- Como usuario, quero registrar despachos para acompanhar a saida da mercadoria.
- Como usuario, quero consultar o historico de cotacoes para verificar tentativas anteriores.
- Como usuario, quero manter uma tabela de contingencia para operar quando a cotacao externa estiver indisponivel.

### Epic 7 - Precificacao dinamica

- Como gestor, quero cadastrar parametros globais para definir a regra padrao de precificacao.
- Como gestor, quero criar politicas por produto para sobrescrever o padrao quando necessario.
- Como gestor, quero simular preco de venda para avaliar margem e viabilidade.
- Como gestor, quero bloquear simulacoes inseguras quando a margem estiver abaixo do minimo.
- Como gestor, quero aplicar o preco aprovado diretamente no produto.
- Como gestor, quero consultar o historico de simulacoes para auditar a operacao.

## 3. Relacao com as funcionalidades do sistema

- Funcionalidade de cobrancas: atende a Epic 3.
- Funcionalidade de engajamento: atende a Epic 4.
- Funcionalidade de suprimentos: atende a Epic 5.
- Funcionalidade de frete: atende a Epic 6.
- Funcionalidade de precificacao dinamica: atende a Epic 7.

## 4. Observacao

Este documento complementa o `context-map.cml` e o `README.md`, consolidando a linguagem do dominio e o mapa de historias em uma unica referencia para avaliacao e manutencao do projeto.
