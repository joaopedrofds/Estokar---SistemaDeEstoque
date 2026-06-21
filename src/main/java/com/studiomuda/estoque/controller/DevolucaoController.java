package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.model.Devolucao;
import com.studiomuda.estoque.model.ItemDevolucao;
import com.studiomuda.estoque.model.Pedido;
import com.studiomuda.estoque.model.ItemPedido;
import com.studiomuda.estoque.service.DevolucaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller da camada de apresentação para devoluções.
 * Camada: Apresentação (Arquitetura Limpa)
 */
@Controller
@RequestMapping("/devolucoes")
public class DevolucaoController {

    @Autowired
    private DevolucaoService devolucaoService;

    @GetMapping
    public String listar(@RequestParam(required = false) String status, Model model) {
        model.addAttribute("devolucoes", devolucaoService.listarComNomeCliente(status));
        model.addAttribute("statusFiltro", status);
        return "devolucoes/lista";
    }

    @GetMapping("/nova/{pedidoId}")
    public String formNova(@PathVariable int pedidoId, Model model) {
        try {
            Pedido pedido = devolucaoService.buscarPedidoParaDevolucao(pedidoId);
            if (pedido == null) return "redirect:/pedidos";
            List<ItemPedido> itens = devolucaoService.listarItensPedidoParaDevolucao(pedidoId);
            model.addAttribute("pedido", pedido);
            model.addAttribute("itensPedido", itens);
            model.addAttribute("devolucao", new Devolucao());
            return "devolucoes/form";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro: " + e.getMessage());
            return "erro";
        }
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam int pedidoId,
                         @RequestParam String motivo,
                         @RequestParam String tipoRestituicao,
                         @RequestParam(required = false) List<Integer> produtoIds,
                         @RequestParam(required = false) List<Integer> quantidades,
                         @RequestParam(required = false) List<Double> valoresUnitarios,
                         @RequestParam(required = false) List<String> condicoes,
                         Model model) {
        try {
            Pedido pedido = devolucaoService.buscarPedidoParaDevolucao(pedidoId);
            if (pedido == null) return "redirect:/pedidos";

            Devolucao devolucao = new Devolucao();
            devolucao.setPedidoId(pedidoId);
            devolucao.setClienteId(pedido.getClienteId());
            devolucao.setMotivo(motivo);
            devolucao.setTipoRestituicao(tipoRestituicao);

            List<ItemDevolucao> itens = new ArrayList<>();
            if (produtoIds != null) {
                for (int i = 0; i < produtoIds.size(); i++) {
                    if (quantidades.get(i) != null && quantidades.get(i) > 0) {
                        ItemDevolucao item = new ItemDevolucao();
                        item.setProdutoId(produtoIds.get(i));
                        item.setQuantidade(quantidades.get(i));
                        item.setValorUnitario(valoresUnitarios != null ? valoresUnitarios.get(i) : 0);
                        item.setCondicao(condicoes != null ? condicoes.get(i) : "BOM");
                        itens.add(item);
                    }
                }
            }

            if (itens.isEmpty()) {
                model.addAttribute("mensagemErro", "Selecione ao menos um item para devolver.");
                return formNova(pedidoId, model);
            }

            devolucaoService.solicitar(devolucao, itens);
            return "redirect:/devolucoes/" + devolucao.getId();
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao registrar devolução: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable int id, Model model) {
        Devolucao devolucao = devolucaoService.buscarPorIdComDetalhes(id);
        if (devolucao == null) return "redirect:/devolucoes";

        try {
            Pedido pedido = devolucaoService.buscarPedidoParaDevolucao(devolucao.getPedidoId());
            model.addAttribute("devolucao", devolucao);
            model.addAttribute("pedido", pedido);
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro: " + e.getMessage());
            return "erro";
        }

        return "devolucoes/detalhe";
    }

    @PostMapping("/{id}/aprovar")
    public String aprovar(@PathVariable int id,
                          @RequestParam(required = false) String observacao,
                          Model model) {
        try {
            devolucaoService.aprovar(id, observacao);
            System.out.println("[DevolucaoController] Devolução #" + id + " aprovada com sucesso.");
            return "redirect:/devolucoes/" + id + "?sucesso=aprovada";
        } catch (Exception e) {
            System.err.println("[DevolucaoController] Erro ao aprovar: " + e.getMessage());
            model.addAttribute("mensagemErro", e.getMessage());
            return detalhe(id, model);
        }
    }

    @PostMapping("/{id}/rejeitar")
    public String rejeitar(@PathVariable int id,
                           @RequestParam(required = false) String observacao,
                           Model model) {
        try {
            devolucaoService.rejeitar(id, observacao);
            return "redirect:/devolucoes/" + id + "?sucesso=rejeitada";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", e.getMessage());
            return detalhe(id, model);
        }
    }

    @GetMapping("/creditos")
    public String creditos(Model model) {
        model.addAttribute("creditos", devolucaoService.listarCreditosAtivos());
        return "devolucoes/creditos";
    }
}
