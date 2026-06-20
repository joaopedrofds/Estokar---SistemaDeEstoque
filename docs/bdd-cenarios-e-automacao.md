# Cenarios de teste BDD e automacao com Cucumber

## Objetivo

Documentar os cenarios BDD das funcionalidades do PDF e da funcionalidade de precificacao dinamica, mostrando como cada um e automatizado pelo Cucumber no projeto.

## 1. Funcionalidades cobertas

- Cobranca e bloqueio de inadimplentes
- Engajamento, fidelidade e retencao de clientes
- Cota o e expedicao de fretes com Proxy
- Precificacao dinamica com margem de lucro

## 2. Cenarios BDD por funcionalidade

### 2.1 Cobranca e inadimplencia

Arquivo: `src/test/resources/features/inadimplencia.feature`

### Cen ario 1 - Bloqueio de cliente com faturas em atraso
- Dado que o cliente possui faturas pendentes ha mais de 45 dias
- Quando o sistema valida o perfil para um novo pedido
- Entao o sistema deve impedir a venda

### Cobertura de negocio
- Bloqueio automatico no ponto de venda
- Validacao de inadimplencia antes da venda

### 2.2 Engajamento e fidelizacao

Arquivo: `src/test/resources/features/frequencia.feature`

### Cen ario 1 - Classificacao dinamica de cliente VIP
- Dado que o intervalo medio de compras do cliente e de 12 dias
- Quando o algoritmo de classificacao e executado
- Entao o cliente deve receber a etiqueta "VIP"

### Cobertura de negocio
- Recalculo de categoria por frequencia de compra
- Base para aplicacao de beneficios e retencao

### 2.3 Frete e expedicao

Arquivo: `src/test/resources/features/remessas.feature`

### Cen ario 1 - Bloqueio por capacidade excedida com sugestao de janela alternativa
- Dado que a doca suporta X paletes por dia
- E ja existem Y paletes agendados
- E existe uma doca alternativa com capacidade disponivel
- Quando solicito uma remessa de Z paletes
- Entao o agendamento da remessa deve ser bloqueado
- E o sistema deve sugerir uma janela alternativa

### Cobertura de negocio
- Controle de capacidade das docas
- Sugestao de agenda alternativa

### 2.4 Precificacao dinamica

Arquivo: `src/test/resources/features/precificacao_dinamica.feature`

### Cen ario 1 - Simulacao aprovada com componentes do custo
- Dado um produto com preco atual e custo de compra
- E uma politica com margem, impostos, despesas e desconto maximo
- E uma margem minima global e desconto maximo global
- Quando o sistema calcula a precificacao dinamica
- Entao o custo total deve ser calculado
- E o preco sugerido deve ser calculado
- E o status deve ser "APROVADO"
- E a simulacao deve conter os componentes de custo

### Cen ario 2 - Simulacao bloqueada quando a margem desejada fica abaixo da minima global
- Dado um produto com custo de compra
- E uma politica com margem abaixo do limite
- E uma margem minima global
- Quando o sistema calcula a precificacao dinamica
- Entao o status deve ser "BLOQUEADO_MARGEM"
- E a justificativa deve conter a causa do bloqueio

## 3. Automacao com Cucumber

### Suite principal

Arquivo: `src/test/java/com/studiomuda/estoque/bdd/CucumberTest.java`

- Executa todos os cenarios localizados em `src/test/resources/features`
- Usa o glue `com.studiomuda.estoque.bdd`
- Gera relatorio HTML em `target/cucumber-reports/security.html`

### Configuracao do contexto

Arquivo: `src/test/java/com/studiomuda/estoque/bdd/CucumberSpringConfiguration.java`

- Sobe o contexto Spring para os cenarios BDD
- Integra com MockMvc e com o ambiente de testes do projeto

### Step definitions

- `InadimplenciaStepDefinitions.java`
- `FrequenciaStepDefinitions.java`
- `RemessasStepDefinitions.java`
- `PrecificacaoStepDefinitions.java`

## 4. Relacao entre cenarios e automacao

- Cobranca: feature `inadimplencia.feature` + `InadimplenciaStepDefinitions`
- Engajamento: feature `frequencia.feature` + `FrequenciaStepDefinitions`
- Frete: feature `remessas.feature` + `RemessasStepDefinitions`
- Precificacao: feature `precificacao_dinamica.feature` + `PrecificacaoStepDefinitions`

## 5. Como executar os testes BDD

```bash
mvn test
```

Ou, para executar apenas o Cucumber:

```bash
mvn test -Dtest=CucumberTest
```

## 6. Conclusao

Os cenarios BDD das funcionalidades do PDF e da precificacao dinamica estao documentados e automatizados com Cucumber no projeto.
