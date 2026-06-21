# Cenários BDD e automação com Cucumber

Todos os arquivos usam Gherkin em português (`# language: pt`) e são executados
pela suíte `CucumberTest`, com contexto Spring Boot, H2 em memória e MockMvc para
os cenários web.

## Matriz de cobertura

| ID | Funcionalidade | Feature |
|---|---|---|
| E-01 | Cancelamento de pedido | `cancelamento_pedido.feature` |
| E-02 | Inventário periódico | `inventario_periodico.feature` |
| E-03 | Ajuste de estoque | `ajuste_estoque.feature` |
| E-04 | Suprimentos | `suprimentos.feature` |
| E-05 | Remessas e docas | `remessas.feature` |
| E-06 | Precificação dinâmica | `precificacao_dinamica.feature` |
| E-07 | Inadimplência | `inadimplencia.feature` |
| E-08 | Frequência e fidelidade | `frequencia.feature` |
| E-09 | Cotação de frete | `cotacao_frete.feature` |
| E-10 | Autenticação | `seguranca.feature` |
| E-11 | RBAC por perfil | `rbac_perfil.feature` |
| E-12 | Relatório financeiro | `relatorio_financeiro.feature` |
| E-13 | KPIs | `kpis.feature` |
| E-14 | Histórico de preço | `historico_preco.feature` |
| E-15 | Estratégias de desconto | `estrategias_desconto.feature` |

Os cenários cobrem caminhos felizes, regras bloqueantes e casos de borda. Os
steps ficam em `src/test/java/com/studiomuda/estoque/bdd`.

## Execução

```bash
mvn test -Dtest=CucumberTest
```

Relatório HTML:

```text
target/cucumber-reports/estokar.html
```
