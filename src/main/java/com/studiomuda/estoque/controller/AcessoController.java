package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.security.application.dto.PerfilAcessoDTO;
import com.studiomuda.estoque.security.application.dto.UsuarioAcessoDTO;
import com.studiomuda.estoque.security.dominio.ILogAcessoRepositorio;
import com.studiomuda.estoque.security.dominio.IPerfilAcessoRepositorio;
import com.studiomuda.estoque.security.dominio.IPermissaoPerfilRepositorio;
import com.studiomuda.estoque.security.dominio.IUsuarioAcessoRepositorio;
import com.studiomuda.estoque.security.dominio.LogAcesso;
import com.studiomuda.estoque.security.dominio.PerfilAcesso;
import com.studiomuda.estoque.security.dominio.PermissaoPerfil;
import com.studiomuda.estoque.security.dominio.UsuarioAcesso;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/acesso")
public class AcessoController {
    private final ILogAcessoRepositorio logAcessoRepo;
    private final IPerfilAcessoRepositorio perfilAcessoRepo;
    private final IPermissaoPerfilRepositorio permissaoPerfilRepo;
    private final IUsuarioAcessoRepositorio usuarioAcessoRepo;
    private final PasswordEncoder passwordEncoder;

    public AcessoController(PasswordEncoder passwordEncoder, ILogAcessoRepositorio logAcessoRepo,
                           IPerfilAcessoRepositorio perfilAcessoRepo,
                           IPermissaoPerfilRepositorio permissaoPerfilRepo,
                           IUsuarioAcessoRepositorio usuarioAcessoRepo) {
        this.passwordEncoder = passwordEncoder;
        this.logAcessoRepo = logAcessoRepo;
        this.perfilAcessoRepo = perfilAcessoRepo;
        this.permissaoPerfilRepo = permissaoPerfilRepo;
        this.usuarioAcessoRepo = usuarioAcessoRepo;
    }

    @GetMapping
    public String index() {
        return "redirect:/acesso/perfis";
    }

    @GetMapping("/perfis")
    public String listarPerfis(@RequestParam(required = false) Integer editarId, Model model) {
        try {
            List<PerfilAcesso> perfis = perfilAcessoRepo.listarTodos();
            PerfilAcessoDTO perfilForm = new PerfilAcessoDTO();
            if (editarId != null) {
                perfilForm = perfilAcessoRepo.buscarPorId(editarId).map(PerfilAcessoDTO::de).orElse(perfilForm);
            }
            model.addAttribute("perfis", perfis);
            model.addAttribute("perfilForm", perfilForm);
            return "acesso/perfis";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao carregar perfis: " + e.getMessage());
            return "erro";
        }
    }

    @PostMapping("/perfis/salvar")
    public String salvarPerfil(@ModelAttribute PerfilAcessoDTO perfilForm, RedirectAttributes redirectAttributes) {
        try {
            if (perfilForm.getNome() == null || perfilForm.getNome().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("mensagemErro", "O nome do perfil é obrigatório.");
                return "redirect:/acesso/perfis";
            }
            String nome = perfilForm.getNome().trim();

            PerfilAcesso existente = perfilAcessoRepo.buscarPorNome(nome).orElse(null);
            if (existente != null && existente.getId() != perfilForm.getId()) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Já existe um perfil com este nome.");
                return "redirect:/acesso/perfis";
            }

            boolean novo = perfilForm.getId() == 0;
            perfilAcessoRepo.salvar(new PerfilAcesso(perfilForm.getId(), nome,
                    perfilForm.getDescricao(), perfilForm.isAtivo()));
            redirectAttributes.addFlashAttribute("mensagem",
                    novo ? "Perfil cadastrado com sucesso." : "Perfil atualizado com sucesso.");
            return "redirect:/acesso/perfis";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar perfil: " + e.getMessage());
            return "redirect:/acesso/perfis";
        }
    }

    @GetMapping("/perfis/inativar/{id}")
    public String inativarPerfil(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            perfilAcessoRepo.buscarPorId(id).ifPresent(perfil -> {
                perfil.inativar();
                perfilAcessoRepo.salvar(perfil);
            });
            redirectAttributes.addFlashAttribute("mensagem", "Perfil inativado com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao inativar perfil: " + e.getMessage());
        }
        return "redirect:/acesso/perfis";
    }

    @GetMapping("/permissoes")
    public String gerenciarPermissoes(@RequestParam(required = false) Integer perfilId, Model model) {
        try {
            List<PerfilAcesso> perfis = perfilAcessoRepo.listarAtivos();
            if (perfis.isEmpty()) {
                model.addAttribute("mensagemErro", "Cadastre ao menos um perfil ativo para configurar permissões.");
                model.addAttribute("perfis", perfis);
                return "acesso/permissoes";
            }

            int perfilSelecionadoId = perfilId != null ? perfilId : perfis.get(0).getId();
            Map<String, Set<String>> permissoesAtivas = permissaoPerfilRepo.carregarMapaPermissoes(perfilSelecionadoId);

            model.addAttribute("perfis", perfis);
            model.addAttribute("perfilSelecionadoId", perfilSelecionadoId);
            model.addAttribute("permissoesAtivas", permissoesAtivas);
            model.addAttribute("recursos", RecursoAcesso.values());
            model.addAttribute("operacoes", OperacaoAcesso.values());
            return "acesso/permissoes";
        } catch (Exception e) {
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

            permissaoPerfilRepo.substituir(perfilId, permissoes);
            redirectAttributes.addFlashAttribute("mensagem", "Permissões atualizadas com sucesso.");
            return "redirect:/acesso/permissoes?perfilId=" + perfilId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar permissões: " + e.getMessage());
            return "redirect:/acesso/permissoes?perfilId=" + perfilId;
        }
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        try {
            Map<Integer, PerfilAcesso> perfisPorId = perfilAcessoRepo.listarTodos().stream()
                    .collect(Collectors.toMap(PerfilAcesso::getId, Function.identity()));
            List<UsuarioAcessoDTO> usuarios = usuarioAcessoRepo.listarTodos().stream()
                    .map(usuario -> UsuarioAcessoDTO.de(usuario,
                            resolverNomesPerfis(usuario.getPerfilIds(), perfisPorId)))
                    .collect(Collectors.toList());
            model.addAttribute("usuarios", usuarios);
            return "acesso/usuarios";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao carregar usuários: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/usuarios/novo")
    public String novoUsuario(Model model) {
        try {
            prepararTelaFormularioUsuario(model, new UsuarioAcessoDTO(), true);
            return "acesso/usuario-form";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao preparar formulário: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable int id, Model model) {
        try {
            UsuarioAcesso usuario = usuarioAcessoRepo.buscarPorId(id).orElse(null);
            if (usuario == null) {
                return "redirect:/acesso/usuarios";
            }
            prepararTelaFormularioUsuario(model, UsuarioAcessoDTO.de(usuario), false);
            return "acesso/usuario-form";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao carregar usuário: " + e.getMessage());
            return "erro";
        }
    }

    @PostMapping("/usuarios/salvar")
    public String salvarUsuario(@ModelAttribute UsuarioAcessoDTO usuario,
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

            String username = usuario.getUsername().trim();
            String nome = usuario.getNome().trim();
            usuario.setPerfilIds(perfilIds);

            String senha;
            if (usuario.getId() == 0) {
                if (novaSenha == null || novaSenha.trim().isEmpty()) {
                    return retornarComErroFormularioUsuario(model, usuario, perfilIds, "A senha é obrigatória no cadastro.");
                }
                senha = passwordEncoder.encode(novaSenha.trim());
                usuarioAcessoRepo.salvar(new UsuarioAcesso(
                        0, username, nome, senha, usuario.isAtivo(), perfilIds));
                redirectAttributes.addFlashAttribute("mensagem", "Usuário cadastrado com sucesso.");
            } else {
                boolean atualizarSenha = novaSenha != null && !novaSenha.trim().isEmpty();
                if (atualizarSenha) {
                    senha = passwordEncoder.encode(novaSenha.trim());
                } else {
                    UsuarioAcesso existente = usuarioAcessoRepo.buscarPorId(usuario.getId()).orElse(null);
                    if (existente == null) {
                        return retornarComErroFormularioUsuario(
                                model, usuario, perfilIds, "Usuário não encontrado para atualização.");
                    }
                    senha = existente.getSenha();
                }
                usuarioAcessoRepo.salvar(new UsuarioAcesso(
                        usuario.getId(), username, nome, senha, usuario.isAtivo(), perfilIds));
                redirectAttributes.addFlashAttribute("mensagem", "Usuário atualizado com sucesso.");
            }
            return "redirect:/acesso/usuarios";
        } catch (Exception e) {
            return retornarComErroFormularioUsuario(model, usuario, perfilIds, "Erro ao salvar usuário: " + e.getMessage());
        }
    }

    @GetMapping("/usuarios/inativar/{id}")
    public String inativarUsuario(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            usuarioAcessoRepo.inativar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Usuário inativado com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao inativar usuário: " + e.getMessage());
        }
        return "redirect:/acesso/usuarios";
    }

    @GetMapping("/logs")
    public String listarLogs(@RequestParam(required = false) String resultado,
                             @RequestParam(defaultValue = "200") int limite,
                             Model model) {
        try {
            List<LogAcesso> logs = logAcessoRepo.listarRecentes(resultado, limite);
            model.addAttribute("logs", logs);
            model.addAttribute("filtroResultado", resultado);
            model.addAttribute("limite", limite);
            return "acesso/logs";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao carregar logs de acesso: " + e.getMessage());
            return "erro";
        }
    }

    private void prepararTelaFormularioUsuario(Model model, UsuarioAcessoDTO usuario, boolean novoCadastro) {
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfis", perfilAcessoRepo.listarAtivos());
        model.addAttribute("novoCadastro", novoCadastro);
    }

    private String retornarComErroFormularioUsuario(Model model,
                                                    UsuarioAcessoDTO usuario,
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
        } catch (Exception e) {
            model.addAttribute("mensagemErro", mensagemErro);
            return "erro";
        }
    }

    private List<String> resolverNomesPerfis(List<Integer> perfilIds,
                                             Map<Integer, PerfilAcesso> perfisPorId) {
        if (perfilIds == null || perfilIds.isEmpty()) {
            return Collections.emptyList();
        }
        return perfilIds.stream()
                .map(perfisPorId::get)
                .filter(perfil -> perfil != null)
                .map(PerfilAcesso::getNome)
                .sorted()
                .collect(Collectors.toList());
    }
}
