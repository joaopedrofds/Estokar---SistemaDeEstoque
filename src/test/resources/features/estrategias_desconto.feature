# language: pt
Funcionalidade: Estrategias de desconto e restituicao
  Como operador de vendas
  Quero selecionar a regra conforme o tipo do cupom ou da devolucao
  Para aplicar desconto e restituicao sem alterar os fluxos principais

  Cenario: Cupom percentual calcula sobre o total
    Dado um pedido no valor de "200.00"
    E um cupom "PERCENTUAL" no valor de "10.00"
    Quando calculo o desconto pela estrategia
    Entao o desconto calculado deve ser "20.00"

  Cenario: Cupom fixo nao ultrapassa o total do pedido
    Dado um pedido no valor de "80.00"
    E um cupom "FIXO" no valor de "100.00"
    Quando calculo o desconto pela estrategia
    Entao o desconto calculado deve ser "80.00"

  Cenario: Percentual invalido nao concede desconto
    Dado um pedido no valor de "200.00"
    E um cupom "PERCENTUAL" no valor de "120.00"
    Quando calculo o desconto pela estrategia
    Entao o desconto calculado deve ser "0.00"

  Cenario: Devolucao com credito em loja usa a estrategia de credito
    Dado uma devolucao com tipo de restituicao "CREDITO_LOJA"
    Quando seleciono a estrategia de restituicao
    Entao a descricao da estrategia de restituicao deve ser "Crédito em loja"

  Cenario: Devolucao com troca usa a estrategia de troca
    Dado uma devolucao com tipo de restituicao "TROCA"
    Quando seleciono a estrategia de restituicao
    Entao a descricao da estrategia de restituicao deve ser "Troca de produto no balcão"

  Cenario: Devolucao com estorno usa a estrategia de estorno
    Dado uma devolucao com tipo de restituicao "ESTORNO"
    Quando seleciono a estrategia de restituicao
    Entao a descricao da estrategia de restituicao deve ser "Estorno financeiro (cartão/boleto)"
