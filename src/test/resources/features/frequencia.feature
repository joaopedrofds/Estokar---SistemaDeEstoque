# language: pt
Funcionalidade: Análise de Frequência de Compras
  Cenário: Classificação dinâmica de Cliente VIP
    Dado que o intervalo médio de compras do cliente é de 12 dias
    Quando o algoritmo de classificação é executado
    Então o cliente deve receber a etiqueta "VIP"
