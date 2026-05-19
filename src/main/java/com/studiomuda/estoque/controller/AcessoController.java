package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.dao.LogAcessoDAO;
import com.studiomuda.estoque.dao.PerfilAcessoDAO;
import com.studiomuda.estoque.dao.PermissaoPerfilDAO;
import com.studiomuda.estoque.dao.UsuarioAcessoDAO;
import com.studiomuda.estoque.model.LogAcesso;
import com.studiomuda.estoque.model.PerfilAcesso;
import com.studiomuda.estoque.model.PermissaoPerfil;
import com.studiomuda.estoque.model.UsuarioAcesso;
import com.studiomuda.estoque.security.OperacaoAcesso;
import com.studiomuda.estoque.security.RecursoAcesso;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/acesso")
public class AcessoController {
    private final PerfilAcessoDAO perfilAcessoDAO = new PerfilAcessoDAO();
    private final PermissaoPerfilDAO permissaoPerfilDAO = new PermissaoPerfilDAO();
    private final UsuarioAcessoDAO usuarioAcessoDAO = new UsuarioAcessoDAO();
    private final LogAcessoDAO logAcessoDAO = new LogAcessoDAO();
    private final PasswordEncoder passwordEncoder;

    public AcessoController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String index() {
        return "redirect:/acesso/perfis";
    }

    @GetMapping("/perfis")
    public String listarPerfis(@RequestParam(required = false) Integer editarId, Model model) {
        try {
            List<PerfilAcesso> perfis = perfilAcessoDAO.listarTodos();
            PerfilAcesso perfilForm = new PerfilAcesso();
            if (editarId != null) {
                PerfilAcesso existente = perfilAcessoDAO.buscarPorId(editarId);
                if (existente != null) {
                    perfilForm = existente;
                }
            }
            model.addAttribute("perfis", perfis);
            model.addAttribute("perfilForm", perfilForm);
            return "acesso/perfis";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao carregar perfis: " + e.getMessage());
            return "erro";
        }
    }

    @PostMapping("/perfis/salvar")
    public String salvarPerfil(@ModelAttribute PerfilAcesso perfil, RedirectAttributes redirectAttributes) {
        try {
            if (perfil.getNome() == null || perfil.getNome().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("mensagemErro", "O nome do perfil é obrigatório.");
                return "redirect:/acesso/perfis";
            }

            PerfilAcesso existente = perfilAcessoDAO.buscarPorNome(perfil.getNome().trim());
            if (existente != null && existente.getId() != perfil.getId()) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Já existe um perfil com este nome.");
                return "redirect:/acesso/perfis";
            }

            perfil.setNome(perfil.getNome().trim());
            if (perfil.getId() == 0) {
                perfilAcessoDAO.inserir(perfil);
                redirectAttributes.addFlashAttribute("mensagem", "Perfil cadastrado com sucesso.");
            } else {
                perfilAcessoDAO.atualizar(perfil);
                redirectAttributes.addFlashAttribute("mensagem", "Perfil atualizado com sucesso.");
            }
            return "redirect:/acesso/perfis";
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar perfil: " + e.getMessage());
            return "redirect:/acesso/perfis";
        }
    }

    @GetMapping("/perfis/inativar/{id}")
    public String inativarPerfil(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            perfilAcessoDAO.inativar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Perfil inativado com sucesso.");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao inativar perfil: " + e.getMessage());
        }
        return "redirect:/acesso/perfis";
    }

    @GetMapping("/permissoes")
    public String gerenciarPermissoes(@RequestParam(required = false) Integer perfilId, Model model) {
        try {
            List<PerfilAcesso> perfis = perfilAcessoDAO.listarAtivos();
            if (perfis.isEmpty()) {
                model.addAttribute("mensagemErro", "Cadastre ao menos um perfil ativo para configurar permissões.");
                model.addAttribute("perfis", perfis);
                return "acesso/permissoes";
            }

            int perfilSelecionadoId = perfilId != null ? perfilId : perfis.get(0).getId();
            Map<String, Set<String>> permissoesAtivas = permissaoPerfilDAO.carregarMapaPermissoes(perfilSelecionadoId);

            model.addAttribute("perfis", perfis);
            model.addAttribute("perfilSelecionadoId", perfilSelecionadoId);
            model.addAttribute("permissoesAtivas", permissoesAtivas);
            model.addAttribute("recursos", RecursoAcesso.values());
            model.addAttribute("operacoes", OperacaoAcesso.values());
            return "acesso/permissoes";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao carregar permissões: " + e.getMessage());
            return "erro";
        }
    }

    @PostMapping("/permissoes/salvar")
    public String salvarPermissoes(@RequestParam int perfilId,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        try {
            List<PermissaoPerfil> permissoes = new ArrayList<>();
            for (RecursoAcesso recurso : RecursoAcesso.values()) {
                for (OperacaoAcesso operacao : OperacaoAcesso.values()) {
                    String nomeCampo = "perm_" + recurso.name() + "_" + operacao.name();
                    boolean permitido = request.getParameter(nomeCampo) != null;
                    permissoes.add(new PermissaoPerfil(0, perfilId, recurso.name(), operacao.name(), permitido));
                }
            }

            permissaoPerfilDAO.substituirPermissoesPerfil(perfilId, permissoes);
            redirectAttributes.addFlashAttribute("mensagem", "Permissões atualizadas com sucesso.");
            return "redirect:/acesso/permissoes?perfilId=" + perfilId;
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar permissões: " + e.getMessage());
            return "redirect:/acesso/permissoes?perfilId=" + perfilId;
        }
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        try {
            model.addAttribute("usuarios", usuarioAcessoDAO.listarTodos());
            return "acesso/usuarios";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao carregar usuários: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/usuarios/novo")
    public String novoUsuario(Model model) {
        try {
            prepararTelaFormularioUsuario(model, new UsuarioAcesso(), true);
            return "acesso/usuario-form";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao preparar formulário: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable int id, Model model) {
        try {
            UsuarioAcesso usuario = usuarioAcessoDAO.buscarPorId(id);
            if (usuario == null) {
                return "redirect:/acesso/usuarios";
            }
            prepararTelaFormularioUsuario(model, usuario, false);
            return "acesso/usuario-form";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao carregar usuário: " + e.getMessage());
            return "erro";
        }
    }

    @PostMapping("/usuarios/salvar")
    public String salvarUsuario(@ModelAttribute UsuarioAcesso usuario,
                                @RequestParam(value = "perfilIds", required = false) List<Integer> perfilIds,
                                @RequestParam(value = "novaSenha", required = false) String novaSenha,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
                return retornarComErroFormularioUsuario(model, usuario, perfilIds, "O login do usuário é obrigatório.");
            }
            if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
                return retornarComErroFormularioUsuario(model, usuario, perfilIds, "O nome do usuário é obrigatório.");
            }
            if (perfilIds == null || perfilIds.isEmpty()) {
                return retornarComErroFormularioUsuario(model, usuario, perfilIds, "Selecione ao menos um perfil.");
            }

            usuario.setUsername(usuario.getUsername().trim());
            usuario.setNome(usuario.getNome().trim());
            usuario.setPerfilIds(perfilIds);

            if (usuario.getId() == 0) {
                if (novaSenha == null || novaSenha.trim().isEmpty()) {
                    return retornarComErroFormularioUsuario(model, usuario, perfilIds, "A senha é obrigatória no cadastro.");
                }
                usuario.setSenha(passwordEncoder.encode(novaSenha.trim()));
                usuarioAcessoDAO.inserir(usuario);
                redirectAttributes.addFlashAttribute("mensagem", "Usuário cadastrado com sucesso.");
            } else {
                boolean atualizarSenha = novaSenha != null && !novaSenha.trim().isEmpty();
                if (atualizarSenha) {
                    usuario.setSenha(passwordEncoder.encode(novaSenha.trim()));
                }
                usuarioAcessoDAO.atualizar(usuario, atualizarSenha);
                redirectAttributes.addFlashAttribute("mensagem", "Usuário atualizado com sucesso.");
            }
            return "redirect:/acesso/usuarios";
        } catch (SQLException e) {
            return retornarComErroFormularioUsuario(model, usuario, perfilIds, "Erro ao salvar usuário: " + e.getMessage());
        }
    }

    @GetMapping("/usuarios/inativar/{id}")
    public String inativarUsuario(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            usuarioAcessoDAO.inativar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Usuário inativado com sucesso.");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao inativar usuário: " + e.getMessage());
        }
        return "redirect:/acesso/usuarios";
    }

    @GetMapping("/logs")
    public String listarLogs(@RequestParam(required = false) String resultado,
                             @RequestParam(defaultValue = "200") int limite,
                             Model model) {
        try {
            List<LogAcesso> logs = logAcessoDAO.listarRecentes(resultado, limite);
            model.addAttribute("logs", logs);
            model.addAttribute("filtroResultado", resultado);
            model.addAttribute("limite", limite);
            return "acesso/logs";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao carregar logs de acesso: " + e.getMessage());
            return "erro";
        }
    }

    private void prepararTelaFormularioUsuario(Model model, UsuarioAcesso usuario, boolean novoCadastro) throws SQLException {
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfis", perfilAcessoDAO.listarAtivos());
        model.addAttribute("novoCadastro", novoCadastro);
    }

    private String retornarComErroFormularioUsuario(Model model,
                                                    UsuarioAcesso usuario,
                                                    List<Integer> perfilIds,
                                                    String mensagemErro) {
        try {
            if (perfilIds != null) {
                usuario.setPerfilIds(perfilIds);
            } else {
                usuario.setPerfilIds(new ArrayList<>());
            }
            prepararTelaFormularioUsuario(model, usuario, usuario.getId() == 0);
            model.addAttribute("mensagemErro", mensagemErro);
            return "acesso/usuario-form";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", mensagemErro);
            return "erro";
        }
    }
}
