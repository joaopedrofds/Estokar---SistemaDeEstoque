# language: pt
Funcionalidade: Alerta de reposicao
  Como gestor de estoque
  Quero registrar alertas persistidos para produtos abaixo do ponto de pedido
  Para tratar risco de ruptura com rastreabilidade

  Cenario: Produto abaixo do ponto de pedido cria alerta ativo
    Dado um parametro de reposicao para o produto "Shampoo" com fornecedor "Acme"
    E estoque atual de 4 unidades para reposicao
    E ponto de pedido de 10 unidades para reposicao
    E quantidade sugerida de 6 unidades
    Quando sincronizo os alertas de reposicao
    Entao deve existir 1 alerta ativo de reposicao
    E o alerta de reposicao deve referenciar o produto "Shampoo"

  Cenario: Nova verificacao atualiza o alerta existente sem duplicar
    Dado um parametro de reposicao para o produto "Condicionador" com fornecedor "Acme"
    E estoque atual de 3 unidades para reposicao
    E ponto de pedido de 8 unidades para reposicao
    E quantidade sugerida de 5 unidades
    E ja existe um alerta ativo para esse produto
    Quando sincronizo os alertas de reposicao
    Entao deve existir 1 alerta ativo de reposicao
    E o alerta de reposicao deve ter estoque atual 3

  Cenario: Estoque recuperado resolve automaticamente o alerta
    Dado um parametro de reposicao para o produto "Mascara" com fornecedor "Acme"
    E estoque atual de 12 unidades para reposicao
    E ponto de pedido de 10 unidades para reposicao
    E quantidade sugerida de 0 unidades
    E ja existe um alerta ativo para esse produto
    Quando sincronizo os alertas de reposicao
    Entao nao deve existir alerta ativo para o produto
    E deve existir 1 alerta resolvido de reposicao
