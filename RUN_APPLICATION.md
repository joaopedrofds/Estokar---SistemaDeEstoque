# Tutorial: Como Rodar a Aplicação Estoque Studio Muda

## Pré-requisitos

- **Java 11+** (JDK)
- **Maven 3.6+**
- **MySQL 8.0+**
- **Git** (opcional, para clonar)

---

## 1. Configurar o Banco de Dados MySQL

### 1.1 Iniciar o MySQL
```bash
# Ubuntu/Debian/WSL
sudo service mysql start

# Ou se estiver usando systemd
sudo systemctl start mysql

# Verificar se está rodando
sudo service mysql status
```

### 1.2 Criar o banco e usuário
```bash
mysql -u root -p
```

No prompt do MySQL:
```sql
CREATE DATABASE IF NOT EXISTS studiomuda CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'estokar'@'localhost' IDENTIFIED BY 'estokar123';
GRANT ALL PRIVILEGES ON studiomuda.* TO 'estokar'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 1.3 Executar o script de criação das tabelas
```bash
mysql -u estokar -pestokar123 studiomuda < setup_database.sql
```

> **Nota:** O arquivo `setup_database.sql` já contém todas as tabelas necessárias incluindo:
> - `devolucao` e `item_devolucao` (para a aba de Devoluções)
> - `cupom_uso` (para Histórico de Cupons)
> - Todas as tabelas do sistema

---

## 2. Configurar a Aplicação

### 2.1 Verificar o arquivo de configuração
O arquivo `src/main/resources/application.properties` já está configurado:

```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/studiomuda?useSSL=false&serverTimezone=America/Sao_Paulo
spring.datasource.username=estokar
spring.datasource.password=estokar123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> **Importante:** `ddl-auto=update` faz o Hibernate criar/atualizar tabelas automaticamente baseado nas entidades JPA.

---

## 3. Compilar e Rodar

### 3.1 Compilar o projeto
```bash
cd /home/jpa2019/estokar
mvn clean compile
```

### 3.2 Rodar a aplicação
```bash
mvn spring-boot:run
```

A aplicação vai subir em: **http://localhost:8081**

---

## 4. Acessar a Aplicação

### 4.1 Credenciais padrão
O script `setup_database.sql` cria usuários padrão:

| Usuário | Senha | Perfil |
|---------|-------|--------|
| `admin` | `Admin@123` | Administrador (acesso total) |
| `gerente` | `Gerente@123` | Gerente |
| `operador` | `Operador@123` | Operador |

### 4.2 Login
1. Abra o navegador em: **http://localhost:8081**
2. Faça login com: **admin / Admin@123**

---

## 5. Navegar para as Abas

### 5.1 Devoluções
- **Menu:** Operação → Devoluções
- **URL direta:** http://localhost:8081/devolucoes
- Permite: listar, criar, visualizar, aprovar/rejeitar devoluções

### 5.2 Histórico de Cupons
- **Menu:** Catálogo → Histórico de Cupons
- **URL direta:** http://localhost:8081/cupons/uso
- Mostra todo o histórico de uso de cupons

---

## 6. Verificação Rápida via Terminal

```bash
# 1. Login e obter cookie
curl -c cookies.txt -X POST http://localhost:8081/login \
  -d "username=admin&password=Admin@123"

# 2. Testar Devoluções
curl -b cookies.txt http://localhost:8081/devolucoes

# 3. Testar Histórico de Cupons
curl -b cookies.txt http://localhost:8081/cupons/uso
```

Ambos devem retornar **HTTP 200** com HTML completo.

---

## 7. Estrutura do Projeto

```
estokar/
├── src/
│   ├── main/
│   │   ├── java/com/studiomuda/estoque/
│   │   │   ├── controller/     # Controllers (DevolucaoController, CupomUsoController, etc)
│   │   │   ├── dao/            # Data Access Objects
│   │   │   ├── model/          # Entidades JPA
│   │   │   ├── service/        # Serviços de negócio
│   │   │   └── config/         # Configurações (SecurityConfig, etc)
│   │   └── resources/
│   │       ├── templates/      # Templates Thymeleaf
│   │       │   ├── devolucoes/lista.html
│   │       │   ├── cupons/uso.html
│   │       │   └── layout.html
│   │       └── application.properties
│   └── test/
├── setup_database.sql          # Script completo do banco
├── pom.xml                     # Dependências Maven
└── RUN_APPLICATION.md          # Este arquivo
```

---

## 8. Troubleshooting

### Erro: "Table 'studiomuda.devolucao' doesn't exist"
```bash
# Execute o script SQL novamente
mysql -u estokar -pestokar123 studiomuda < setup_database.sql
```

### Erro de conexão com banco
1. Verifique se MySQL está rodando: `service mysql status`
2. Confira credenciais em `application.properties`
3. Teste conexão: `mysql -u estokar -pestokar123 -e "SELECT 1"`

### Porta 8081 já em uso
```bash
# Matar processo na porta 8081
lsof -ti:8081 | xargs kill -9

# Ou mudar a porta em application.properties
server.port=8082
```

### Limpar e recompilar tudo
```bash
mvn clean spring-boot:run
```

---

## 9. Comandos Úteis

```bash
# Ver logs da aplicação
tail -f /tmp/spring-boot.log

# Parar a aplicação
pkill -f "spring-boot:run"

# Rodar testes
mvn test

# Gerar JAR executável
mvn clean package
java -jar target/estoque-0.0.1-SNAPSHOT.jar
```

---

## 10. Resumo Rápido (One-liner)

```bash
# Tudo em um comando (após configurar MySQL)
cd /home/jpa2019/estokar && \
mysql -u estokar -pestokar123 studiomuda < setup_database.sql && \
mvn spring-boot:run
```

**Acesse:** http://localhost:8081  
**Login:** admin / Admin@123  
**Devoluções:** http://localhost:8081/devolucoes  
**Histórico Cupons:** http://localhost:8081/cupons/uso