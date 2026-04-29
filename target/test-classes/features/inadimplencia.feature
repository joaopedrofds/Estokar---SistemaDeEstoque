# language: pt
Funcionalidade: Gestão de Inadimplência
  Cenário: Bloqueio de cliente com faturas em atraso
    Dado que o cliente "Tech LTDA" tem faturas pendentes há mais de 45 dias
    Quando o sistema valida o perfil para um novo pedido
    Então o sistema deve impedir a venda
