# language: pt
Funcionalidade: Precificacao dinamica com margem de lucro
  Como gestor de precificacao
  Quero calcular o preco de venda com base no custo, impostos, despesas e margem
  Para salvar simulacoes e decidir se o preco pode ser aplicado ao produto

  Cenario: Simulacao aprovada com componentes do custo
    Dado um produto de id 1 com preco atual de 100.00 e custo de compra de 50.00
    E uma politica com margem desejada de 40.00, impostos de 10.00, despesas operacionais de 15.00 e desconto maximo de 12.00
    E uma margem minima global de 30.00 e desconto maximo global de 20.00
    Quando o sistema calcula a precificacao dinamica
    Entao o custo total deve ser 62.50
    E o preco sugerido deve ser 104.17
    E o status deve ser "APROVADO"
    E a simulacao deve conter 3 componentes de custo

  Cenario: Simulacao bloqueada quando a margem desejada fica abaixo da minima global
    Dado um produto de id 2 com preco atual de 90.00 e custo de compra de 40.00
    E uma politica com margem desejada de 20.00, impostos de 8.00, despesas operacionais de 10.00 e desconto maximo de 5.00
    E uma margem minima global de 30.00 e desconto maximo global de 20.00
    Quando o sistema calcula a precificacao dinamica
    Entao o status deve ser "BLOQUEADO_MARGEM"
    E a justificativa deve conter "abaixo da margem minima global"

  Cenario: Simulacao bloqueada quando o desconto compromete a margem minima
    Dado um produto de id 3 com preco atual de 120.00 e custo de compra de 60.00
    E uma politica com margem desejada de 35.00, impostos de 8.00, despesas operacionais de 7.00 e desconto maximo de 30.00
    E uma margem minima global de 30.00 e desconto maximo global de 30.00
    Quando o sistema calcula a precificacao dinamica
    Entao o status deve ser "BLOQUEADO_DESCONTO"
    E a justificativa deve conter "desconto seguro calculado"
