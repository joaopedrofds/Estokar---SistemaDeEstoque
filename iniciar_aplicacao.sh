#!/bin/bash
# Script de inicialização completa da aplicação com todos os testes

echo "===== CONFIGURANDO E INICIANDO APLICAÇÃO COM NOVAS FUNCIONALIDADES ====="
echo

# Diretório da aplicação
APP_DIR="/home/temp/Área de trabalho/studiomudaofc/EstoqueStudioMudaBD"
cd "$APP_DIR" || { echo "Diretório não encontrado!"; exit 1; }

# 1. Recriar o banco de dados com a estrutura atualizada
echo "1. Recriando banco de dados com estrutura atualizada..."
echo "   Por favor, digite seu nome de usuário MySQL:"
read -r mysql_user
echo "   Digite sua senha MySQL:"
read -rs mysql_pass

echo "   Executando setup_database.sql..."
mysql -u "$mysql_user" -p"$mysql_pass" < setup_database.sql 2>/dev/null

if [ $? -eq 0 ]; then
  echo "   ✓ Banco de dados criado com sucesso!"
else
  echo "   ✗ Erro ao criar banco de dados. Verifique suas credenciais."
  exit 1
fi

# 2. Inserir dados de teste para todas as funcionalidades
echo
echo "2. Inserindo dados de teste para todas as funcionalidades..."
mysql -u "$mysql_user" -p"$mysql_pass" < dados_teste.sql 2>/dev/null

if [ $? -eq 0 ]; then
  echo "   ✓ Dados de teste inseridos com sucesso!"
else
  echo "   ✗ Erro ao inserir dados de teste."
  exit 1
fi

# 3. Verificar se as tabelas e campos foram criados corretamente
echo
echo "3. Verificando tabelas e campos do banco de dados..."

CUPOM_FIELDS=$(mysql -u "$mysql_user" -p"$mysql_pass" -e "USE studiomuda; DESC cupom;" 2>/dev/null | awk '{print $1}' | grep -v "Field")
PEDIDO_FIELDS=$(mysql -u "$mysql_user" -p"$mysql_pass" -e "USE studiomuda; DESC pedido;" 2>/dev/null | awk '{print $1}' | grep -v "Field")

if [[ "$CUPOM_FIELDS" == *"id"* && "$CUPOM_FIELDS" == *"codigo"* && "$CUPOM_FIELDS" == *"valor"* ]]; then
  echo "   ✓ Tabela cupom configurada corretamente."
else
  echo "   ✗ Problemas na estrutura da tabela cupom!"
fi

if [[ "$PEDIDO_FIELDS" == *"funcionario_id"* && "$PEDIDO_FIELDS" == *"cupom_id"* && "$PEDIDO_FIELDS" == *"valor_desconto"* ]]; then
  echo "   ✓ Tabela pedido configurada corretamente com os novos campos."
else
  echo "   ✗ Problemas na estrutura da tabela pedido! Faltam campos novos."
fi

# 4. Executar consultas de teste para validar as funcionalidades
echo
echo "4. Executando consultas de teste para validar as funcionalidades..."
mysql -u "$mysql_user" -p"$mysql_pass" < teste_funcionalidades.sql > resultado_testes.txt 2>/dev/null

if [ $? -eq 0 ]; then
  echo "   ✓ Testes executados com sucesso! Resultados salvos em resultado_testes.txt"
else
  echo "   ✗ Erro ao executar consultas de teste."
fi

# 5. Iniciar a aplicação
echo
echo "5. Deseja iniciar a aplicação agora? (S/n)"
read -r start_app

if [[ "$start_app" == "S" || "$start_app" == "s" || "$start_app" == "" ]]; then
  echo "   Iniciando a aplicação Spring Boot..."
  echo "   Aguarde, este processo pode levar alguns minutos."
  echo "   Após inicialização, acesse: http://localhost:8081"
  echo
  echo "   Para interromper a aplicação, pressione CTRL+C"
  echoS
  mvn spring-boot:run
else
  echo "   Para iniciar a aplicação manualmente, execute: mvn spring-boot:ruSn"
  echo "   Após inicialização, acesse: http://localhost:8081"
fi

echo
echo "===== PROCESSO DE INICIALIZAÇÃO CONCLUÍDO ====="
