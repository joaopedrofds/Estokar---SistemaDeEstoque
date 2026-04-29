package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.application.estoque.ExcluirMovimentacaoUseCase;
import com.studiomuda.estoque.application.estoque.ListarMovimentacoesUseCase;
import com.studiomuda.estoque.application.estoque.RegistrarMovimentacaoUseCase;
import com.studiomuda.estoque.application.produto.ListarProdutosUseCase;
import com.studiomuda.estoque.presentation.web.estoque.MovimentacaoForm;
import com.studiomuda.estoque.presentation.web.estoque.MovimentacaoView;
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
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {

    private final RegistrarMovimentacaoUseCase registrar;
    private final ExcluirMovimentacaoUseCase excluir;
    private final ListarMovimentacoesUseCase listar;
    private final ListarProdutosUseCase listarProdutos;

    public EstoqueController(RegistrarMovimentacaoUseCase registrar,
                             ExcluirMovimentacaoUseCase excluir,
                             ListarMovimentacoesUseCase listar,
                             ListarProdutosUseCase listarProdutos) {
        this.registrar = registrar;
        this.excluir = excluir;
        this.listar = listar;
        this.listarProdutos = listarProdutos;
    }

    @GetMapping
    public String listarMovimentacoes(Model model,
                                       @RequestParam(required = false) String produto,
                                       @RequestParam(required = false) String tipo,
                                       @RequestParam(required = false) String dataInicio,
                                       @RequestParam(required = false) String dataFim) {
        try {
            List<MovimentacaoView> views = (produto != null || tipo != null || dataInicio != null || dataFim != null)
                    ? listar.buscarComFiltros(produto, tipo, dataInicio, dataFim).stream()
                            .map(MovimentacaoView::desde).collect(Collectors.toList())
                    : listar.listarTodas().stream().map(MovimentacaoView::desde).collect(Collectors.toList());
            model.addAttribute("movimentacoes", views);
            model.addAttribute("filtroProduto", produto);
            model.addAttribute("filtroTipo", tipo);
            model.addAttribute("filtroDataInicio", dataInicio);
            model.addAttribute("filtroDataFim", dataFim);
            return "estoque/lista";
        } catch (RuntimeException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/nova")
    public String formNovaMovimentacao(Model model) {
        try {
            MovimentacaoForm form = new MovimentacaoForm();
            form.setData(LocalDate.now());
            form.setTipo("entrada");
            model.addAttribute("movimentacao", form);
            List<ProdutoView> produtos = listarProdutos.listarTodos().stream()
                    .map(ProdutoView::new).collect(Collectors.toList());
            model.addAttribute("produtos", produtos);
            return "estoque/form";
        } catch (RuntimeException e) {
            model.addAttribute("mensagemErro", "Erro ao preparar formulário: " + e.getMessage());
            return "erro";
        }
    }

    @PostMapping("/salvar")
    public String salvarMovimentacao(@ModelAttribute("movimentacao") MovimentacaoForm form,
                                      @RequestParam(value = "dataStr", required = false) String dataStr) {
        try {
            LocalDate data = (dataStr != null && !dataStr.isEmpty()) ? LocalDate.parse(dataStr) : LocalDate.now();
            registrar.executar(form.toCommand(data));
            return "redirect:/estoque";
        } catch (RuntimeException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/excluir/{id}")
    public String excluirMovimentacao(@PathVariable("id") int id) {
        try {
            excluir.executar(id);
            return "redirect:/estoque";
        } catch (RuntimeException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }
}

@RestController
@RequestMapping("/api/estoque")
class EstoqueApiController {
    private final ListarMovimentacoesUseCase listar;

    EstoqueApiController(ListarMovimentacoesUseCase listar) {
        this.listar = listar;
    }

    @GetMapping("/count")
    public ResponseEntity<?> contarMovimentacoes() {
        try {
            int count = listar.listarTodas().size();
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao contar movimentações: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
