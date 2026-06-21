# Protótipos de baixa fidelidade

Estes wireframes representam os fluxos web principais. As telas implementadas
em Thymeleaf refinam esses protótipos com Bootstrap.

## Operação de estoque — E-01, E-02 e E-03

```text
+--------------------------------------------------------------+
| ESTOQUE > Inventário / Ajustes / Cancelamentos               |
+--------------------------------------------------------------+
| [Nova sessão] [Solicitar ajuste] [Cancelar pedido]           |
|--------------------------------------------------------------|
| Item         Sistema   Físico   Diferença   Estado   Ação    |
| Produto A       20       18        -2       Pendente [Analisar]|
+--------------------------------------------------------------+
| Mensagem de regra ou aprovação necessária                    |
+--------------------------------------------------------------+
```

## Suprimentos e logística — E-04 e E-05

```text
+--------------------------------------------------------------+
| SUPRIMENTOS / REMESSAS                                       |
+--------------------------------------------------------------+
| Produto [____] Lead time [__] Margem [__] [Calcular]         |
| Ponto de pedido: 11  | Quantidade sugerida: 9                |
| [Gerar ordem em rascunho]                                    |
|--------------------------------------------------------------|
| Doca [____] Data [__/__/__] Horário [__:__] Paletes [__]     |
| [Agendar]  Sugestões: Doca Sul - 10:00                       |
+--------------------------------------------------------------+
```

## Precificação — E-06

```text
+--------------------------------------------------------------+
| PRECIFICAÇÃO DINÂMICA                                       |
+----------------------------+---------------------------------+
| Produto [____________]     | Resultado                       |
| Custo [____] Margem [__]   | Custo total: R$ ____            |
| Impostos [__] Despesa [__] | Preço sugerido: R$ ____         |
| Desconto [__] [Simular]    | Status: APROVADO/BLOQUEADO      |
|                            | [Aplicar preço]                  |
+----------------------------+---------------------------------+
```

## Clientes — E-07 e E-08

```text
+--------------------------------------------------------------+
| CLIENTE: Comercial Recife                                   |
+--------------------------------------------------------------+
| Faturas | Acordos | Frequência | Benefícios | Retenção       |
| Atraso: 20 dias     Venda: LIBERADA                          |
| Média entre compras: 25 dias   Faixa: REGULAR                |
| [Registrar fatura] [Criar acordo] [Recalcular faixa]         |
+--------------------------------------------------------------+
```

## Frete — E-09

```text
+--------------------------------------------------------------+
| COTAÇÃO DE FRETE                                             |
+--------------------------------------------------------------+
| CEP [________] Peso [___] Dimensões [___ x ___ x ___]        |
| Cliente [________] Pedido [________] [Cotar]                  |
|--------------------------------------------------------------|
| Valor: R$ 39,90 | Origem: API / CACHE / CONTINGÊNCIA         |
| [Gerar ordem de despacho]                                    |
+--------------------------------------------------------------+
```

## Segurança e RBAC — E-10 e E-11

```text
+--------------------------------------------------------------+
| USUÁRIOS, PERFIS E PERMISSÕES                                |
+--------------------------------------------------------------+
| Usuário [________] Nome [____________] Ativo [x]              |
| Perfis: [x] ADMIN  [ ] GERENTE  [ ] OPERADOR                 |
| [Salvar usuário]                                             |
|--------------------------------------------------------------|
| Recurso       Leitura   Escrita   Aprovação                   |
| FINANCEIRO      [x]       [x]        [ ]                      |
| PEDIDO          [x]       [ ]        [x]                      |
+--------------------------------------------------------------+
```

## Financeiro — E-12

```text
+--------------------------------------------------------------+
| RELATÓRIO FINANCEIRO                                         |
+--------------------------------------------------------------+
| Template [________] Período [__/__/__] a [__/__/__] [Gerar]  |
| Receita       R$ ______                                      |
| Custos        R$ ______                                      |
| Ajustes       R$ ______                                      |
| Resultado     R$ ______   Margem ____%                        |
| [Detalhar] [Exportar]                                        |
+--------------------------------------------------------------+
```

## Indicadores — E-13

```text
+--------------------------------------------------------------+
| INDICADORES OPERACIONAIS                                     |
+--------------------------------------------------------------+
| KPI              Valor      Meta       Situação      Ação     |
| Ticket médio     125,00     >=150      ALERTA       [Resolver]|
| [Configurar meta] [Recalcular todos] [Ver snapshots]         |
+--------------------------------------------------------------+
```

## Histórico de preço — E-14

```text
+--------------------------------------------------------------+
| PRODUTO A                                                    |
+--------------------------------------------------------------+
| Preço atual: R$ 125,00                                     |
| Histórico: 100,00 -> 125,00 (+25%) por gestor                |
| [Editar preço] [Consultar histórico completo]                |
+--------------------------------------------------------------+
```

## Estratégias — E-15

```text
+--------------------------------------------------------------+
| DESCONTO / RESTITUIÇÃO                                       |
+--------------------------------------------------------------+
| Tipo de cupom: ( ) Fixo  (x) Percentual                      |
| Valor: [____]  Total calculado: R$ ____                       |
| Restituição: (x) Crédito  ( ) Troca  ( ) Estorno             |
| [Executar estratégia]                                        |
+--------------------------------------------------------------+
```
