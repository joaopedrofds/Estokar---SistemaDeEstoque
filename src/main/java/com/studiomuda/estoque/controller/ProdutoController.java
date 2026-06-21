package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.model.Produto;
import com.studiomuda.estoque.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public String listarProdutos(Model model,
                                @RequestParam(required = false) String nome,
                                @RequestParam(required = false) String tipo,
                                @RequestParam(required = false) String estoque) {
        try {
            List<Produto> produtos = produtoService.listar();
            model.addAttribute("produtos", produtos);
            model.addAttribute("filtroNome", nome);
            model.addAttribute("filtroTipo", tipo);
            model.addAttribute("filtroEstoque", estoque);
            return "produtos/lista";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao listar produtos: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/novo")
    public String formNovoProduto(Model model) {
        model.addAttribute("produto", new Produto());
        return "produtos/form";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute Produto produto, Authentication authentication) {
        try {
            String usuario = (authentication != null) ? authentication.getName() : "sistema";

            if (produto.getId() == 0) {
                produtoService.salvar(produto);
            } else {
                produtoService.atualizar(produto, usuario);
            }
            return "redirect:/produtos";
        } catch (Exception e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable("id") int id, Model model) {
        try {
            Optional<Produto> optProduto = produtoService.buscarPorId(id);
            if (optProduto.isPresent()) {
                model.addAttribute("produto", optProduto.get());
                return "produtos/form";
            } else {
                return "redirect:/produtos";
            }
        } catch (Exception e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/excluir/{id}")
    public String excluirProduto(@PathVariable("id") int id) {
        try {
            produtoService.deletar(id);
            return "redirect:/produtos";
        } catch (Exception e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> listarProdutosApi() {
        try {
            List<Produto> produtos = produtoService.listar();
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao listar produtos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarProdutoApi(@PathVariable("id") int id) {
        try {
            Optional<Produto> optProduto = produtoService.buscarPorId(id);
            if (optProduto.isPresent()) {
                return ResponseEntity.ok(optProduto.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao buscar produto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

@RestController
@RequestMapping("/api/produtos")
class ProdutoApiController {
    private final ProdutoService produtoService;

    public ProdutoApiController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/count")
    public ResponseEntity<?> contarProdutos() {
        try {
            List<Produto> produtos = produtoService.listar();
            int count = produtos.size();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao contar produtos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
