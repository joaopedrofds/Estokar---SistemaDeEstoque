# language: pt
Funcionalidade: Solicitacao e aprovacao de ajuste manual de estoque
  Como operador de estoque
  Quero solicitar ajustes manuais com analise de risco
  Para corrigir saldos com rastreabilidade e aprovacao quando necessario

  Cenario: Sobra pequena e aplicada automaticamente
    Dado que o produto possui saldo atual de 20 unidades
    E o limite de ajuste automatico e 5 unidades
    Quando solicito um ajuste "SOBRA" de 2 unidades com justificativa "Sobra encontrada na conferência"
    Entao a solicitacao deve ficar com status "APLICADO_AUTOMATICAMENTE"
    E o saldo projetado deve ser 22 unidades

  Cenario: Perda acima da alcada fica pendente
    Dado que o produto possui saldo atual de 20 unidades
    E o limite de ajuste automatico e 5 unidades
    Quando solicito um ajuste "PERDA" de 8 unidades com justificativa "Perda operacional identificada"
    Entao a solicitacao deve ficar com status "PENDENTE_APROVACAO"
    E o saldo projetado deve ser 12 unidades

  Cenario: Perda que deixa estoque negativo e bloqueada
    Dado que o produto possui saldo atual de 3 unidades
    E o limite de ajuste automatico e 5 unidades
    Quando solicito um ajuste "AVARIA" de 4 unidades com justificativa "Avaria encontrada na inspeção"
    Entao o ajuste deve ser bloqueado por saldo insuficiente
