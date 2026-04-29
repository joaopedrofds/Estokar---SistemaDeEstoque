package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.application.cupom.AtualizarCupomUseCase;
import com.studiomuda.estoque.application.cupom.BuscarCupomUseCase;
import com.studiomuda.estoque.application.cupom.CadastrarCupomUseCase;
import com.studiomuda.estoque.application.cupom.ListarCuponsUseCase;
import com.studiomuda.estoque.application.cupom.RemoverCupomUseCase;
import com.studiomuda.estoque.domain.cupom.Cupom;
import com.studiomuda.estoque.presentation.web.cupom.CupomForm;
import com.studiomuda.estoque.presentation.web.cupom.CupomView;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cupons")
public class CupomController {

    private final CadastrarCupomUseCase cadastrar;
    private final AtualizarCupomUseCase atualizar;
    private final BuscarCupomUseCase buscar;
    private final ListarCuponsUseCase listar;
    private final RemoverCupomUseCase remover;

    public CupomController(CadastrarCupomUseCase cadastrar,
                           AtualizarCupomUseCase atualizar,
                           BuscarCupomUseCase buscar,
                           ListarCuponsUseCase listar,
                           RemoverCupomUseCase remover) {
        this.cadastrar = cadastrar;
        this.atualizar = atualizar;
        this.buscar = buscar;
        this.listar = listar;
        this.remover = remover;
    }

    @GetMapping
    public String listarCupons(Model model) {
        try {
            List<CupomView> views = listar.listarTodos().stream()
                    .map(CupomView::new).collect(Collectors.toList());
            model.addAttribute("cupons", views);
            return "cupons/lista";
        } catch (RuntimeException e) {
            model.addAttribute("mensagemErro", "Erro ao listar cupons: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> listarCuponsApi() {
        try {
            List<CupomView> views = listar.listarTodos().stream()
                    .map(CupomView::new).collect(Collectors.toList());
            return ResponseEntity.ok(views);
        } catch (RuntimeException e) {
            return erro("Erro ao listar cupons: " + e.getMessage());
        }
    }

    @GetMapping("/novo")
    public String formNovoCupom(Model model) {
        CupomForm f = new CupomForm();
        f.setDataInicio(LocalDate.now());
        f.setValidade(LocalDate.now().plusMonths(1));
        model.addAttribute("cupom", f);
        return "cupons/form";
    }

    @PostMapping("/salvar")
    public String salvarCupom(
            @ModelAttribute("cupom") CupomForm form,
            @RequestParam("dataInicioStr") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicio,
            @RequestParam("validadeStr") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate validade) {
        try {
            if (form.getId() == 0) {
                cadastrar.executar(form.toCommand(dataInicio, validade));
            } else {
                atualizar.executar(form.toCommand(dataInicio, validade));
            }
            return "redirect:/cupons";
        } catch (RuntimeException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @PostMapping("/api/salvar")
    @ResponseBody
    public ResponseEntity<?> salvarCupomApi(@RequestBody CupomForm form) {
        try {
            if (form.getId() == 0) {
                Cupom salvo = cadastrar.executar(form.toCommand(null, null));
                return ResponseEntity.ok(new CupomView(salvo));
            } else {
                atualizar.executar(form.toCommand(null, null));
                return ResponseEntity.ok().build();
            }
        } catch (RuntimeException e) {
            return erro("Erro ao salvar cupom: " + e.getMessage());
        }
    }

    @GetMapping("/editar/{id}")
    public String editarCupom(@PathVariable("id") int id, Model model) {
        Optional<Cupom> cupom = buscar.porId(id);
        if (cupom.isPresent()) {
            model.addAttribute("cupom", CupomForm.desde(cupom.get()));
            return "cupons/form";
        }
        return "redirect:/cupons";
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarCupomApi(@PathVariable("id") int id) {
        return buscar.porId(id)
                .map(c -> ResponseEntity.ok((Object) new CupomView(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/codigo/{codigo}")
    @ResponseBody
    public ResponseEntity<?> buscarCupomPorCodigoApi(@PathVariable("codigo") String codigo) {
        return buscar.porCodigo(codigo)
                .map(c -> ResponseEntity.ok((Object) new CupomView(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/excluir/{id}")
    public String excluirCupom(@PathVariable("id") int id) {
        try {
            remover.executar(id);
            return "redirect:/cupons";
        } catch (RuntimeException e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirCupomApi(@PathVariable("id") int id) {
        try {
            remover.executar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Cupom excluído com sucesso");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return erro("Erro ao excluir cupom: " + e.getMessage());
        }
    }

    @GetMapping("/validos")
    public String listarCuponsValidos(Model model) {
        try {
            List<CupomView> views = listar.listarValidos().stream()
                    .map(CupomView::new).collect(Collectors.toList());
            model.addAttribute("cupons", views);
            model.addAttribute("apenasValidos", true);
            return "cupons/lista";
        } catch (RuntimeException e) {
            model.addAttribute("mensagemErro", "Erro ao listar cupons válidos: " + e.getMessage());
            return "erro";
        }
    }

    @GetMapping("/api/validos")
    @ResponseBody
    public ResponseEntity<?> listarCuponsValidosApi() {
        try {
            List<CupomView> views = listar.listarValidos().stream()
                    .map(CupomView::new).collect(Collectors.toList());
            return ResponseEntity.ok(views);
        } catch (RuntimeException e) {
            return erro("Erro ao listar cupons válidos: " + e.getMessage());
        }
    }

    @GetMapping("/filtros")
    @ResponseBody
    public Map<String, java.util.List<String>> getFiltrosCupons() throws java.sql.SQLException {
        Map<String, java.util.List<String>> filtros = new java.util.HashMap<>();
        try (java.sql.Connection conn = com.studiomuda.estoque.conexao.Conexao.getConnection()) {
            java.util.List<String> status = new java.util.ArrayList<>();
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT status FROM cupom WHERE status IS NOT NULL AND status <> ''");
                 java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) status.add(rs.getString("status"));
            }
            filtros.put("status", status);
        }
        return filtros;
    }

    private ResponseEntity<?> erro(String mensagem) {
        Map<String, String> e = new HashMap<>();
        e.put("erro", mensagem);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
    }
}
