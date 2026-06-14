# language: pt
Funcionalidade: Inventario periodico com ajuste inteligente de diferenca
  Como gerente de estoque
  Quero abrir sessoes de inventario, registrar contagens e fechar com ajuste inteligente
  Para manter o saldo sistemico alinhado ao saldo fisico com rastreabilidade

  Cenario: Bloqueio de sessao simultanea no mesmo setor
    Dado que existe uma sessao de inventario "EM_ANDAMENTO" para o setor "Estoque Central"
    Quando tento abrir nova sessao de inventario para o setor "Estoque Central"
    Entao a abertura deve ser bloqueada por concorrencia

  Cenario: Fechamento automatico dentro da tolerancia
    Dado que existe uma sessao de inventario "EM_ANDAMENTO" para o setor "Loja"
    E o produto "Vaso Ceramico" possui saldo de sistema 10 unidades
    E a tolerancia do inventario e 3 unidades
    Quando o auxiliar registra contagem fisica de 12 unidades
    E o gerente fecha a sessao de inventario
    Entao a sessao deve ficar com status "FECHADO"
    E o saldo final do produto deve ser 12 unidades
    E deve ser gerada uma movimentacao "entrada" de 2 unidades

  Cenario: Divergencia severa exige aprovacao gerencial
    Dado que existe uma sessao de inventario "EM_ANDAMENTO" para o setor "Loja"
    E o produto "Cachepot" possui saldo de sistema 20 unidades
    E a tolerancia do inventario e 3 unidades
    Quando o auxiliar registra contagem fisica de 10 unidades
    E o operador tenta fechar a sessao de inventario
    Entao a sessao deve ficar com status "AGUARDANDO_APROVACAO"
    E o saldo final do produto deve ser 20 unidades
    Quando o gerente fecha a sessao de inventario
    Entao a sessao deve ficar com status "FECHADO"
    E o saldo final do produto deve ser 10 unidades
    E deve ser gerada uma movimentacao "saida" de 10 unidades
