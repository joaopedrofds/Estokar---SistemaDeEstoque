# language: pt
Funcionalidade: Cotacao e expedicao de fretes
  Como operador de logistica
  Quero cotar fretes com cache, contingencia e limite de uso
  Para manter a operacao disponivel sem sobrecarregar a integracao externa

  Cenario: Cotacao valida usa a transportadora externa
    Dado uma cotacao de frete valida
    E a integracao externa retorna o valor 25.00
    Quando solicito a cotacao de frete
    Entao a origem da cotacao deve ser "API"
    E o valor cotado deve ser 25.00

  Cenario: Falha externa usa a tabela de contingencia
    Dado uma cotacao de frete valida
    E a integracao externa esta indisponivel
    E existe uma faixa de contingencia no valor 39.90
    Quando solicito a cotacao de frete
    Entao a origem da cotacao deve ser "CONTINGENCIA"
    E o valor cotado deve ser 39.90

  Cenario: Limite horario bloqueia nova cotacao
    Dado uma cotacao de frete valida
    E o usuario ja realizou 50 cotacoes externas na ultima hora
    Quando solicito a cotacao de frete
    Entao a cotacao deve ser bloqueada por limite
