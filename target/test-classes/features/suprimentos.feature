# language: pt
Funcionalidade: Gestao de Suprimentos e Reposicao Inteligente
  Como gestor de suprimentos
  Quero que o sistema calcule dinamicamente o ponto de pedido
  Para gerar rascunhos de compra antes da ruptura de estoque

  Cenario: Geracao de rascunho quando o estoque atinge o ponto de pedido
    Dado que um produto possui estoque atual de 5 unidades
    E consumo medio diario de 2 unidades
    E lead time do fornecedor de 4 dias
    E margem de seguranca de 3 unidades
    Quando o sistema calcula a necessidade de reposicao
    Entao o ponto de pedido deve ser 11 unidades
    E uma ordem de compra em rascunho deve ser sugerida com 9 unidades
