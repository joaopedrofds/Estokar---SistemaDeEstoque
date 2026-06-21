# language: pt
Funcionalidade: Rastreabilidade de alteracoes de preco
  Como gestor comercial
  Quero registrar cada alteracao efetiva de preco
  Para auditar o valor anterior, o novo valor e a variacao percentual

  Cenario: Alteracao de preco gera historico
    Dado um evento de preco de "100.00" para "125.00"
    Quando o observer de historico processa o evento
    Entao um historico deve ser persistido
    E a variacao registrada deve ser "25.00" por cento

  Cenario: Preco inalterado nao gera historico
    Dado um evento de preco de "100.00" para "100.00"
    Quando o observer de historico processa o evento
    Entao nenhum historico deve ser persistido

  Cenario: Preco anterior zero nao produz divisao invalida
    Dado um evento de preco de "0.00" para "50.00"
    Quando o observer de historico processa o evento
    Entao um historico deve ser persistido
    E a variacao registrada deve ser nula
