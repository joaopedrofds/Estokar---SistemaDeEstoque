package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.dao.ClienteDAO;
import com.studiomuda.estoque.model.Cliente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @GetMapping
    public String listarClientes(Model model,
                                @RequestParam(required = false) String nome,
                                @RequestParam(required = false) String tipo,
                                @RequestParam(required = false) String status) {
        try {
            List<Cliente> clientes;
            
            // Se há filtros, usar busca filtrada, senão listar todos
            if (nome != null || tipo != null || status != null) {
                clientes = clienteDAO.buscarComFiltros(nome, tipo, status);
            } else {
                clientes = clienteDAO.listar();
            }
            
            model.addAttribute("clientes", clientes);
            model.addAttribute("filtroNome", nome);
            model.addAttribute("filtroTipo", tipo);
            model.addAttribute("filtroStatus", status);
            return "clientes/lista";
        } catch (SQLException e) {
            model.addAttribute("mensagemErro", "Erro ao listar clientes: " + e.getMessage());
            return "erro";
        }
    }
    
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> listarClientesApi() {
        try {
            List<Cliente> clientes = clienteDAO.listar();
            return ResponseEntity.ok(clientes);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao listar clientes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/novo")
    public String formNovoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @PostMapping("/salvar")
    public String salvarCliente(@ModelAttribute Cliente cliente, Model model) {
        try {
            // Validação manual básica (pode ser expandida)
            if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
                model.addAttribute("mensagemErro", "O nome é obrigatório.");
                model.addAttribute("cliente", cliente);
                return "clientes/form";
            }
            if (cliente.getCpfCnpj() == null || cliente.getCpfCnpj().trim().isEmpty()) {
                model.addAttribute("mensagemErro", "O CPF/CNPJ é obrigatório.");
                model.addAttribute("cliente", cliente);
                return "clientes/form";
            }
            // Validação de tamanho de CPF/CNPJ ignorando máscara
            String cpfCnpjLimpo = cliente.getCpfCnpj().replaceAll("[^0-9]", "");
            String tipo = cliente.getTipo();
            if (tipo == null || tipo.trim().isEmpty()) tipo = "PF";
            if ("PF".equalsIgnoreCase(tipo)) {
                if (cpfCnpjLimpo.length() != 11) {
                    model.addAttribute("mensagemErro", "O CPF deve conter exatamente 11 dígitos. Valor informado: " + cpfCnpjLimpo);
                    model.addAttribute("cliente", cliente);
                    return "clientes/form";
                }
            } else if ("PJ".equalsIgnoreCase(tipo)) {
                if (cpfCnpjLimpo.length() != 14) {
                    model.addAttribute("mensagemErro", "O CNPJ deve conter exatamente 14 dígitos. Valor informado: " + cpfCnpjLimpo);
                    model.addAttribute("cliente", cliente);
                    return "clientes/form";
                }
            }
            // Atribui o valor limpo de volta ao objeto antes de salvar
            cliente.setCpfCnpj(cpfCnpjLimpo);
            if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
                model.addAttribute("mensagemErro", "O e-mail é obrigatório.");
                model.addAttribute("cliente", cliente);
                return "clientes/form";
            }
            // Exemplo de validação de formato de e-mail
            if (!cliente.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                model.addAttribute("mensagemErro", "E-mail em formato inválido.");
                model.addAttribute("cliente", cliente);
                return "clientes/form";
            }

            // Validação global de CPF/CNPJ (cliente e funcionário)
            com.studiomuda.estoque.dao.FuncionarioDAO funcionarioDAO = new com.studiomuda.estoque.dao.FuncionarioDAO();
            Cliente existenteCliente = clienteDAO.buscarPorCpfCnpj(cpfCnpjLimpo);
            com.studiomuda.estoque.model.Funcionario existenteFuncionario = funcionarioDAO.buscarPorCpf(cpfCnpjLimpo);
            boolean duplicado = false;
            if (cliente.getId() == 0) {
                // Cadastro novo
                if (existenteCliente != null || existenteFuncionario != null) {
                    duplicado = true;
                }
            } else {
                // Edição
                if ((existenteCliente != null && existenteCliente.getId() != cliente.getId()) || existenteFuncionario != null) {
                    duplicado = true;
                }
            }
            if (duplicado) {
                model.addAttribute("mensagemErro", "Já existe um cliente ou funcionário com esse CPF/CNPJ cadastrado.");
                model.addAttribute("cliente", cliente);
                return "clientes/form";
            }

            if (cliente.getId() == 0) {
                clienteDAO.inserir(cliente);
            } else {
                clienteDAO.atualizar(cliente);
            }
            return "redirect:/clientes";
        } catch (SQLException e) {
            String msg = e.getMessage();
            model.addAttribute("mensagemErro", "Erro ao salvar cliente: " + msg);
            model.addAttribute("cliente", cliente);
            return "clientes/form";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro inesperado ao salvar cliente. Por favor, revise os dados e tente novamente.");
            model.addAttribute("cliente", cliente);
            return "clientes/form";
        }
    }
    
    @PostMapping("/api/salvar")
    @ResponseBody
    public ResponseEntity<?> salvarClienteApi(@RequestBody Cliente cliente) {
        try {
            if (cliente.getId() == 0) {
                clienteDAO.inserir(cliente);
            } else {
                clienteDAO.atualizar(cliente);
            }
            return ResponseEntity.ok(cliente);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao salvar cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable("id") int id, Model model) {
        try {
            Cliente cliente = clienteDAO.buscarPorId(id);
            if (cliente != null) {
                model.addAttribute("cliente", cliente);
                return "clientes/form";
            } else {
                return "redirect:/clientes";
            }
        } catch (SQLException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }
    
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarClienteApi(@PathVariable("id") int id) {
        try {
            Cliente cliente = clienteDAO.buscarPorId(id);
            if (cliente != null) {
                return ResponseEntity.ok(cliente);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao buscar cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/excluir/{id}")
    public String excluirCliente(@PathVariable("id") int id) {
        try {
            clienteDAO.deletar(id);
            return "redirect:/clientes";
        } catch (SQLException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }
    
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirClienteApi(@PathVariable("id") int id) {
        try {
            clienteDAO.deletar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Cliente excluído com sucesso");
            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao excluir cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/filtros")
    @ResponseBody
    public Map<String, java.util.List<String>> getFiltrosClientes() throws SQLException {
        Map<String, java.util.List<String>> filtros = new HashMap<>();
        try (java.sql.Connection conn = com.studiomuda.estoque.conexao.Conexao.getConnection()) {
            // Tipos reais
            java.util.List<String> tipos = new java.util.ArrayList<>();
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT tipo FROM cliente WHERE tipo IS NOT NULL AND tipo <> ''")) {
                java.sql.ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tipos.add(rs.getString("tipo"));
                }
            }
            filtros.put("tipos", tipos);

            // Status reais
            java.util.List<String> status = new java.util.ArrayList<>();
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT CASE WHEN ativo=1 THEN 'ativo' ELSE 'inativo' END as status FROM cliente")) {
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

@RestController
@RequestMapping("/api/clientes")
class ClienteApiController {
    private final ClienteDAO clienteDAO = new ClienteDAO();

    @GetMapping("/count")
    public ResponseEntity<?> contarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listar();
            int count = clientes.size();
            return ResponseEntity.ok(count);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao contar clientes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
