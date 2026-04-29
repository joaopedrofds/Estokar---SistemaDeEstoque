package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.application.cliente.AtualizarClienteUseCase;
import com.studiomuda.estoque.application.cliente.BuscarClienteUseCase;
import com.studiomuda.estoque.application.cliente.CadastrarClienteUseCase;
import com.studiomuda.estoque.application.cliente.ListarClientesUseCase;
import com.studiomuda.estoque.application.cliente.RemoverClienteUseCase;
import com.studiomuda.estoque.application.cliente.dto.ClienteComFrequencia;
import com.studiomuda.estoque.domain.cliente.Cliente;
import com.studiomuda.estoque.presentation.web.cliente.ClienteForm;
import com.studiomuda.estoque.presentation.web.cliente.ClienteView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final CadastrarClienteUseCase cadastrar;
    private final AtualizarClienteUseCase atualizar;
    private final BuscarClienteUseCase buscar;
    private final ListarClientesUseCase listar;
    private final RemoverClienteUseCase remover;

    public ClienteController(CadastrarClienteUseCase cadastrar,
                             AtualizarClienteUseCase atualizar,
                             BuscarClienteUseCase buscar,
                             ListarClientesUseCase listar,
                             RemoverClienteUseCase remover) {
        this.cadastrar = cadastrar;
        this.atualizar = atualizar;
        this.buscar = buscar;
        this.listar = listar;
        this.remover = remover;
    }

    @GetMapping
    public String listarClientes(Model model,
                                 @RequestParam(required = false) String nome,
                                 @RequestParam(required = false) String tipo,
                                 @RequestParam(required = false) String status) {
        try {
            List<ClienteComFrequencia> clientes = (nome != null || tipo != null || status != null)
                    ? listar.buscarComFiltros(nome, tipo, status)
                    : listar.listarTodos();
            List<ClienteView> views = clientes.stream().map(ClienteView::desde).collect(Collectors.toList());
            model.addAttribute("clientes", views);
            model.addAttribute("filtroNome", nome);
            model.addAttribute("filtroTipo", tipo);
            model.addAttribute("filtroStatus", status);
            return "clientes/lista";
        } catch (RuntimeException e) {
            model.addAttribute("mensagemErro", "Erro ao listar clientes: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> listarClientesApi() {
        try {
            List<ClienteView> views = listar.listarTodos().stream()
                    .map(ClienteView::desde).collect(Collectors.toList());
            return ResponseEntity.ok(views);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao listar clientes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/novo")
    public String formNovoCliente(Model model) {
        model.addAttribute("cliente", new ClienteForm());
        return "clientes/form";
    }

    @PostMapping("/salvar")
    public String salvarCliente(@ModelAttribute("cliente") ClienteForm form, Model model) {
        try {
            if (form.getId() == 0) {
                cadastrar.executar(form.toCadastrarCommand());
            } else {
                atualizar.executar(form.toAtualizarCommand());
            }
            return "redirect:/clientes";
        } catch (IllegalArgumentException | com.studiomuda.estoque.domain.cliente.exceptions.ClienteJaExisteException e) {
            model.addAttribute("mensagemErro", e.getMessage());
            model.addAttribute("cliente", form);
            return "clientes/form";
        } catch (RuntimeException e) {
            model.addAttribute("mensagemErro", "Erro inesperado ao salvar cliente. Por favor, revise os dados e tente novamente.");
            model.addAttribute("cliente", form);
            return "clientes/form";
        }
    }

    @PostMapping("/api/salvar")
    @ResponseBody
    public ResponseEntity<?> salvarClienteApi(@RequestBody ClienteForm form) {
        try {
            if (form.getId() == 0) {
                Cliente salvo = cadastrar.executar(form.toCadastrarCommand());
                return ResponseEntity.ok(ClienteView.semFrequencia(salvo));
            } else {
                atualizar.executar(form.toAtualizarCommand());
                return ResponseEntity.ok().build();
            }
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao salvar cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable("id") int id, Model model) {
        Optional<Cliente> cliente = buscar.porId(id);
        if (cliente.isPresent()) {
            model.addAttribute("cliente", ClienteForm.desde(cliente.get()));
            return "clientes/form";
        }
        return "redirect:/clientes";
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarClienteApi(@PathVariable("id") int id) {
        return buscar.porId(id)
                .map(c -> ResponseEntity.ok((Object) ClienteView.semFrequencia(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/excluir/{id}")
    public String excluirCliente(@PathVariable("id") int id) {
        remover.executar(id);
        return "redirect:/clientes";
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirClienteApi(@PathVariable("id") int id) {
        try {
            remover.executar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Cliente excluído com sucesso");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao excluir cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/filtros")
    @ResponseBody
    public Map<String, java.util.List<String>> getFiltrosClientes() throws java.sql.SQLException {
        Map<String, java.util.List<String>> filtros = new HashMap<>();
        try (java.sql.Connection conn = com.studiomuda.estoque.conexao.Conexao.getConnection()) {
            java.util.List<String> tipos = new java.util.ArrayList<>();
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT tipo FROM cliente WHERE tipo IS NOT NULL AND tipo <> ''")) {
                java.sql.ResultSet rs = stmt.executeQuery();
                while (rs.next()) tipos.add(rs.getString("tipo"));
            }
            filtros.put("tipos", tipos);

            java.util.List<String> status = new java.util.ArrayList<>();
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT CASE WHEN ativo=1 THEN 'ativo' ELSE 'inativo' END as status FROM cliente")) {
                java.sql.ResultSet rs = stmt.executeQuery();
                while (rs.next()) status.add(rs.getString("status"));
            }
            filtros.put("status", status);
        }
        return filtros;
    }
}

@RestController
@RequestMapping("/api/clientes")
class ClienteApiController {
    private final ListarClientesUseCase listar;

    ClienteApiController(ListarClientesUseCase listar) {
        this.listar = listar;
    }

    @GetMapping("/count")
    public ResponseEntity<?> contarClientes() {
        try {
            int count = listar.listarTodos().size();
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao contar clientes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
