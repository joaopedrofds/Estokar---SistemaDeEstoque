package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.application.funcionario.AtualizarFuncionarioUseCase;
import com.studiomuda.estoque.application.funcionario.BuscarFuncionarioUseCase;
import com.studiomuda.estoque.application.funcionario.CadastrarFuncionarioUseCase;
import com.studiomuda.estoque.application.funcionario.ListarFuncionariosUseCase;
import com.studiomuda.estoque.application.funcionario.RemoverFuncionarioUseCase;
import com.studiomuda.estoque.domain.funcionario.Funcionario;
import com.studiomuda.estoque.domain.funcionario.exceptions.FuncionarioJaExisteException;
import com.studiomuda.estoque.presentation.web.funcionario.FuncionarioForm;
import com.studiomuda.estoque.presentation.web.funcionario.FuncionarioView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final CadastrarFuncionarioUseCase cadastrar;
    private final AtualizarFuncionarioUseCase atualizar;
    private final BuscarFuncionarioUseCase buscar;
    private final ListarFuncionariosUseCase listar;
    private final RemoverFuncionarioUseCase remover;

    public FuncionarioController(CadastrarFuncionarioUseCase cadastrar,
                                 AtualizarFuncionarioUseCase atualizar,
                                 BuscarFuncionarioUseCase buscar,
                                 ListarFuncionariosUseCase listar,
                                 RemoverFuncionarioUseCase remover) {
        this.cadastrar = cadastrar;
        this.atualizar = atualizar;
        this.buscar = buscar;
        this.listar = listar;
        this.remover = remover;
    }

    @GetMapping
    public String listarFuncionarios(Model model) {
        try {
            List<FuncionarioView> views = listar.listarTodos().stream()
                    .map(FuncionarioView::new).collect(Collectors.toList());
            model.addAttribute("funcionarios", views);
            return "funcionarios/lista";
        } catch (RuntimeException e) {
            model.addAttribute("mensagemErro", "Erro ao listar funcionários: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/novo")
    public String formNovoFuncionario(Model model) {
        FuncionarioForm form = new FuncionarioForm();
        form.setAtivo(true);
        form.setData_nasc(Date.valueOf(LocalDate.now()));
        model.addAttribute("funcionario", form);
        return "funcionarios/form";
    }

    @PostMapping("/salvar")
    public String salvarFuncionario(@ModelAttribute("funcionario") FuncionarioForm form,
                                    @RequestParam(value = "dataNascimento", required = false) String dataNascimento,
                                    Model model) {
        try {
            LocalDate dataParseada = (dataNascimento != null && !dataNascimento.isEmpty())
                    ? LocalDate.parse(dataNascimento) : null;
            if (form.getId() == 0) {
                cadastrar.executar(form.toCadastrarCommand(dataParseada));
            } else {
                atualizar.executar(form.toAtualizarCommand());
            }
            return "redirect:/funcionarios";
        } catch (IllegalArgumentException | FuncionarioJaExisteException e) {
            model.addAttribute("mensagemErro", e.getMessage());
            model.addAttribute("funcionario", form);
            return "funcionarios/form";
        } catch (RuntimeException e) {
            model.addAttribute("mensagemErro", "Erro inesperado ao salvar funcionário. Por favor, revise os dados e tente novamente.");
            model.addAttribute("funcionario", form);
            return "funcionarios/form";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarFuncionario(@PathVariable("id") int id, Model model) {
        Optional<Funcionario> funcionario = buscar.porId(id);
        if (funcionario.isPresent()) {
            model.addAttribute("funcionario", FuncionarioForm.desde(funcionario.get()));
            return "funcionarios/form";
        }
        return "redirect:/funcionarios";
    }

    @GetMapping("/excluir/{id}")
    public String excluirFuncionario(@PathVariable("id") int id) {
        try {
            remover.executar(id);
            return "redirect:/funcionarios";
        } catch (RuntimeException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/filtros")
    @ResponseBody
    public Map<String, java.util.List<String>> getFiltrosFuncionarios() throws java.sql.SQLException {
        Map<String, java.util.List<String>> filtros = new java.util.HashMap<>();
        java.util.List<String> cargos = new java.util.ArrayList<>();
        cargos.add("Diretor");
        cargos.add("Auxiliar");
        cargos.add("Estoquista");
        filtros.put("cargos", cargos);

        java.util.List<String> status = new java.util.ArrayList<>();
        try (java.sql.Connection conn = com.studiomuda.estoque.conexao.Conexao.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(
                     "SELECT DISTINCT CASE WHEN ativo=1 THEN 'ativo' ELSE 'inativo' END as status FROM funcionario");
             java.sql.ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) status.add(rs.getString("status"));
        }
        filtros.put("status", status);
        return filtros;
    }
}
