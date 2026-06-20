# Funcionalidade 3 — Precificação Dinâmica com Margem de Lucro

## Objetivo
Permitir que o gestor calcule automaticamente o preço de venda dos produtos com base no custo de compra, margem de lucro desejada, impostos, despesas operacionais e descontos máximos permitidos.

## Entidades envolvidas
- Produto
- PrecificacaoParametro
- PrecificacaoPolitica
- PrecificacaoSimulacao
- PrecificacaoComponente
- HistoricoPreco

## Regras de negócio

### RN1 — Composição dinâmica do custo total
O sistema calcula o custo total do produto somando o custo de compra, os impostos e as despesas operacionais. Cada componente do cálculo é materializado como uma linha em `precificacao_componente` e percorrido pelo padrão GoF Iterator.

**Fórmula:**

```text
custo_total = custo_compra + (custo_compra * impostos%) + (custo_compra * despesas_operacionais%)
```

### RN2 — Cálculo do preço de venda por margem desejada
O preço sugerido é calculado de modo que a margem desejada seja mantida sobre o preço final, não apenas como markup simples sobre custo.

**Fórmula:**

```text
preco_sugerido = custo_total / (1 - margem_lucro_desejada / 100)
```

### RN3 — Bloqueio por margem mínima e desconto inseguro
A simulação é bloqueada quando a margem desejada fica abaixo da margem mínima global ou quando o desconto solicitado reduziria o preço abaixo do preço mínimo operacional.

**Fórmula auxiliar:**

```text
preco_minimo_permitido = custo_total / (1 - margem_minima_global / 100)
desconto_seguro = (preco_sugerido - preco_minimo_permitido) / preco_sugerido * 100
```

## Arquitetura aplicada

A funcionalidade foi refatorada em uma estrutura inspirada em DDD e arquitetura limpa:

```text
com.studiomuda.estoque.precificacao
├── domain
│   ├── model
│   └── iterator
├── application
│   ├── command
│   ├── dto
│   └── service
└── infrastructure
    └── persistence
        ├── entity
        └── repository
```

## Persistência
A implementação usa Spring Data JPA em todas as operações da funcionalidade, sem DAO/JDBC legado. As tabelas adicionadas são:

- `precificacao_parametro`
- `precificacao_politica`
- `precificacao_simulacao`
- `precificacao_componente`
- `historico_preco`

O arquivo `setup_database.sql` já contém o schema completo e `dados_teste.sql` contém custos, políticas, simulações e componentes próprios para a funcionalidade.
