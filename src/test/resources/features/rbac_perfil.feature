# language: pt
Funcionalidade: Controle de acesso por perfil (RBAC) na matriz permissao_perfil
  Como administrador de seguranca
  Quero conceder operacoes a perfis por recurso na matriz de permissoes
  Para que cada perfil execute apenas as operacoes autorizadas

  Contexto:
    Dado a matriz de permissoes:
      | perfil | recurso    | operacao  | permitido |
      | 1      | FINANCEIRO | LEITURA   | true      |
      | 1      | FINANCEIRO | ESCRITA   | true      |
      | 2      | FINANCEIRO | LEITURA   | true      |
      | 2      | PEDIDO     | APROVACAO | false     |

  Cenario: Perfil com concessao explicita tem acesso liberado
    Quando o perfil 1 solicita "ESCRITA" sobre "FINANCEIRO"
    Entao o acesso e concedido

  Cenario: Perfil sem a operacao exigida e bloqueado
    Quando o perfil 2 solicita "ESCRITA" sobre "FINANCEIRO"
    Entao o acesso e negado

  Cenario: Permissao marcada como nao permitida bloqueia o acesso
    Quando o perfil 2 solicita "APROVACAO" sobre "PEDIDO"
    Entao o acesso e negado

  Cenario: Recurso sem nenhuma permissao cadastrada e negado
    Quando o perfil 1 solicita "LEITURA" sobre "FUNCIONARIO"
    Entao o acesso e negado

  Cenario: Operacao de escrita e inferida pelo nome do metodo interceptado
    Quando o metodo "salvarRelatorio" e interceptado
    Entao a operacao inferida e "ESCRITA"

  Cenario: Metodos de consulta sao classificados como leitura
    Quando o metodo "listarRelatorios" e interceptado
    Entao a operacao inferida e "LEITURA"

  Cenario: Metodos de aprovacao sao classificados como operacao de aprovacao
    Quando o metodo "aprovarOrdemCompra" e interceptado
    Entao a operacao inferida e "APROVACAO"
