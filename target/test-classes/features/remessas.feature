# language: pt
Funcionalidade: Roteirizacao de Remessas com Conflitos de Docas
  Como gestor de logistica
  Quero bloquear agendamentos que excedem capacidade ou chocam horarios
  Para manter a operacao das docas previsivel e segura

  Cenario: Bloqueio por capacidade excedida com sugestao de janela alternativa
    Dado que a doca "Doca Norte" suporta 10 paletes por dia
    E ja existem 8 paletes agendados para essa doca
    E existe uma doca alternativa "Doca Sul" com capacidade disponivel
    Quando solicito uma remessa de 4 paletes para a doca principal
    Entao o agendamento da remessa deve ser bloqueado
    E o sistema deve sugerir uma janela alternativa na doca "Doca Sul"
