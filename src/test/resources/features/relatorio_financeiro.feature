# language: pt
Funcionalidade: Relatorio financeiro consolidado
  Como gestor financeiro
  Quero consolidar receita, custo e ajustes do periodo e calcular indicadores
  Para acompanhar o resultado e compara-lo com o periodo anterior

  Cenario: Consolidacao do resultado considerando ajustes manuais
    Dado uma receita operacional de 1000.0 e custo operacional de 600.0
    E ajustes de receita de 50.0 e ajustes de despesa de 30.0
    Quando consolido o relatorio financeiro
    Entao o resultado operacional deve ser 400.0
    E o resultado consolidado deve ser 420.0

  Cenario: Margem bruta e ticket medio a partir da receita do periodo
    Dado uma receita operacional de 1000.0 e custo operacional de 600.0
    E foram pagos 20 pedidos no periodo
    Quando calculo os indicadores financeiros
    Entao a margem bruta deve ser 40.0
    E o ticket medio deve ser 50.0

  Cenario: Margem bruta e zero quando nao ha receita no periodo
    Dado uma receita operacional de 0.0 e custo operacional de 600.0
    Quando calculo os indicadores financeiros
    Entao a margem bruta deve ser 0.0

  Cenario: Variacao percentual positiva entre periodos
    Quando comparo o valor atual 120.0 com o anterior 100.0
    Entao a variacao percentual deve ser 20.0

  Cenario: Variacao e de cem por cento quando nao havia base anterior
    Quando comparo o valor atual 80.0 com o anterior 0.0
    Entao a variacao percentual deve ser 100.0

  Cenario: Variacao e zero quando atual e anterior sao zero
    Quando comparo o valor atual 0.0 com o anterior 0.0
    Entao a variacao percentual deve ser 0.0
