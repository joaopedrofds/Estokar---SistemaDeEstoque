package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.model.Cliente;
import com.studiomuda.estoque.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listarClientes(Model model,
                                 @RequestParam(required = false) String nome,
                                 @RequestParam(required = false) String tipo,
                                 @RequestParam(required = false) String status) {
        try {
            List<Cliente> clientes = clienteService.listar(nome, tipo, status);
            model.addAttribute("clientes", clientes);
            model.addAttribute("filtroNome", nome);
            model.addAttribute("filtroTipo", tipo);
            model.addAttribute("filtroStatus", status);
            return "clientes/lista";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao listar clientes: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> listarClientesApi() {
        try {
            return ResponseEntity.ok(clienteService.listar(null, null, null));
        } catch (Exception e) {
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
            clienteService.salvar(cliente);
            return "redirect:/clientes";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensagemErro", e.getMessage());
            model.addAttribute("cliente", cliente);
            return "clientes/form";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao salvar cliente: " + e.getMessage());
            model.addAttribute("cliente", cliente);
            return "clientes/form";
        }
    }

    @PostMapping("/api/salvar")
    @ResponseBody
    public ResponseEntity<?> salvarClienteApi(@RequestBody Cliente cliente) {
        try {
            clienteService.salvar(cliente);
            return ResponseEntity.ok(cliente);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao salvar cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable("id") int id, Model model) {
        try {
            Cliente cliente = clienteService.buscarPorId(id);
            if (cliente != null) {
                model.addAttribute("cliente", cliente);
                return "clientes/form";
            }
            return "redirect:/clientes";
        } catch (Exception e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarClienteApi(@PathVariable("id") int id) {
        try {
            Cliente cliente = clienteService.buscarPorId(id);
            if (cliente != null) {
                return ResponseEntity.ok(cliente);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao buscar cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/excluir/{id}")
    public String excluirCliente(@PathVariable("id") int id) {
        try {
            clienteService.inativar(id);
            return "redirect:/clientes";
        } catch (Exception e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirClienteApi(@PathVariable("id") int id) {
        try {
            clienteService.inativar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Cliente excluído com sucesso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao excluir cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/filtros")
    @ResponseBody
    public Map<String, List<String>> getFiltrosClientes() {
        Map<String, List<String>> filtros = new HashMap<>();
        filtros.put("tipos", clienteService.listarTiposDisponiveis());
        filtros.put("status", clienteService.listarStatusDisponiveis());
        return filtros;
    }
}

@RestController
@RequestMapping("/api/clientes")
class ClienteApiController {
    private final ClienteService clienteService;

    ClienteApiController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/count")
    public ResponseEntity<?> contarClientes() {
        try {
            return ResponseEntity.ok(clienteService.contar());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao contar clientes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
