#!/usr/bin/env bash
#
# load-demo-data.sh — carga única do banco do Estokar para demonstração.
#
# Executa, em ordem, o reset do schema (setup_database.sql), os migrations dos
# módulos que não estão no schema-base (frete, devolução, cupom, crédito),
# os patches de RBAC/funcionalidades, os triggers e, por fim, os dados de demo.
#
# Como o setup_database.sql faz DROP DATABASE / CREATE DATABASE, rodar este
# script é idempotente: cada execução recria o banco do zero.
#
# Uso:
#   ./load-demo-data.sh                 # schema + módulos + patches + demo
#   ./load-demo-data.sh --schema-only   # tudo, menos os dados de demonstração
#
# Configuração por variáveis de ambiente (com padrões):
#   MYSQL_HOST (127.0.0.1)  MYSQL_PORT (3306)
#   MYSQL_USER (root)       MYSQL_PASSWORD (vazio → mysql pergunta)
#   DB_NAME    (studiomuda)
#
# Exemplos:
#   MYSQL_USER=root MYSQL_PASSWORD=segredo ./load-demo-data.sh
#   MYSQL_PASSWORD=root ./load-demo-data.sh --schema-only

set -euo pipefail

# Diretório do projeto (onde este script vive), para rodar de qualquer lugar.
PROJ_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJ_DIR"

MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
DB_NAME="${DB_NAME:-studiomuda}"

CARREGAR_DEMO=true
if [[ "${1:-}" == "--schema-only" ]]; then
  CARREGAR_DEMO=false
elif [[ -n "${1:-}" ]]; then
  echo "Argumento desconhecido: $1 (use --schema-only ou nenhum)" >&2
  exit 2
fi

# Monta os argumentos de conexão. A senha, se informada, vai por MYSQL_PWD
# para não aparecer na lista de processos; sem senha, o mysql pergunta (-p).
MYSQL_ARGS=(-h "$MYSQL_HOST" -P "$MYSQL_PORT" -u "$MYSQL_USER")
if [[ -n "${MYSQL_PASSWORD:-}" ]]; then
  export MYSQL_PWD="$MYSQL_PASSWORD"
else
  MYSQL_ARGS+=(-p)
fi

# Migrations de módulos AUSENTES no setup_database.sql (idempotentes: IF NOT EXISTS).
#
# setup_database.sql é a FONTE ÚNICA DE VERDADE do schema. Ao longo do tempo ele
# absorveu vários módulos que antes viviam só em migrations; manter essas migrations
# aqui passou a causar erro (índice/coluna duplicados) ou divergência de schema.
# Por isso foram removidas da carga e do repositório:
#   - V8__create_devolucao_tables.sql        → setup já cria devolucao + item_devolucao
#                                              e os índices; rodar V8 dava
#                                              "Duplicate key name 'idx_devolucao_pedido'".
#   - migration antiga de precificação      → setup já cria as tabelas precificacao_*,
#                                              a coluna produto.custo e os índices; rodar
#                                              dava "Duplicate key name idx_precificacao_*".
#   - migration antiga do financeiro         → setup já cria/semeia o cluster Financeiro
#                                              em VARCHAR(36) UUID; a versão INT só
#                                              reintroduzia divergência de schema.
#   - setup isolado de KPIs                   → setup já cria/semeia o cluster de
#                                              Indicadores (UUID); reexecutar duplicava
#                                              metas (meta_indicador não tem chave única).
#   - V10__update_cupom_table.sql            → setup já cria a tabela cupom com as
#                                              colunas tipo_desconto/limite_usos/
#                                              usos_realizados/cliente_id/ativo. Além
#                                              disso o "ADD COLUMN IF NOT EXISTS" do V10
#                                              é sintaxe MariaDB, inválida no MySQL 8.
#
# Permanecem apenas as migrations cujas tabelas NÃO existem em setup_database.sql
# (cupom_uso, credito_cliente, frete).
MIGRACOES=(
  "src/main/resources/db/migration/V9__create_cupom_uso_table.sql"
  "src/main/resources/db/migration/V11__create_credito_cliente_table.sql"
  "frete_migration.sql"
)

# Patches de RBAC e ajustes incrementais por funcionalidade.
# Mantidos apenas os patches IDEMPOTENTES (CREATE TABLE IF NOT EXISTS, INSERT ...
# WHERE NOT EXISTS, ALTER/CREATE INDEX guardados por information_schema) — eles
# rodam sem erro mesmo com o schema já completo do setup_database.sql e ainda
# fornecem seeds próprios (parâmetros, docas, distribuidoras).
#
# Removidos por conflitarem com o setup (fonte única de verdade) ou por serem
# incompatíveis com o MySQL 8 / com o schema atual:
#   - patch antigo de ajuste de estoque      → CREATE INDEX não-guardado em índice
#                                               que o setup já cria
#                                               (idx_solicitacao_ajuste_status_data).
#   - patch antigo de inadimplência           → usava "ADD COLUMN IF NOT EXISTS"
#                                               (sintaxe MariaDB, inválida no MySQL 8)
#                                               e CREATE INDEX duplicado; tudo já no setup.
#   - triggers antigos                        → escritos para um schema antigo com
#                                               tabelas no plural (produtos/clientes/
#                                               pedidos/movimentacoes_estoque) e colunas
#                                               inexistentes (preco, data_pedido, ...).
#                                               Falha já no 1º CREATE TRIGGER e, se
#                                               "corrigido", duplicaria a baixa de estoque.
PATCHES=(
  "atualizacao_acesso_operador_pedidos.sql"
  "atualizacao_cancelamento_pedido.sql"
  "atualizacao_inventario_periodico.sql"
  "atualizacao_remessas.sql"
)

# Dados de demonstração (dependem do schema + módulos já carregados).
DEMO=(
  "dados_teste.sql"
  "dados_teste_cobranca.sql"
  "dados_teste_frete.sql"
  "dados_demo_frete_enriquecido.sql"
)

# Executa um .sql. O primeiro argumento "sem_banco" roda sem selecionar o
# database (necessário para o setup, que cria o próprio banco).
rodar_sql() {
  local arquivo="$1"
  local sem_banco="${2:-}"
  if [[ ! -f "$arquivo" ]]; then
    echo "  ⚠️  ignorando (não encontrado): $arquivo" >&2
    return 0
  fi
  echo "  ▶ $arquivo"
  if [[ "$sem_banco" == "sem_banco" ]]; then
    mysql "${MYSQL_ARGS[@]}" < "$arquivo"
  else
    mysql "${MYSQL_ARGS[@]}" "$DB_NAME" < "$arquivo"
  fi
}

echo "==> 1/4 Reset do schema + seeds base (setup_database.sql)"
rodar_sql "setup_database.sql" sem_banco

echo "==> 2/4 Migrations de módulos (frete, devolução, cupom, crédito, KPIs)"
for f in "${MIGRACOES[@]}"; do rodar_sql "$f"; done

echo "==> 3/4 Patches de RBAC, funcionalidades e triggers"
for f in "${PATCHES[@]}"; do rodar_sql "$f"; done

if $CARREGAR_DEMO; then
  echo "==> 4/4 Dados de demonstração"
  for f in "${DEMO[@]}"; do rodar_sql "$f"; done
else
  echo "==> 4/4 Dados de demonstração: PULADO (--schema-only)"
fi

echo ""
echo "✅ Banco '$DB_NAME' carregado. Suba a aplicação com: mvn spring-boot:run"
echo "   Usuários: admin/Admin@123 · gerente/Gerente@123 · operador/Operador@123"
