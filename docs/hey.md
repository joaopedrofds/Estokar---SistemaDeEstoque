# CLAUDE.md — Guia de Padrão de Código do Estokar

> Este arquivo define **o padrão de código obrigatório** deste projeto. Toda
> nova funcionalidade, refatoração ou correção DEVE seguir as convenções aqui
> descritas. O objetivo é que o código novo seja indistinguível do bom código
> existente. Quando houver dúvida, **imite o slice do contexto `Precificacao`**
> (`MotorPrecificacaoDinamica` / `PoliticaPrecificacao` / `SimulacaoPrecificacao`)
> — é a referência canônica de domínio dentro do monólito.
>
> A **arquitetura** segue o modelo DDD + Arquitetura Limpa validado pelo
> professor (mesmo padrão do [PetCollar](https://github.com/Carlosesposito22/PetCollar.git));
> as **convenções de nomenclatura** são as do Estokar
> (repositórios `IXxxRepositorio`, VOs de Id com `gerar()`/`de()`, views/DTOs
> `XxxView` ou `XxxDTO` com `de(...)`).

---

## 1. Visão Geral

**Estokar** é um sistema de **gestão de estoque e operações comerciais**
(produtos, pedidos, clientes, suprimentos, logística, financeiro e indicadores),
construído com **Domain-Driven Design + Arquitetura Limpa**. O domínio deve ser
**Java puro** (sem framework) e coberto por **testes BDD com Cucumber em português**.

O sistema cobre o fluxo operacional da distribuidora: do controle de estoque e
vendas, passando por cobrança e engajamento de clientes, até logística, frete e
precificação dinâmica. São **16 funcionalidades (E-01 a E-16)** distribuídas
entre cinco integrantes:

| Integrante | Funcionalidades |
|---|---|
| **Claudio** | E-01 Cancelamento de pedido com estorno · E-02 Inventário periódico · E-03 Ajuste de estoque |
| **Luiz Nogueira** | E-04 Suprimentos e compras · E-05 Remessas e docas · E-06 Precificação dinâmica |
| **Henrique** | E-07 Cobrança e inadimplência · E-08 Engajamento e fidelidade · E-09 Cotação e expedição de fretes |
| **Guilherme Mourão** | E-10 Autenticação e segurança · E-11 RBAC por perfil · E-12 Relatório financeiro · E-13 KPIs operacionais |
| **João Pedro Araújo** | E-14 Rastreabilidade de preço · E-15 Alerta de reposição · E-16 Estratégias de desconto/restituição |

- **Linguagem do código e do domínio:** Português (classes, métodos, variáveis,
  mensagens de erro, testes). Mantenha SEMPRE o português — inclusive em nomes
  como `buscarPorId`, `salvar`, `listarOrdenada`, `recalcular`, `finalizar`.
- **Linguagem onipresente:** use os termos do glossário do domínio — `Produto`,
  `Estoque`, `Pedido`, `Cliente`, `Cupom`, `Cobrança`, `Política de Crédito`,
  `Fatura`, `Acordo de Pagamento`, `Faixa de Fidelidade`, `Ordem de Compra`,
  `Remessa`, `Doca`, `Cotação de Frete`, `Ordem de Despacho`, `Meta de Indicador`,
  `Snapshot de Indicador`, `Alerta de Indicador`, `Permissão de Perfil`,
  `Template de Relatório`, `Simulação de Precificação`.
- **Java 11** · **Spring Boot 2.7** · **Spring Security (form login + RBAC)** ·
  **Spring Data JPA + MySQL 8** (produção) / **H2** (testes) ·
  **Thymeleaf + Bootstrap** (apresentação web) ·
  **Cucumber 7 + JUnit 5 + Mockito** · **Maven** (monólito, evolução para
  multi-módulo conforme bounded contexts do `context-map.cml`).

> Glossário completo e mapa de histórias: [`docs/descricao-dominio-e-mapa-historias.md`](docs/descricao-dominio-e-mapa-historias.md)  
> Context Map (CML): [`context-map.cml`](context-map.cml)

---

## 2. Estrutura de Módulos e Regra de Dependência

### 2.1 Alvo arquitetural (espelho PetCollar)

Cada bounded context do `context-map.cml` deve evoluir para seu próprio módulo
Maven `dominio-<Contexto>`:

```
estokar-pai (pom · groupId com.studiomuda)
├── dominio-compartilhado          ← Shared Kernel: VOs de Id, enums, eventos (Java puro)
├── dominio-Estoque                ← produto, movimentação, alerta de reposição (Java puro)
├── dominio-Vendas                 ← pedido, item, cupom, cancelamento (Java puro)
├── dominio-Cliente                ← cadastro e perfil comercial (Java puro)
├── dominio-RH                     ← funcionário, identidade, acesso (Java puro)
├── dominio-Suprimentos            ← ordem de compra, fornecedor, reposição (Java puro)
├── dominio-Logistica              ← remessa, doca, calendário (Java puro)
├── dominio-Cobranca               ← política, fatura, acordo, inadimplência (Java puro)
├── dominio-Engajamento            ← faixa de fidelidade, retenção, benefício (Java puro)
├── dominio-Frete                  ← cotação, despacho, contingência (Java puro)
├── dominio-Precificacao           ← simulação, política, componentes de custo (Java puro)
├── dominio-Financeiro             ← categorias, templates, relatórios consolidados (Java puro)
├── dominio-Indicadores            ← KPI, meta, snapshot, alerta (Java puro)
├── aplicacao                      ← Casos de uso (orquestra Services do domínio)
├── infraestrutura                 ← JPA, adapters, @Configuration, seeds
└── apresentacao                   ← Spring Boot, Controllers Thymeleaf, Security, bootstrap
```

### 2.2 Estado atual (monólito transicional)

Enquanto a migração multi-módulo não concluir, o código vive em um único artefato
Maven com pacotes que **mapeiam** os bounded contexts:

```
src/main/java/com/studiomuda/estoque/
├── precificacao/                  ← REFERÊNCIA CANÔNICA (domain / application / infrastructure)
│   ├── domain/                    ← Java puro
│   ├── application/               ← commands, services, views
│   └── infrastructure/            ← JPA entities + repositories
├── calculo/                       ← Decorator de KPIs (E-13) — mover para dominio-Indicadores
├── observer/                      ← Observer de preço/estoque/cupom (E-14, E-15)
├── strategy/                      ← Strategy de desconto/restituição (E-16)
├── proxy/                         ← Proxy de cotação de frete (E-09)
├── service/                       ← serviços de aplicação (incl. ajuste/ Template Method)
├── controller/                    ← apresentação Thymeleaf
├── security/                      ← autenticação + RBAC (E-10, E-11)
├── config/                        ← SecurityConfig, beans
├── model/                         ← entidades JPA legadas (migrar para infra)
├── repository/                    ← Spring Data JPA
├── jpa/                           ← entidades/repos JPA adicionais
├── dao/                           ← JDBC legado (migrar para adapters na infra)
└── conexao/                       ← Conexao + interceptor RBAC legado
```

**Regra de dependência (NUNCA inverter):**

```
apresentacao (controller) ──▶ aplicacao (service / use case) ──▶ dominio-<Contexto>
infraestrutura (jpa, dao, repository) ──▶ dominio-*     (implementa IXxxRepositorio)
apresentacao ──▶ infraestrutura (runtime)               (beans Spring)
```

- O domínio **não conhece** Spring, JPA ou JDBC. Sem `import org.springframework.*`
  ou `javax.persistence.*` em pacotes `domain/` ou futuros módulos `dominio-*`.
- **Pacote raiz canônico:** `com.studiomuda.estoque`, seguido da camada e do
  contexto. Ex.: `com.studiomuda.estoque.precificacao.domain.model`,
  `com.studiomuda.estoque.calculo`, `com.studiomuda.estoque.security`.
- Código legado em `model/`, `dao/` e `jpa/` **não recebe funcionalidade nova**;
  ao tocar, extraia para o slice canônico do contexto correspondente.

---

## 3. Convenções de Nomenclatura por Camada

Para um agregado `Xxx`, os arquivos seguem este mapa (referência: `Precificacao`):

| Camada | Tipo | Convenção de nome | Exemplo Estokar |
|---|---|---|---|
| domínio | Entidade / Agregado | substantivo do domínio | `Pedido`, `MetaIndicador`, `PoliticaPrecificacao` |
| domínio | Value Object | substantivo | `ComponenteCusto`, `PeriodoCalculo`, `HashCotacao` |
| domínio | VO de identidade | `XxxId` | `PedidoId`, `ProdutoId`, `IndicadorId` |
| domínio | Enum | substantivo | `StatusPrecificacao`, `OperacaoAcesso`, `StatusAjuste` |
| domínio | Interface de repositório | **`IXxxRepositorio`** | `IIndicadorRepositorio`, `ISimulacaoPrecificacaoRepositorio` |
| domínio | Serviço de domínio | `XxxService` | `MotorPrecificacaoDinamica`, `GestaoMetaIndicadorService` |
| aplicação | Caso de uso | `VerboXxxUseCase` | `RecalcularIndicadoresUseCase`, `GerarRelatorioUseCase` |
| aplicação | Command (alternativa) | `VerboXxxCommand` | `SimularPrecoCommand`, `SalvarParametrosPrecificacaoCommand` |
| infraestrutura | Entidade JPA | `XxxJpa` / `XxxJpaEntity` | `SimulacaoPrecificacaoJpaEntity`, `FaturaJpaEntity` |
| infraestrutura | Repositório Spring Data | `XxxJpaRepository` | `MetaIndicadorRepository`, `FaturaJpaRepository` |
| infraestrutura | Adapter JDBC legado | `XxxDAO` | `RelatorioDAO` (migrar para `RelatorioRepositorioJpa`) |
| infraestrutura | Adapter (impl. da interface) | `XxxRepositorioJpa` | `IndicadorRepositorioJpa` |
| infraestrutura | Wiring de beans | `XxxConfig` | `PrecificacaoConfig`, `IndicadorConfig` |
| apresentação | Controller MVC | `XxxController` | `IndicadorController`, `FinanceiroController` |
| apresentação | View / DTO de saída | `XxxView` ou `XxxDTO` + `de(...)` | `PainelPrecificacaoView`, `ResultadoPrecificacaoView` |
| apresentação | Command de formulário | campos `@RequestParam` ou `RequisicaoXxxForm` | formulários Thymeleaf |

**Organização de pacotes:** por **contexto e depois agregado**, nunca por tipo
técnico. Ex.: `precificacao.domain.model`, não `domain.entidades`.

---

## 4. Camada de Domínio — Java puro

Esta é a camada mais importante. Regras:

### 4.1 Entidades / Agregados

- **Identidade por VO** (`XxxId`), **recebida pelo construtor** (gerada fora com
  `XxxId.gerar()` ou reconstruída com `XxxId.de(valor)`):
  ```java
  public MetaIndicador(MetaIndicadorId id, IndicadorId indicadorId, double valorAlvo) {
      if (id == null)
          throw new IllegalArgumentException("Id da meta não pode ser nulo.");
      if (indicadorId == null)
          throw new IllegalArgumentException("Id do indicador não pode ser nulo.");
      this.id = id;
      this.indicadorId = indicadorId;
      this.valorAlvo = valorAlvo;
      this.ativo = true;
  }
  ```
- **Validação no construtor**, lançando `IllegalArgumentException` com mensagem
  em português para cada invariante violada.
- **Comportamento rico + máquina de estados**: regras em métodos da entidade
  (`isViolada(...)`, `isCritico(...)`, `aprovar(...)`, `cancelar(...)`),
  protegendo transições com `IllegalStateException`:
  ```java
  public void cancelar() {
      if (this.status != StatusPedido.PENDENTE)
          throw new IllegalStateException("Só é possível cancelar pedidos com status PENDENTE.");
      this.status = StatusPedido.CANCELADO;
      this.dataCancelamento = LocalDateTime.now();
  }
  ```
  **Evite setters públicos**; mutação por métodos de negócio nomeados.
- **Construtor de reconstituição**: construtor adicional com todos os campos,
  comentado como `// Construtor de RECONSTRUÇÃO`, usado pela infra para recriar
  a entidade a partir do banco.
- **Imutabilidade defensiva**: campos `final` quando possível; coleções
  retornadas como `Collections.unmodifiableList(...)` e copiadas na entrada.
- **Constantes de regra** como `static final`, nunca números mágicos espalhados
  (ex.: tolerância de inventário, limite de dias de inadimplência, casas decimais
  de arredondamento de KPI).

> Referência existente: `MetaIndicador.isViolada()` / `isCritico()` em
> `model/MetaIndicador.java` — evoluir para domínio puro sem `@Entity`.

### 4.2 Value Objects de Identidade (`XxxId`)

- `final class`, construtor **privado**, factories estáticos `gerar()` e
  `de(String valor)` com validação, `getValor()`, `equals`/`hashCode`/`toString`.
- VOs compartilhados entre contextos moram em **`dominio-compartilhado`**
  (`ProdutoId`, `ClienteId`, `PedidoId`, `UsuarioId`). VOs locais ficam no
  contexto dono (`MetaIndicadorId`, `SimulacaoPrecificacaoId`).

> **Transição:** enquanto o legado usa `int` auto-increment, código **novo** em
> contextos refatorados (Precificação, Indicadores, Financeiro) deve adotar
> `XxxId`. Não misture `int` e `XxxId` no mesmo agregado refatorado.

### 4.3 Enums

- Simples quando bastam (`OperacaoAcesso { LEITURA, ESCRITA, APROVACAO }`,
  `StatusPrecificacao { APROVADO, BLOQUEADO_MARGEM, BLOQUEADO_DESCONTO }`).
- Carregam comportamento quando a regra pede (ex.: operador de meta
  `MAIOR_IGUAL` / `MENOR_IGUAL` com métodos de comparação).

### 4.4 Interfaces de Repositório (`IXxxRepositorio`)

- Definidas **no domínio**, prefixadas com **`I`**, verbos em português:
  `salvar`, `buscarPorId` → `Optional<T>`, `listar...`, `remover`, finders
  específicos do agregado.
  ```java
  public interface IMetaIndicadorRepositorio {
      void salvar(MetaIndicador meta);
      Optional<MetaIndicador> buscarPorId(MetaIndicadorId id);
      List<MetaIndicador> listarAtivasPorIndicador(IndicadorId indicadorId);
  }
  ```

### 4.5 Serviços de Domínio (`XxxService`)

- Dependências por construtor; validação de nulos no construtor.
- Orquestram regras que cruzam entidades/repositórios; podem ser stateless.
- Convenção de erros:
  - **Pré-condição / argumento inválido** → `IllegalArgumentException`.
  - **Estado / conflito de regra de negócio** → `IllegalStateException`.
- **Sem anotações Spring** no domínio. Bean via `@Configuration` na infra.

> Referência existente: `MotorPrecificacaoDinamica` em
> `precificacao/domain/model/` — domínio puro, sem Spring.

---

## 5. Camada de Aplicação

- Casos de uso finos: `VerboXxxUseCase` com método **`executar(...)`** que
  delega ao(s) `Service` do domínio. Sem anotações Spring.
  ```java
  public class RecalcularIndicadoresUseCase {
      private final GestaoIndicadorService gestaoIndicador;
      public RecalcularIndicadoresUseCase(GestaoIndicadorService g) {
          this.gestaoIndicador = g;
      }
      public int executar(LocalDate inicio, LocalDate fim, UsuarioId usuario) {
          return gestaoIndicador.recalcularTodos(inicio, fim, usuario);
      }
  }
  ```
- **Commands** (`SimularPrecoCommand`, `SalvarParametrosPrecificacaoCommand`) são
  aceitos como alternativa ao UseCase quando o fluxo é acionado por formulário
  Thymeleaf — o Application Service traduz Command → domínio.
- Use esta camada para orquestração **entre subdomínios**. Casos simples podem ir
  direto do Controller ao Service, mas **nunca** coloque regra de negócio no
  controller.

> Referência existente: `PrecificacaoDinamicaApplicationService`,
> `IndicadorService` (evoluir para delegar cálculo ao domínio + UseCase).

---

## 6. Camada de Infraestrutura

Para cada agregado, quatro tipos de arquivo (quando migrado do legado):

### 6.1 Entidade JPA (`XxxJpa` / `XxxJpaEntity`)

- `@Entity` + `@Table(name = "snake_case")` — tabelas em snake_case singular/plural
  conforme schema existente (`meta_indicador`, `precificacao_simulacao`).
- **`@Id`**: preferencialmente `String` do VO (`id.getValor()`). Legado com
  `@GeneratedValue` permanece até migração completa.
- Construtor `protected` sem-args para JPA.
- **Mapeamento manual** `fromDomain(Xxx d)` e `toDomain()` com construtor de
  reconstituição.
- Enums e VOs persistidos como `String` (`enum.name()`, `id.getValor()`).
  Evite `@Enumerated` em código novo.

> Referência: `precificacao/infrastructure/persistence/entity/`.

### 6.2 Relações entre tabelas — regra de DDD

- **Dentro do mesmo agregado**: `@OneToMany(cascade = ALL, orphanRemoval = true)`.
- **Entre agregados diferentes**: guarde apenas o **Id de referência** (`String`
  ou `int` no legado) e monte o objeto no Adapter via repositório do outro
  contexto. Evite `@ManyToOne` cruzando bounded contexts.

### 6.3 Repositório Spring Data (`XxxJpaRepository`)

- `interface XxxJpaRepository extends JpaRepository<XxxJpa, IdTipo>`.
- Finders por convenção ou `@Query` (JPQL / native para relatórios pesados).

### 6.4 Adapter (`XxxRepositorioJpa`)

- `@Repository`, implementa `IXxxRepositorio`, traduz com `fromDomain`/`toDomain`.
- `@Transactional` em operações multi-tabela.

### 6.5 Camada JDBC legada (`dao/`)

- **Existente** para Financeiro, RBAC, partes de Estoque/Vendas.
- Acionada via `Conexao.getConnection()` com interceptor RBAC
  (`InterceptadorAutorizacaoDao`).
- **Não expandir.** Novo código de persistência vai para JPA + Adapter.
- Ao refatorar: extrair interface no domínio, implementar adapter, manter DAO
  como fachada deprecated até remoção.

### 6.6 Configuração de beans (`XxxConfig`)

- `@Configuration` montando Services/UseCases como `@Bean`.
- Wiring de padrões (Observer, Proxy, Decorator) registrado aqui.
- Seeds em `@Bean CommandLineRunner` ou scripts SQL (`setup_database.sql`).

---

## 7. Camada de Apresentação

### 7.1 Controllers (Thymeleaf)

- `@Controller` + `@RequestMapping("/recurso")` — rotas web do Estokar
  (`/kpis`, `/financeiro`, `/acesso`, `/precificacao`, etc.).
- Dependências por construtor (`@Service`, UseCase, repositórios).
- Verbos: `@GetMapping` (telas), `@PostMapping` (ações), redirects com
  `RedirectAttributes` para feedback ao usuário.
- O controller **traduz formulário ↔ domínio/DTO** e delega ao Service/UseCase.
  **Não** coloque regra de negócio no controller.
- Endpoints REST auxiliares (`@RestController` em `/api/...`) são permitidos
  para integrações pontuais (ex.: `/api/kpis`), mas a UI principal é Thymeleaf.

> **Referência de injeção:** o `FinanceiroController` foi migrado para depender
> apenas de `FinanceiroService` (injeção por construtor) — imite esse padrão.
> A regra de cálculo do relatório vive em `financeiro.domain.CalculadoraRelatorioFinanceiro`
> (Java puro). Evite o anti-padrão de instanciar DAOs com `new` na apresentação.
> *Débito conhecido:* alguns DAOs ainda instanciam DAOs colaboradores com `new`
> internamente — migrar para injeção ao tocar nesses arquivos.

### 7.2 Views / DTOs

- Saída: classes `XxxView` ou records `XxxDTO` com factory `de(Entidade)`.
- Entrada: `@RequestParam`, `@ModelAttribute` ou `RequisicaoXxxForm`.
- Nunca exponha entidades JPA diretamente nos templates — use objetos de
  apresentação.

> Referência: `precificacao/application/dto/PainelPrecificacaoView`.

### 7.3 Tratamento de erros

- Controllers MVC: capturar `IllegalArgumentException` / `IllegalStateException`
  e exibir mensagem via `model.addAttribute("mensagemErro", ...)` ou
  `RedirectAttributes.addFlashAttribute(...)`.
- Endpoints REST: centralizar em `@RestControllerAdvice` (400 para argumento
  inválido, 409 para conflito de regra).
- Segurança: `AccessDeniedException` / HTTP 403 via `SecurityConfig`;
  RBAC fino: `AcessoNegadoException` do interceptor DAO.

### 7.4 Segurança (Spring Security + RBAC)

- Autenticação por **form login** (`/login`), sessão HTTP, senhas com
  `PasswordEncoder` delegating (`{bcrypt}`).
- `DatabaseUserDetailsService` carrega usuário, perfis e roles de
  `usuario_acesso` / `usuario_perfil` / `perfil_acesso`.
- Autorização em **dois níveis**:
  1. **Rotas** — `SecurityConfig` com `hasAnyRole(...)` por path.
  2. **Operações DAO** — `InterceptadorAutorizacaoDao` valida matriz
     perfil × recurso × operação (`permissao_perfil`) e registra em `log_acesso`.
- Perfis padrão: `ADMINISTRADOR`, `GERENTE_OPERACIONAL`, `OPERADOR_VENDEDOR`.
- Configuração em `application.properties` (credenciais MySQL nunca commitadas
  em produção — usar variáveis de ambiente).

### 7.5 Bootstrap

- `EstoqueApplication` (`@SpringBootApplication`) no pacote raiz.
- `@EntityScan` e `@EnableJpaRepositories` listam `model`, `jpa.entity`,
  `precificacao.infrastructure.persistence.entity`.
- Porta padrão: **8081**.

---

## 8. Padrões de Projeto em Uso (replicar quando aplicável)

Mínimo **6 padrões GoF**, um por integrante. Cada um resolve um problema real:

| Padrão | Integrante | Funcionalidade | Pacote / arquivos |
|---|---|---|---|
| **Decorator** | Guilherme Mourão | E-13 KPIs | `calculo/` — `CalculadoraIndicador`, `CalculadoraBase`, `CalculadoraDecorator`, `ValidacaoPeriodoDecorator`, `LogCalculoDecorator`, `ArredondamentoDecorator`; montagem em `IndicadorService` |
| **Observer** | João Pedro Araújo | E-14, E-15 | `observer/` — `HistoricoPrecosObserver`, alertas de reposição, eventos de cupom/devolução |
| **Strategy** | João Pedro Araújo | E-16 | `strategy/` — `EstrategiaDesconto`, `RestituicaoStrategy`, `ContextoDesconto`, `ContextoRestituicao` |
| **Template Method** | Claudio | E-03 | `service/ajuste/AbstractAjusteEstoqueTemplate` + subclasses |
| **Proxy** | Henrique | E-09 | `proxy/` — `CotacaoFreteProxy` envolve `TransportadoraApiClient` |
| **Iterator** | Luiz Nogueira | E-06 | `precificacao/domain/iterator/ComponentesCusto` |

- Documente o padrão com **Javadoc curto** explicando a intenção (ver `calculo/CalculadoraDecorator.java`).
- Wiring no `@Configuration` ou construtor do Service que monta a cadeia.
- O interceptor `InterceptadorAutorizacaoDao` é mecanismo de **RBAC**, não Proxy GoF.

---

## 9. Persistência / Banco de Dados

- **Produção:** MySQL 8, schema via `setup_database.sql` (bootstrap) +
  `spring.jpa.hibernate.ddl-auto=update` (complementa entidades JPA).
- **Testes:** H2 em memória, `ddl-auto=create-drop`, `spring.flyway.enabled=false`
  (se Flyway for adotado no futuro).
- Configuração em `src/main/resources/application.properties`.
- Tabelas em **snake_case**; colunas alinhadas às entidades JPA.
- Scripts SQL na raiz: `setup_database.sql` (schema + seeds RBAC); scripts
  `dados_teste*.sql` para cenários de demo.
- Chaves primárias: **legado** `INT AUTO_INCREMENT`; **alvo** `String` do VO
  em contextos refatorados.

---

## 10. Testes (BDD com Cucumber, em português)

- Features em **`src/test/resources/features/<nome>.feature`** com
  `# language: pt` e Gherkin PT (`Funcionalidade`, `Cenário`, `Dado`, `Quando`, `Então`).
- Step definitions em **`src/test/java/com/studiomuda/estoque/bdd/`** com
  `io.cucumber.java.pt.{Dado,Quando,Então}`.
- Runner: `CucumberTest` / `CucumberSpringConfiguration` (contexto Spring + H2).
- **Domínio puro:** preferir testes com entidades reais + mocks de
  `IXxxRepositorio` (Mockito), sem banco.
- **Integração web:** `seguranca.feature` usa `MockMvc` + Spring Security Test.

### Features existentes (mapear às funcionalidades)

| Feature | Funcionalidade |
|---|---|
| `cancelamento_pedido.feature` | E-01 |
| `inventario_periodico.feature` | E-02 |
| `ajuste_estoque.feature` | E-03 |
| `suprimentos.feature` | E-04 |
| `remessas.feature` | E-05 |
| `precificacao_dinamica.feature` | E-06 |
| `inadimplencia.feature` | E-07 |
| `frequencia.feature` | E-08 |
| *(pendente)* | E-09 Frete |
| `seguranca.feature` | E-10 |
| `rbac_perfil.feature` | E-11 RBAC matriz |
| `relatorio_financeiro.feature` | E-12 Financeiro |
| `kpis.feature` | E-13 |
| *(pendente)* | E-14, E-15, E-16 |

- Mínimo **3 cenários** por funcionalidade: caminho feliz, regra bloqueante,
  caso de borda.
- Rodar: `mvn test` ou `mvn test -Dtest=CucumberTest`.

---

## 11. Checklist para Criar uma Nova Funcionalidade

Siga esta ordem (de dentro para fora), espelhando `Precificacao`:

1. **Domínio** (pacote `.<contexto>.domain` ou futuro `dominio-<Contexto>`):
   - [ ] Entidade(s) com `XxxId`, validação, métodos de negócio, construtor de
         reconstituição.
   - [ ] VOs/enums necessários.
   - [ ] Interface `IXxxRepositorio`.
   - [ ] `XxxService` com regras, sem Spring.

2. **Testes BDD**:
   - [ ] `features/<funcionalidade>.feature` (pt) com ≥ 3 cenários.
   - [ ] Step definitions no pacote `.bdd`.
   - [ ] `mvn test` verde.

3. **Infraestrutura**:
   - [ ] `XxxJpa` com `fromDomain`/`toDomain`.
   - [ ] `XxxJpaRepository` + `XxxRepositorioJpa implements IXxxRepositorio`.
   - [ ] `XxxConfig` com `@Bean` dos services/use cases e wiring de padrão.

4. **Aplicação**:
   - [ ] `VerboXxxUseCase` com `executar(...)` (ou Application Service equivalente).

5. **Apresentação**:
   - [ ] `XxxController` fino, injeção por construtor, templates Thymeleaf.
   - [ ] Views/DTOs com `de(...)`.
   - [ ] Permissões em `SecurityConfig` e, se usar DAO, em `permissao_perfil`.

**Regras de ouro:**

- Domínio é sagrado: **zero dependência de framework** em `domain/`.
- Tudo em **português**, fiel à linguagem onipresente do Estokar.
- **`IllegalArgumentException`** = entrada inválida; **`IllegalStateException`** = conflito de regra.
- Conversão domínio ↔ persistência via `fromDomain`/`toDomain`; domínio ↔ UI via View/DTO + `de(...)`.
- Organize por **contexto/agregado** (`context-map.cml`), injete por **construtor**.
- **Não expanda `dao/`** — migre para o padrão Precificação.
- Ao refatorar legado, uma funcionalidade por PR, mantendo BDD verde.

---

## 12. Referências do Projeto

| Artefato | Caminho |
|---|---|
| Context Map (CML) | [`context-map.cml`](context-map.cml) |
| Domínio + histórias | [`docs/descricao-dominio-e-mapa-historias.md`](docs/descricao-dominio-e-mapa-historias.md) |
| BDD documentado | [`docs/bdd-cenarios-e-automacao.md`](docs/bdd-cenarios-e-automacao.md) |
| Como rodar | [`README.md`](README.md) |
| Checklist de entrega | [`checklist.md`](checklist.md) |
| Modelo arquitetural | [PetCollar (GitHub)](https://github.com/Carlosesposito22/PetCollar.git) |
