# Estokar - Sistema de Estoque

Sistema web para gerenciamento completo de estoque, desenvolvido com Java e Spring Boot. Permite o controle de produtos, pedidos,clientes, funcionários e todas as movimentações de estoque em uma interface web moderna e intuitiva.

---

## Visão Geral

- **Arquitetura**: Aplicação web Java com Spring Boot
- **Front-end**: Thymeleaf, Bootstrap, CSS personalizado, JavaScript
- **Back-end**: Java 11, Spring (Web, Security, MVC)
- **Banco de Dados**: MySQL com acesso via JDBC puro (sem ORM)
- **Autenticação**: Sistema de login seguro com Spring Security

---

## Módulos

### Produtos
- CRUD completo: cadastrar, listar, atualizar e deletar produtos.
- Campos: `nome`, `descricao`, `tipo`, `valor`, `quantidade`.
- Tipos definidos via menu: Adubo, Plantas, Vasos, etc.
- Quantidade gerenciada exclusivamente pelo módulo de estoque.

### Funcionários
- Cadastro com validação de CPF (11 dígitos, com ou sem pontuação).
- Campos: `nome`, `cpf`, `cargo`, `data_nasc`, `telefone`, `endereco`.
- Cargos disponíveis: Diretor, Auxiliar, Estoquista.
- Exclusão lógica com listagem separada de ativos e inativos.
- Auditoria de alterações via triggers.

### Clientes
- Cadastro com validação de CPF (11 dígitos) e CNPJ (14 dígitos).
- Tipo PF/PJ definido automaticamente pela quantidade de dígitos.
- Exclusão lógica com listagens separadas.
- Auditoria de alterações via triggers.

### Pedidos
- Criação de pedidos de venda vinculados a clientes ativos.
- Cadastro de itens por pedido com tabela intermediária (`item_pedido`).

### Estoque (Movimentação)
- Registro de entradas e saídas de produtos.
- CRUD completo para movimentações.
- Filtro por tipo de movimentação (entrada, saída, todas).

---

## Banco de Dados

- Gerenciado em MySQL.
- Tabelas criadas via `setup_database.sql`.
- Estrutura: `produto`, `funcionario`, `cliente`, `pedido`, `item_pedido`, `cupom`, `movimentacao_estoque`, `historico_estoque`, `historico_funcionario`, `historico_cliente`.
- Triggers de auditoria para rastreamento de alterações.

---

## Tecnologias Utilizadas

- Java 11
- Spring Boot 2.7.9
- MySQL 8.0
- Thymeleaf
- Bootstrap
- Spring Security
- JDBC (sem ORM)
- Padrão DAO
- Maven

---

## Instalação e Configuração

### Pré-requisitos

- JDK 11
- MySQL 8.0
- Maven

### Passos

1. **Clone o repositório**
   ```bash
   git clone https://github.com/joaopedrofds/Estokar---SistemaDeEstoque.git
   cd Estokar---SistemaDeEstoque
   ```

2. **Configure o banco de dados**
   ```bash
   mysql -u root -p < setup_database.sql
   ```

3. **Verifique as configurações em application.properties**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/studiomuda
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   ```

4. **Compile e execute**
   ```bash
   mvn clean package
   java -jar target/estoque-0.0.1-SNAPSHOT.jar
   ```

5. **Acesse a aplicação**
   ```
   http://localhost:8081
   ```

---

## Dashboard e Relatórios

- Top 10 produtos mais vendidos
- Top 10 clientes com mais pedidos
- Resumo financeiro mensal
- Alerta de estoque crítico
- Pedidos pendentes
- Gráficos interativos de vendas e movimentações

---

## Segurança

- Senhas criptografadas no banco de dados
- Validação de dados em todos os formulários
- Proteção contra SQL Injection via PreparedStatements
