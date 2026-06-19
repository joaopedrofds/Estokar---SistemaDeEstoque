package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.model.Cupom;
import com.studiomuda.estoque.service.CupomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cupons")
public class CupomController {

    private final CupomService cupomService;

    public CupomController(CupomService cupomService) {
        this.cupomService = cupomService;
    }

    @GetMapping
    public String listarCupons(Model model) {
        try {
            model.addAttribute("cupons", cupomService.listarTodos());
            return "cupons/lista";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao listar cupons: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> listarCuponsApi() {
        try {
            List<Cupom> cupons = cupomService.listarTodos();
            return ResponseEntity.ok(cupons);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao listar cupons: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/novo")
    public String formNovoCupom(Model model) {
        Cupom cupom = new Cupom();
        cupom.setDataInicio(LocalDate.now());
        cupom.setValidade(LocalDate.now().plusMonths(1));
        model.addAttribute("cupom", cupom);
        return "cupons/form";
    }

    @PostMapping("/salvar")
    public String salvarCupom(
            @ModelAttribute Cupom cupom,
            @RequestParam("dataInicioStr") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicio,
            @RequestParam("validadeStr") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate validade) {
        try {
            cupom.setDataInicio(dataInicio);
            cupom.setValidade(validade);

            cupomService.salvar(cupom);
            return "redirect:/cupons";
        } catch (Exception e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @PostMapping("/api/salvar")
    @ResponseBody
    public ResponseEntity<?> salvarCupomApi(@RequestBody Cupom cupom) {
        try {
            cupomService.salvar(cupom);
            return ResponseEntity.ok(cupom);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao salvar cupom: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/editar/{id}")
    public String editarCupom(@PathVariable("id") int id, Model model) {
        try {
            Cupom cupom = cupomService.buscarPorId(id).orElse(null);
            if (cupom != null) {
                model.addAttribute("cupom", cupom);
                return "cupons/form";
            } else {
                return "redirect:/cupons";
            }
        } catch (Exception e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarCupomApi(@PathVariable("id") int id) {
        try {
            Cupom cupom = cupomService.buscarPorId(id).orElse(null);
            if (cupom != null) {
                return ResponseEntity.ok(cupom);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao buscar cupom: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/api/codigo/{codigo}")
    @ResponseBody
    public ResponseEntity<?> buscarCupomPorCodigoApi(@PathVariable("codigo") String codigo) {
        try {
            Cupom cupom = cupomService.listarTodos().stream()
                .filter(c -> c.getCodigo().equals(codigo))
                .findFirst().orElse(null);
            if (cupom != null) {
                return ResponseEntity.ok(cupom);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao buscar cupom: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/excluir/{id}")
    public String excluirCupom(@PathVariable("id") int id) {
        try {
            cupomService.deletar(id);
            return "redirect:/cupons";
        } catch (Exception e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirCupomApi(@PathVariable("id") int id) {
        try {
            cupomService.deletar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Cupom excluído com sucesso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao excluir cupom: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/validos")
    public String listarCuponsValidos(Model model) {
        try {
            model.addAttribute("cupons", cupomService.listarAtivos());
            model.addAttribute("apenasValidos", true);
            return "cupons/lista";
        } catch (Exception e) {
            model.addAttribute("mensagemErro", "Erro ao listar cupons válidos: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/api/validos")
    @ResponseBody
    public ResponseEntity<?> listarCuponsValidosApi() {
        try {
            List<Cupom> cupons = cupomService.listarAtivos();
            return ResponseEntity.ok(cupons);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao listar cupons válidos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/filtros")
    @ResponseBody
    public Map<String, java.util.List<String>> getFiltrosCupons() {
        Map<String, java.util.List<String>> filtros = new HashMap<>();
        // Status disponíveis baseados nos dados
        java.util.List<String> status = new java.util.ArrayList<>();
        status.add("ATIVO");
        status.add("INATIVO");
        status.add("ESGOTADO");
        status.add("EXPIRADO");
        filtros.put("status", status);
        return filtros;
    }
}