# language: pt
Funcionalidade: Cancelamento de Pedido com Estorno de Estoque
  Como operador de vendas
  Quero cancelar pedidos validos com rastreabilidade
  Para devolver automaticamente os itens ao estoque sem duplicar estornos

  Cenario: Cancelamento de pedido dentro da alcada operacional
    Dado que existe um pedido "PENDENTE" com 2 itens e 5 unidades no total
    E o limite de cancelamento sem aprovacao e 10 unidades
    Quando o solicitante "Operador" cancela o pedido com justificativa "Cliente desistiu da compra"
    Entao o pedido deve ficar com status "CANCELADO"
    E o estoque deve receber estorno de 5 unidades

  Cenario: Cancelamento acima do limite fica pendente de aprovacao
    Dado que existe um pedido "CONCLUIDO" com 2 itens e 15 unidades no total
    E o limite de cancelamento sem aprovacao e 10 unidades
    Quando o solicitante "Operador" cancela o pedido com justificativa "Cliente solicitou cancelamento total"
    Entao o pedido deve ficar com status "CANCELAMENTO_PENDENTE_APROVACAO"
    E o estoque deve receber estorno de 0 unidades

  Cenario: Pedido ja cancelado bloqueia novo estorno
    Dado que existe um pedido "CANCELADO" com 1 itens e 3 unidades no total
    E o limite de cancelamento sem aprovacao e 10 unidades
    Quando o solicitante "Operador" cancela o pedido com justificativa "Nova tentativa indevida"
    Entao a tentativa deve ser bloqueada por idempotencia
    E o estoque deve receber estorno de 0 unidades

  Cenario: Gerente aprova cancelamento pendente acima do limite
    Dado que existe um pedido "CANCELAMENTO_PENDENTE_APROVACAO" com 2 itens e 15 unidades no total
    E o limite de cancelamento sem aprovacao e 10 unidades
    Quando o gerente "Gerente Operacional" aprova o cancelamento com justificativa "Aprovado por conferência gerencial"
    Entao o pedido deve ficar com status "CANCELADO"
    E o estoque deve receber estorno de 15 unidades
