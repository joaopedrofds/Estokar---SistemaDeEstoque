package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.dao.FuncionarioDAO;
import com.studiomuda.estoque.model.Funcionario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

    @GetMapping
    public String listarFuncionarios(Model model) {
        try {
            model.addAttribute("funcionarios", funcionarioDAO.listar());
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
        // Inicializar com a data atual
        funcionario.setData_nasc(Date.valueOf(LocalDate.now()));
        model.addAttribute("funcionario", funcionario);
        return "funcionarios/form";
    }

    @PostMapping("/salvar")
    public String salvarFuncionario(@ModelAttribute Funcionario funcionario, @RequestParam(value = "dataNascimento", required = false) String dataNascimento, Model model) {
        try {
            // Validação manual básica
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
            // Validação de tamanho do CPF (11 dígitos sem máscara)
            String cpfLimpo = funcionario.getCpf().replaceAll("[^0-9]", "");
            if (cpfLimpo.length() != 11) {
                model.addAttribute("mensagemErro", "O CPF deve conter 11 dígitos.");
                model.addAttribute("funcionario", funcionario);
                return "funcionarios/form";
            }
            // Atribui o valor limpo de volta ao objeto antes de salvar
            funcionario.setCpf(cpfLimpo);
            if (funcionario.getCargo() == null || funcionario.getCargo().trim().isEmpty()) {
                model.addAttribute("mensagemErro", "O cargo é obrigatório.");
                model.addAttribute("funcionario", funcionario);
                return "funcionarios/form";
            }

            // Validação global de CPF (cliente e funcionário)
            com.studiomuda.estoque.dao.ClienteDAO clienteDAO = new com.studiomuda.estoque.dao.ClienteDAO();
            com.studiomuda.estoque.model.Cliente existenteCliente = clienteDAO.buscarPorCpfCnpj(cpfLimpo);
            Funcionario existenteFuncionario = funcionarioDAO.buscarPorCpf(cpfLimpo);
            boolean duplicado = false;
            if (funcionario.getId() == 0) {
                // Cadastro novo
                if (existenteFuncionario != null || existenteCliente != null) {
                    duplicado = true;
                }
            } else {
                // Edição
                if ((existenteFuncionario != null && existenteFuncionario.getId() != funcionario.getId()) || existenteCliente != null) {
                    duplicado = true;
                }
            }
            if (duplicado) {
                model.addAttribute("mensagemErro", "Já existe um cliente ou funcionário com esse CPF/CNPJ cadastrado.");
                model.addAttribute("funcionario", funcionario);
                return "funcionarios/form";
            }

            if (dataNascimento != null && !dataNascimento.isEmpty()) {
                funcionario.setData_nasc(Date.valueOf(dataNascimento));
            }
            if (funcionario.getId() == 0) {
                funcionarioDAO.inserir(funcionario);
            } else {
                funcionarioDAO.atualizar(funcionario);
            }
            return "redirect:/funcionarios";
        } catch (SQLException e) {
            String msg = e.getMessage();
            model.addAttribute("mensagemErro", "Erro ao salvar funcionário: " + msg);
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
            Funcionario funcionario = funcionarioDAO.buscarPorId(id);
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
            System.out.println("Tentando inativar funcionário ID: " + id);
            funcionarioDAO.deletar(id);
            System.out.println("Funcionário inativado com sucesso!");
            return "redirect:/funcionarios";
        } catch (SQLException e) {
            System.out.println("ERRO ao inativar funcionário: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/filtros")
    @ResponseBody
    public Map<String, java.util.List<String>> getFiltrosFuncionarios() throws java.sql.SQLException {
        Map<String, java.util.List<String>> filtros = new java.util.HashMap<>();
        try (java.sql.Connection conn = com.studiomuda.estoque.conexao.Conexao.getConnection()) {
            // Cargos reais - apenas os que realmente existem no banco
            java.util.List<String> cargos = new java.util.ArrayList<>();
            
            // Usando uma lista fixa dos cargos que realmente existem
            cargos.add("Diretor");
            cargos.add("Auxiliar");
            cargos.add("Estoquista");
            
            filtros.put("cargos", cargos);

            // Status reais
            java.util.List<String> status = new java.util.ArrayList<>();
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT CASE WHEN ativo=1 THEN 'ativo' ELSE 'inativo' END as status FROM funcionario")) {
                java.sql.ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    status.add(rs.getString("status"));
                }
            }
            filtros.put("status", status);
        }
        return filtros;
    }
}
