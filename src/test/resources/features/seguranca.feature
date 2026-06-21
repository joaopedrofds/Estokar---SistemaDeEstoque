# language: pt
Funcionalidade: Autenticacao e autorizacao por perfil
  Como usuario do sistema de estoque
  Quero que as rotas sejam protegidas por login e papel
  Para que cada perfil acesse apenas as funcionalidades permitidas

  Cenario: Usuario anonimo e enviado para login ao acessar area protegida
    Dado que nao estou autenticado
    Quando acesso a rota "/dashboard"
    Entao sou redirecionado para a pagina de login

  Esquema do Cenario: Login de usuarios configurados em memoria
    Quando envio login com usuario "<usuario>" e senha "<senha>"
    Entao sou redirecionado para "/"

    Exemplos:
      | usuario  | senha        |
      | admin    | Admin@123    |
      | gerente  | Gerente@123  |
      | operador | Operador@123 |

  Cenario: Login invalido volta para a tela de login com erro
    Quando envio login com usuario "admin" e senha "senha-errada"
    Entao sou redirecionado para "/login?error=true"

  Esquema do Cenario: Acesso permitido por papel
    Dado que estou autenticado com o papel "<papel>"
    Quando consulto a permissao da rota "<rota>"
    Entao o acesso e permitido

    Exemplos:
      | papel               | rota                 |
      | ADMINISTRADOR       | /dashboard           |
      | GERENTE_OPERACIONAL | /dashboard           |
      | OPERADOR_VENDEDOR   | /dashboard           |
      | ADMINISTRADOR       | /funcionarios        |
      | GERENTE_OPERACIONAL | /funcionarios        |
      | ADMINISTRADOR       | /precificacao/simular |
      | OPERADOR_VENDEDOR   | /pedidos             |
      | OPERADOR_VENDEDOR   | /produtos            |

  Esquema do Cenario: Acesso bloqueado por papel
    Dado que estou autenticado com o papel "<papel>"
    Quando consulto a permissao da rota "<rota>"
    Entao o acesso e bloqueado

    Exemplos:
      | papel             | rota                  |
      | OPERADOR_VENDEDOR | /funcionarios         |
      | OPERADOR_VENDEDOR | /precificacao/simular |
      | OPERADOR_VENDEDOR | /produtos/historico/1 |
      | OPERADOR_VENDEDOR | /devolucoes/creditos  |
