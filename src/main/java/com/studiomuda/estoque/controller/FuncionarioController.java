package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.model.Funcionario;
import com.studiomuda.estoque.service.FuncionarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    public String listarFuncionarios(Model model) {
        try {
            model.addAttribute("funcionarios", funcionarioService.listar());
            return "funcionarios/lista";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao listar funcionários: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/novo")
    public String formNovoFuncionario(Model model) {
        Funcionario funcionario = new Funcionario();
        funcionario.setAtivo(true);
        funcionario.setData_nasc(Date.valueOf(LocalDate.now()));
        model.addAttribute("funcionario", funcionario);
        return "funcionarios/form";
    }

    @PostMapping("/salvar")
    public String salvarFuncionario(
            @ModelAttribute Funcionario funcionario,
            @RequestParam(value = "dataNascimento", required = false) String dataNascimento,
            Model model) {
        try {
            if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
                model.addAttribute("mensagemErro", "O nome é obrigatório.");
                model.addAttribute("funcionario", funcionario);
                return "funcionarios/form";
            }
            if (funcionario.getCpf() == null || funcionario.getCpf().trim().isEmpty()) {
                model.addAttribute("mensagemErro", "O CPF é obrigatório.");
                model.addAttribute("funcionario", funcionario);
                return "funcionarios/form";
            }
            String cpfLimpo = funcionario.getCpf().replaceAll("[^0-9]", "");
            if (cpfLimpo.length() != 11) {
                model.addAttribute("mensagemErro", "O CPF deve conter 11 dígitos.");
                model.addAttribute("funcionario", funcionario);
                return "funcionarios/form";
            }
            funcionario.setCpf(cpfLimpo);
            if (funcionario.getCargo() == null || funcionario.getCargo().trim().isEmpty()) {
                model.addAttribute("mensagemErro", "O cargo é obrigatório.");
                model.addAttribute("funcionario", funcionario);
                return "funcionarios/form";
            }

            if (funcionarioService.cpfDuplicado(cpfLimpo, funcionario.getId())) {
                model.addAttribute("mensagemErro", "Já existe um cliente ou funcionário com esse CPF/CNPJ cadastrado.");
                model.addAttribute("funcionario", funcionario);
                return "funcionarios/form";
            }

            if (dataNascimento != null && !dataNascimento.isEmpty()) {
                funcionario.setData_nasc(Date.valueOf(dataNascimento));
            }
            if (funcionario.getId() == 0) {
                funcionarioService.inserir(funcionario);
            } else {
                funcionarioService.atualizar(funcionario);
            }
            return "redirect:/funcionarios";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao salvar funcionário: " + e.getMessage());
            model.addAttribute("funcionario", funcionario);
            return "funcionarios/form";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro inesperado ao salvar funcionário. Por favor, revise os dados e tente novamente.");
            model.addAttribute("funcionario", funcionario);
            return "funcionarios/form";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarFuncionario(@PathVariable("id") int id, Model model) {
        try {
            Funcionario funcionario = funcionarioService.buscarPorId(id);
            if (funcionario != null) {
                model.addAttribute("funcionario", funcionario);
                return "funcionarios/form";
            } else {
                return "redirect:/funcionarios";
            }
        } catch (SQLException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/excluir/{id}")
    public String excluirFuncionario(@PathVariable("id") int id) {
        try {
            funcionarioService.inativar(id);
            return "redirect:/funcionarios";
        } catch (SQLException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/filtros")
    @ResponseBody
    public Map<String, List<String>> getFiltrosFuncionarios() throws SQLException {
        return funcionarioService.getFiltros();
    }
}
