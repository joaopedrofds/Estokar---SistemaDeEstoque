package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.application.produto.AtualizarProdutoUseCase;
import com.studiomuda.estoque.application.produto.BuscarProdutoUseCase;
import com.studiomuda.estoque.application.produto.CadastrarProdutoUseCase;
import com.studiomuda.estoque.application.produto.ListarProdutosUseCase;
import com.studiomuda.estoque.application.produto.RemoverProdutoUseCase;
import com.studiomuda.estoque.domain.produto.Produto;
import com.studiomuda.estoque.presentation.web.produto.ProdutoForm;
import com.studiomuda.estoque.presentation.web.produto.ProdutoView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final CadastrarProdutoUseCase cadastrar;
    private final AtualizarProdutoUseCase atualizar;
    private final BuscarProdutoUseCase buscar;
    private final ListarProdutosUseCase listar;
    private final RemoverProdutoUseCase remover;

    public ProdutoController(CadastrarProdutoUseCase cadastrar,
                             AtualizarProdutoUseCase atualizar,
                             BuscarProdutoUseCase buscar,
                             ListarProdutosUseCase listar,
                             RemoverProdutoUseCase remover) {
        this.cadastrar = cadastrar;
        this.atualizar = atualizar;
        this.buscar = buscar;
        this.listar = listar;
        this.remover = remover;
    }

    @GetMapping
    public String listarProdutos(Model model,
                                 @RequestParam(required = false) String nome,
                                 @RequestParam(required = false) String tipo,
                                 @RequestParam(required = false) String estoque) {
        try {
            List<Produto> produtos = (nome != null || tipo != null || estoque != null)
                    ? listar.buscarComFiltros(nome, tipo, estoque)
                    : listar.listarTodos();
            List<ProdutoView> views = produtos.stream().map(ProdutoView::new).collect(Collectors.toList());
            model.addAttribute("produtos", views);
            model.addAttribute("filtroNome", nome);
            model.addAttribute("filtroTipo", tipo);
            model.addAttribute("filtroEstoque", estoque);
            return "produtos/lista";
        } catch (RuntimeException e) {
            model.addAttribute("mensagemErro", "Erro ao listar produtos: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/novo")
    public String formNovoProduto(Model model) {
        model.addAttribute("produto", new ProdutoForm());
        return "produtos/form";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute("produto") ProdutoForm form) {
        try {
            if (form.getId() == 0) {
                cadastrar.executar(form.toCommand());
            } else {
                atualizar.executar(form.toCommand());
            }
            return "redirect:/produtos";
        } catch (RuntimeException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable("id") int id, Model model) {
        Optional<Produto> produto = buscar.porId(id);
        if (produto.isPresent()) {
            model.addAttribute("produto", ProdutoForm.desde(produto.get()));
            return "produtos/form";
        }
        return "redirect:/produtos";
    }

    @GetMapping("/excluir/{id}")
    public String excluirProduto(@PathVariable("id") int id) {
        try {
            remover.executar(id);
            return "redirect:/produtos";
        } catch (RuntimeException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> listarProdutosApi() {
        try {
            List<ProdutoView> views = listar.listarTodos().stream()
                    .map(ProdutoView::new).collect(Collectors.toList());
            return ResponseEntity.ok(views);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao listar produtos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarProdutoApi(@PathVariable("id") int id) {
        return buscar.porId(id)
                .map(p -> ResponseEntity.ok((Object) new ProdutoView(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/filtros")
    @ResponseBody
    public Map<String, List<String>> getFiltrosDisponiveis() throws SQLException {
        Map<String, List<String>> filtros = new HashMap<>();
        try (Connection conn = com.studiomuda.estoque.conexao.Conexao.getConnection()) {
            List<String> tipos = new java.util.ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT tipo FROM produto WHERE tipo IS NOT NULL AND tipo <> ''");
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) tipos.add(rs.getString("tipo"));
            }
            filtros.put("tipos", tipos);

            List<String> statusEstoque = new java.util.ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT DISTINCT CASE WHEN quantidade = 0 THEN 'zerado' WHEN quantidade <= 5 THEN 'baixo' ELSE 'disponivel' END as status_estoque FROM produto");
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) statusEstoque.add(rs.getString("status_estoque"));
            }
            filtros.put("estoques", statusEstoque);
        }
        return filtros;
    }
}

@RestController
@RequestMapping("/api/produtos")
class ProdutoApiController {
    private final ListarProdutosUseCase listar;

    ProdutoApiController(ListarProdutosUseCase listar) {
        this.listar = listar;
    }

    @GetMapping("/count")
    public ResponseEntity<?> contarProdutos() {
        try {
            int count = listar.listarTodos().size();
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao contar produtos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
