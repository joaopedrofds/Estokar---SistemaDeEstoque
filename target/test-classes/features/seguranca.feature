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
      | usuario    | senha        |
      | diretor    | Diretor@123  |
      | auxiliar   | Auxiliar@123 |
      | estoquista | Estoque@123  |

  Cenario: Login invalido volta para a tela de login com erro
    Quando envio login com usuario "diretor" e senha "senha-errada"
    Entao sou redirecionado para "/login?error=true"

  Esquema do Cenario: Acesso permitido por papel
    Dado que estou autenticado com o papel "<papel>"
    Quando consulto a permissao da rota "<rota>"
    Entao o acesso e permitido

    Exemplos:
      | papel      | rota           |
      | DIRETOR    | /dashboard     |
      | AUXILIAR   | /dashboard     |
      | DIRETOR    | /funcionarios  |
      | DIRETOR    | /cupons        |
      | DIRETOR    | /produtos      |
      | AUXILIAR   | /produtos      |
      | ESTOQUISTA | /produtos      |

  Esquema do Cenario: Acesso bloqueado por papel
    Dado que estou autenticado com o papel "<papel>"
    Quando consulto a permissao da rota "<rota>"
    Entao o acesso e bloqueado

    Exemplos:
      | papel      | rota          |
      | ESTOQUISTA | /dashboard    |
      | AUXILIAR   | /cupons       |
      | ESTOQUISTA | /cupons       |
      | AUXILIAR   | /estoque      |
