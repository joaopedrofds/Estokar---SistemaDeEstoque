package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.dao.ProdutoDAO;
import com.studiomuda.estoque.model.Produto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @GetMapping
    public String listarProdutos(Model model,
                                @RequestParam(required = false) String nome,
                                @RequestParam(required = false) String tipo,
                                @RequestParam(required = false) String estoque) {
        try {
            List<Produto> produtos;
            
            // Se há filtros, usar busca filtrada, senão listar todos
            if (nome != null || tipo != null || estoque != null) {
                produtos = produtoDAO.buscarComFiltros(nome, tipo, estoque);
            } else {
                produtos = produtoDAO.listar();
            }
            
            model.addAttribute("produtos", produtos);
            model.addAttribute("filtroNome", nome);
            model.addAttribute("filtroTipo", tipo);
            model.addAttribute("filtroEstoque", estoque);
            return "produtos/lista";
        } catch (SQLException e) {
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
    public String salvarProduto(@ModelAttribute Produto produto) {
        try {
            if (produto.getId() == 0) {
                produtoDAO.inserir(produto);
            } else {
                produtoDAO.atualizar(produto);
            }
            return "redirect:/produtos";
        } catch (SQLException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable("id") int id, Model model) {
        try {
            Produto produto = produtoDAO.buscarPorId(id);
            if (produto != null) {
                model.addAttribute("produto", produto);
                return "produtos/form";
            } else {
                return "redirect:/produtos";
            }
        } catch (SQLException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/excluir/{id}")
    public String excluirProduto(@PathVariable("id") int id) {
        try {
            produtoDAO.deletar(id);
            return "redirect:/produtos";
        } catch (SQLException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> listarProdutosApi() {
        try {
            List<Produto> produtos = produtoDAO.listar();
            return ResponseEntity.ok(produtos);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao listar produtos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarProdutoApi(@PathVariable("id") int id) {
        try {
            Produto produto = produtoDAO.buscarPorId(id);
            if (produto != null) {
                return ResponseEntity.ok(produto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao buscar produto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/filtros")
    @ResponseBody
    public Map<String, List<String>> getFiltrosDisponiveis() throws SQLException {
        Map<String, List<String>> filtros = new HashMap<>();
        try (Connection conn = com.studiomuda.estoque.conexao.Conexao.getConnection()) {
            // Tipos
            List<String> tipos = new java.util.ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT tipo FROM produto WHERE tipo IS NOT NULL AND tipo <> ''")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tipos.add(rs.getString("tipo"));
                }
            }
            filtros.put("tipos", tipos);

            // Status de estoque calculado
            List<String> statusEstoque = new java.util.ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT CASE WHEN quantidade = 0 THEN 'zerado' WHEN quantidade <= 5 THEN 'baixo' ELSE 'disponivel' END as status_estoque FROM produto")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    statusEstoque.add(rs.getString("status_estoque"));
                }
            }
            filtros.put("estoques", statusEstoque);
        }
        return filtros;
    }
}

@RestController
@RequestMapping("/api/produtos")
class ProdutoApiController {
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @GetMapping("/count")
    public ResponseEntity<?> contarProdutos() {
        try {
            List<Produto> produtos = produtoDAO.listar();
            int count = produtos.size();
            return ResponseEntity.ok(count);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao contar produtos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
