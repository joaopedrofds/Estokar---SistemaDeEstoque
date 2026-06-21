  ---
  🎬 Abertura (15s)
 
  ▎ "Sou responsável por 3 módulos: Segurança/Controle de Acesso, Relatório Financeiro e KPIs Operacionais.
  ▎ Cada um tem uma regra de negócio e um padrão de projeto."
  
  ---
  1️⃣  Segurança + RBAC (~90s) — /acesso
  
  1. Logar como operador / Operador@123
  2. Tentar abrir /acesso/perfis → aparece bloqueio 403
  3. Logar como admin / Admin@123 → abrir /acesso/logs → apontar a linha NEGADO (operador, ACESSO, LEITURA)
  4. Abrir /acesso/permissoes → mostrar a matriz perfil × recurso × operação

  ▎ Fala: "A autorização é interceptada na camada DAO (InterceptadorAutorizacaoDao): toda tentativa é 
  ▎ registrada em log; sem permissão, bloqueia."

  2️⃣  Relatório Financeiro (~90s) — /financeiro
  
  1. Como admin, abrir /financeiro/relatorios
  2. Gerar: template Demonstrativo Mensal, período 01/01/2024 → 31/12/2024
  3. Mostrar o consolidado: Receita R$ 45.229,70, despesas, Margem 61,66%, Ticket Médio R$ 1.103,16
  4. Clicar Exportar CSV → mostrar o rastreio ("PedidoDAO: 41 pedidos pagos")

  ▎ Fala: "Consolida receitas/despesas a partir dos dados reais do sistema, com rastreabilidade da origem e
  ▎ exportação."

  3️⃣  KPIs Operacionais (~90s) — /kpis
  
  1. Em /kpis, abrir meta de um indicador (/kpis/meta/nova/1) e salvar
  3. Mostrar o consolidado: Receita R$ 45.229,70, despesas, Margem 61,66%, Ticket Médio R$ 1.103,16
  4. Clicar Exportar CSV → mostrar o rastreio ("PedidoDAO: 41 pedidos pagos")

  ▎ Fala: "Consolida receitas/despesas a partir dos dados reais do sistema, com rastreabilidade da origem e
  ▎ exportação."

  3️⃣  KPIs Operacionais (~90s) — /kpis

  1. Em /kpis, abrir meta de um indicador (/kpis/meta/nova/1) e salvar
  2. Clicar Recalcular → gera snapshot imutável; se a meta violar, cria alerta automático
  3. Abrir /kpis/alertas → resolver um alerta
  4. Mostrar /kpis/snapshots (histórico preservado)
  4. Clicar Exportar CSV → mostrar o rastreio ("PedidoDAO: 41 pedidos pagos")

  ▎ Fala: "Consolida receitas/despesas a partir dos dados reais do sistema, com rastreabilidade da origem e
  ▎ exportação."

  3️⃣  KPIs Operacionais (~90s) — /kpis

  1. Em /kpis, abrir meta de um indicador (/kpis/meta/nova/1) e salvar
  2. Clicar Recalcular → gera snapshot imutável; se a meta violar, cria alerta automático
  3. Abrir /kpis/alertas → resolver um alerta
  4. Mostrar /kpis/snapshots (histórico preservado)

  1. Em /kpis, abrir meta de um indicador (/kpis/meta/nova/1) e salvar
  2. Clicar Recalcular → gera snapshot imutável; se a meta violar, cria alerta automático
  3. Abrir /kpis/alertas → resolver um alerta
  4. Mostrar /kpis/snapshots (histórico preservado)
  1. Em /kpis, abrir meta de um indicador (/kpis/meta/nova/1) e salvar
  2. Clicar Recalcular → gera snapshot imutável; se a meta violar, cria alerta automático
  3. Abrir /kpis/alertas → resolver um alerta
  4. Mostrar /kpis/snapshots (histórico preservado)
  2. Clicar Recalcular → gera snapshot imutável; se a meta violar, cria alerta automático
  3. Abrir /kpis/alertas → resolver um alerta
  4. Mostrar /kpis/snapshots (histórico preservado)
  4. Mostrar /kpis/snapshots (histórico preservado)
