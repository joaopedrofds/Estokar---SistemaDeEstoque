# language: pt
Funcionalidade: Gestão de Inadimplência
  Cenário: Bloqueio de cliente com faturas em atraso
    Dado que o cliente "Tech LTDA" tem faturas pendentes há mais de 45 dias
    Quando o sistema valida o perfil para um novo pedido
    Então o sistema deve impedir a venda

  Cenário: Atraso abaixo do limite permite a venda
    Dado que o cliente "Comercial Recife" tem faturas pendentes há mais de 20 dias
    Quando o sistema valida o perfil para um novo pedido
    Então o sistema deve permitir a venda

  Cenário: Acordo ativo libera cliente com atraso
    Dado que o cliente "Mercado Central" tem faturas pendentes há mais de 60 dias
    E o cliente possui acordo de pagamento ativo
    Quando o sistema valida o perfil para um novo pedido
    Então o sistema deve permitir a venda
