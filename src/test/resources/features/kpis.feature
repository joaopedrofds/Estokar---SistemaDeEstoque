# language: pt
Funcionalidade: Gestao de indicadores operacionais com metas e alertas persistidos
  Como gestor operacional
  Quero definir metas para indicadores e recalcular seus valores a partir das transacoes reais
  Para ser alertado automaticamente quando uma meta for violada

  Cenario: Valor dentro da meta nao gera violacao
    Dado um indicador "Ticket Medio" com meta de valor alvo 100.0 e operador "MAIOR_IGUAL"
    E o limite critico da meta e 50.0
    Quando o valor calculado do indicador e 120.0
    Entao a meta nao deve estar violada
    E a meta nao deve estar em nivel critico

  Cenario: Valor abaixo da meta gera violacao
    Dado um indicador "Ticket Medio" com meta de valor alvo 100.0 e operador "MAIOR_IGUAL"
    E o limite critico da meta e 50.0
    Quando o valor calculado do indicador e 80.0
    Entao a meta deve estar violada
    E a meta nao deve estar em nivel critico

  Cenario: Valor abaixo do limite critico gera alerta critico
    Dado um indicador "Ticket Medio" com meta de valor alvo 100.0 e operador "MAIOR_IGUAL"
    E o limite critico da meta e 50.0
    Quando o valor calculado do indicador e 40.0
    Entao a meta deve estar violada
    E a meta deve estar em nivel critico

  Cenario: Meta de minimizacao e violada quando o valor sobe demais
    Dado um indicador "Taxa de Cancelamento" com meta de valor alvo 10.0 e operador "MENOR_IGUAL"
    E o limite critico da meta e 25.0
    Quando o valor calculado do indicador e 15.0
    Entao a meta deve estar violada
    E a meta nao deve estar em nivel critico

  Cenario: Periodo invertido e bloqueado pela cadeia de calculo
    Dado uma calculadora decorada que produz o valor cru 42.0
    Quando recalculo o indicador com periodo de "2026-06-30" a "2026-06-01"
    Entao o calculo deve ser bloqueado por periodo invalido

  Cenario: Valor calculado e arredondado para duas casas decimais pela cadeia de calculo
    Dado uma calculadora decorada que produz o valor cru 33.33333
    Quando recalculo o indicador com periodo de "2026-06-01" a "2026-06-30"
    Entao o valor recalculado deve ser 33.33
