# Funcionalidade 3 - Precificacao Dinamica com Margem de Lucro

## Objetivo
Permitir que o gestor calcule automaticamente o preco de venda dos produtos com base no custo de compra, margem de lucro desejada, impostos, despesas operacionais e descontos maximos permitidos.

## Mapa de historias do usuario

- Como gestor, quero cadastrar parametros globais de precificacao para definir o comportamento padrao do sistema.
- Como gestor, quero criar politicas por produto para sobrescrever a configuracao padrao quando necessario.
- Como gestor, quero simular o preco de venda com base no custo real, nas taxas e na margem desejada.
- Como gestor, quero bloquear simulacoes inseguras quando a margem ou o desconto comprometerem o resultado.
- Como gestor, quero aplicar o preco aprovado diretamente no cadastro do produto.
- Como gestor, quero consultar o historico de simulacoes para auditar quem calculou, aprovou e aplicou cada preco.

## Entidades envolvidas
- Produto
- PrecificacaoParametro
- PrecificacaoPolitica
- PrecificacaoSimulacao
- PrecificacaoComponente
- HistoricoPreco

## Regras de negocio

### RN1 - Composicao dinamica do custo total
O sistema calcula o custo total do produto somando o custo de compra, os impostos e as despesas operacionais. Cada componente do calculo e materializado como uma linha em `precificacao_componente` e percorrido pelo padrao GoF Iterator.

Formula:
```text
custo_total = custo_compra + (custo_compra * impostos%) + (custo_compra * despesas_operacionais%)
```

### RN2 - Calculo do preco de venda por margem desejada
O preco sugerido e calculado de modo que a margem desejada seja mantida sobre o preco final, e nao apenas como markup simples sobre custo.

Formula:
```text
preco_sugerido = custo_total / (1 - margem_lucro_desejada / 100)
```

### RN3 - Bloqueio por margem minima e desconto inseguro
A simulacao e bloqueada quando a margem desejada fica abaixo da margem minima global ou quando o desconto solicitado reduziria o preco abaixo do preco minimo operacional.

Formula auxiliar:
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

## Persistencia
A implementacao usa Spring Data JPA em todas as operacoes da funcionalidade, sem DAO/JDBC legado. As tabelas adicionadas sao:

- `precificacao_parametro`
- `precificacao_politica`
- `precificacao_simulacao`
- `precificacao_componente`
- `historico_preco`

O arquivo `setup_database.sql` contem o schema completo e `dados_teste.sql` contem custos, politicas, simulacoes e componentes proprios para a funcionalidade.

## Prototipo funcional

### Tela `/precificacao/simular`
- Coluna esquerda com seletor de produto e campos de custo, margem, impostos, despesas e desconto.
- Bloco de previsualizacao que mostra custo total e preco sugerido antes do envio.
- Coluna direita com o resultado da simulacao, status, justificativa, componentes do custo e botao para aplicar o preco quando aprovado.
- Rodape com as ultimas simulacoes gravadas no banco.

### Tela `/precificacao/regras`
- Formulario de cadastro e edicao de politica por produto.
- Grade com politicas cadastradas, status ativo/inativo e acoes de simular, editar e desativar.

### Tela `/precificacao/parametros`
- Formulario para manter os parametros globais de fallback.
- Bloco lateral com a formula operacional usada pelo motor de precificacao.

### Tela `/precificacao/historico`
- Lista filtravel por status da simulacao.
- Exibicao de custo total, preco sugerido, margem real, desconto seguro, usuario e data.
- Acao de aplicar preco quando a simulacao estiver aprovada.

## Conformidade com o PDF

- Aplicacao web: sim.
- Persistencia em banco relacional: sim.
- DDD e arquitetura limpa: sim, com separacao entre domain, application e infrastructure.
- Padrao Iterator: sim, na composicao dos componentes de custo.
- JPA em toda a funcionalidade: sim.
- BDD: sim, com `src/test/resources/features/precificacao_dinamica.feature`.
- CML/Context Mapper: sim, com `context-map.cml` atualizado para `PrecificacaoContext`.
