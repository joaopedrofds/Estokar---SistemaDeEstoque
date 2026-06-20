# Estokar - Sistema de Estoque

Sistema web para gerenciamento de estoque (produtos, pedidos, clientes, suprimentos, financeiro e indicadores), desenvolvido em Java + Spring Boot, com persistência relacional via JPA/Hibernate e camada de apresentação em Thymeleaf.

Este README é, antes de tudo, um **guia para rodar e testar** cada funcionalidade do sistema.

---

## 1. Stack

- **Back-end:** Java 11, Spring Boot 2.7, Spring MVC, Spring Security
- **Persistência:** JPA/Hibernate + Spring Data (com alguns módulos legados em JDBC/DAO)
- **Banco:** MySQL 8 (produção) / H2 em memória (testes)
- **Front-end:** Thymeleaf + Bootstrap
- **Testes:** JUnit 5, Cucumber (BDD)
- **Build:** Maven

---

## 2. Como rodar a aplicação

### 2.1 Pré-requisitos
- JDK 11
- Maven 3.6+
- MySQL 8 rodando em `localhost:3306`

### 2.2 Subir e preparar o banco (macOS / Homebrew)
```bash
# subir o MySQL
brew services start mysql

# criar banco + usuário (apenas na primeira vez)
mysql -u root -p <<'SQL'
CREATE DATABASE IF NOT EXISTS studiomuda CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'estokar'@'localhost' IDENTIFIED BY 'estokar123';
GRANT ALL PRIVILEGES ON studiomuda.* TO 'estokar'@'localhost';
FLUSH PRIVILEGES;
SQL

# carregar todas as tabelas e dados iniciais (inclui usuários e RBAC)
mysql -u estokar -pestokar123 studiomuda < setup_database.sql
```

> ⚠️ O `application.properties` usa `spring.jpa.hibernate.ddl-auto=none` — o Hibernate **não** cria tabelas. O schema vem inteiro do `setup_database.sql`. Se aparecer "Table doesn't exist", rode o script novamente.

### 2.3 Executar
```bash
mvn spring-boot:run
```
Aplicação em **http://localhost:8081**

### 2.4 Usuários (criados pelo `setup_database.sql`)
| Usuário | Senha | Perfil | Para quê |
|---|---|---|---|
| `admin` | `Admin@123` | ADMINISTRADOR | acesso total (configurações, RBAC) |
| `gerente` | `Gerente@123` | GERENTE_OPERACIONAL | aprovações e decisões |
| `operador` | `Operador@123` | OPERADOR_VENDEDOR | operação restrita (vendas) |

---

## 3. Como rodar os testes (BDD / Cucumber)

Os testes sobem um contexto Spring contra **H2 em memória** — **não precisam de MySQL**.

```bash
# toda a suíte (unit + BDD)
mvn test

# apenas os cenários BDD (Cucumber)
mvn test -Dtest=CucumberTest

# os .feature ficam em src/test/resources/features/
# os step definitions em  src/test/java/com/studiomuda/estoque/bdd/
```

---

## 4. Guia de teste por funcionalidade

Cada integrante é responsável por 2 funcionalidades. Abaixo, a rota de cada uma, o passo a passo de teste pela interface e o `.feature` correspondente.

> Dica: faça login conforme o perfil indicado. Ações de aprovação geralmente exigem `gerente`; configuração de parâmetros/RBAC exige `admin`.

### Claudio
**1. Cancelamento de Pedido com Estorno de Estoque** — `/pedidos/cancelamentos`
- Crie um pedido em `/pedidos/novo`, depois cancele em `/pedidos/cancelamentos`.
- Verifique que o saldo do produto **volta ao estoque** e que uma movimentação de "Entrada por Cancelamento" é registrada.
- Regra-chave (idempotência): cancelar o mesmo pedido duas vezes **não** estorna em dobro.
- BDD: `cancelamento_pedido.feature`

**2. Inventário Periódico com Ajuste de Diferença** — `/inventarios` (+ `/ajustes-estoque`)
- Abra uma sessão (`/inventarios/novo` → `abrir`), lance a contagem física, depois `fechar`.
- Divergência dentro da tolerância → ajuste automático; acima → exige aprovação.
- BDD: `inventario_periodico.feature`, `ajuste_estoque.feature`
- Padrão de projeto: **Template Method** (`service/ajuste/`)

### Luiz Nogueira
**1. Gestão de Suprimentos e Compras** — `/suprimentos`
- Cadastre parâmetros de fornecedor (lead time, custo) e margem de segurança.
- Acione a verificação de reposição → o sistema gera Ordens de Compra em "Rascunho" para itens abaixo do ponto de pedido; aprove/rejeite.
- BDD: `suprimentos.feature`

**2. Roteirização e Ocupação de Docas** — `/remessas`
- Cadastre docas/capacidade e bloqueios de calendário; tente agendar uma remessa.
- Conflito de capacidade ou data bloqueada → o sistema bloqueia e sugere as próximas janelas livres.
- BDD: `remessas.feature`

### Henrique
**1. Gestão de Cobranças e Acordos** — `/cobrancas`
- Em `/cobrancas/politicas` ajuste o limite de dias para bloqueio.
- Registre faturas/acordos; valide que cliente em atraso **sem acordo** é bloqueado na venda, mas **com acordo ativo** passa.
- BDD: `inadimplencia.feature`

**2. Gestão de Engajamento de Clientes** — `/engajamento`
- Configure faixas de segmentação e benefícios VIP; rode o recálculo de um cliente.
- Verifique a mudança de categoria e a geração de ações de retenção (cupom) para "Em Risco".
- BDD: `frequencia.feature`

### Guilherme Mourão
**1. Controle de Acesso por Perfil (RBAC)** — `/acesso` *(perfil: admin)*
- Em `/acesso/perfis` e `/acesso/permissoes`, monte a matriz perfil × recurso × operação.
- Logue como `operador` e tente acessar um recurso negado → bloqueio + registro em log de acesso.
- BDD: `seguranca.feature`
- Mecanismo: interceptor de autorização na camada DAO (`security/InterceptadorAutorizacaoDao`).

**2. Relatório Financeiro / Indicadores Operacionais (KPIs)** — `/financeiro` e `/kpis`
- KPIs: em `/kpis` configure uma meta (`/kpis/meta/nova/{id}`), clique em **Recalcular** → gera snapshot imutável e, se a meta for violada, **cria alerta automático**. Resolva em `/kpis/alertas`; histórico em `/kpis/snapshots`.
- Relatório: em `/financeiro` configure categorias/templates e gere o relatório consolidado.
- BDD: `kpis.feature`
- Padrão de projeto: **Decorator** (`calculo/`) na cadeia de cálculo dos indicadores.

### João Pedro Araújo
**1. Rastreabilidade de Alterações de Preço** — `/produtos/historico/{id}`
- Edite o preço de um produto em `/produtos/editar/{id}` e salve.
- Abra `/produtos/historico/{id}` → o histórico registra preço anterior, novo e variação % automaticamente (só quando o preço muda).
- Padrão de projeto: **Observer** (`observer/`)

**2. Alerta de Reposição Automática de Estoque** — movimentações em `/estoque`
- Registre saídas até o estoque cair abaixo do ponto de pedido → alerta de reposição é gerado automaticamente (sem duplicar para o mesmo produto).
- Padrão de projeto: **Observer** (`observer/`)

---

## 5. Padrões de Projeto Implementados

A 2ª entrega exige **6 ou mais padrões (1 por integrante)** dentre: *Iterator, Decorator, Observer, Proxy, Strategy, Template Method*.

| Padrão | Status | Implementado por | Arquivos principais |
|---|---|---|---|
| Decorator | ✅ | Guilherme Mourão | `calculo/` + `service/IndicadorService.java` |
| Observer | ✅ | João Pedro Araújo | `observer/` |
| Strategy | ✅ | (confirmar) | `strategy/` |
| Template Method | ✅ | Claudio | `service/ajuste/AbstractAjusteEstoqueTemplate.java` (+ subclasses) |
| Proxy | ⬜ pendente | — | *(há um interceptor de autorização em `security/InterceptadorAutorizacaoDao`, mas por inspeção de stack — não é um Proxy GoF clássico de embrulho)* |
| Iterator | ⬜ pendente | — | — |

> A coluna "Implementado por" reflete o histórico do Git e o desenho dos módulos; **confirme com o grupo**. O padrão **DAO** é usado em todo o projeto como decisão de arquitetura, mas **não** faz parte da lista exigida.

### Detalhe — Decorator (cálculo de indicadores)
Cadeia empilhável sobre o cálculo "cru" do indicador, mantendo a mesma interface em cada camada:
- `CalculadoraIndicador` (Component), `CalculadoraBase` (ConcreteComponent, lê via JPA)
- `CalculadoraDecorator` (Decorator abstrato)
- `ValidacaoPeriodoDecorator`, `LogCalculoDecorator`, `ArredondamentoDecorator` (ConcreteDecorators)
- Montagem e uso em `service/IndicadorService.java`

### Detalhe — Observer (rastreabilidade de preço / alerta de reposição)
`observer/` com `ObservadorDePreco`, `PrecoDomainEvent`, `HistoricoPrecosObserver` (e equivalentes de cupom/devolução), disparados pelos serviços de domínio de forma desacoplada.

### Detalhe — Template Method (ajuste de estoque)
`AbstractAjusteEstoqueTemplate.processarSolicitacao()` define o esqueleto `final` do algoritmo; as subclasses (`AjustePorSobra/Perda/Avaria/Correcao`) implementam os passos `abstract`.

---

## 6. Arquitetura (camadas)

```
controller/   → Apresentação (Spring MVC + Thymeleaf)
service/      → Aplicação / regras de negócio
model/        → Domínio (entidades JPA)
repository/   → Infraestrutura (Spring Data JPA)
dao/          → Infraestrutura (JDBC — módulos legados)
jpa/          → Entidades/repos JPA adicionais
observer/, strategy/, calculo/ → padrões de projeto
security/     → autenticação e autorização (RBAC)
```

---

## 7. Troubleshooting

| Problema | Solução |
|---|---|
| `Table 'studiomuda.x' doesn't exist` | rode `mysql -u estokar -pestokar123 studiomuda < setup_database.sql` |
| Erro de conexão com banco | confira se o MySQL está no ar e as credenciais em `application.properties` |
| Porta 8081 em uso | `lsof -ti:8081 \| xargs kill -9` ou mude `server.port` |
| Limpar e rodar do zero | `mvn clean spring-boot:run` |
| Rodar JAR | `mvn clean package && java -jar target/estoque-0.0.1-SNAPSHOT.jar` |
