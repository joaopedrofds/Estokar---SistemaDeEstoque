package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.model.AgendamentoRemessa;
import com.studiomuda.estoque.model.ResultadoAgendamentoRemessa;
import com.studiomuda.estoque.service.RemessaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.time.LocalDate;

@Controller
@RequestMapping("/remessas")
public class RemessaController {
    private final RemessaService remessaService = new RemessaService();

    @GetMapping
    public String listar(Model model,
                         @RequestParam(required = false) String mensagem,
                         @RequestParam(required = false) String erro) {
        try {
            carregarDados(model);
            model.addAttribute("mensagem", mensagem);
            model.addAttribute("erro", erro);
            return "remessas/lista";
        } catch (Exception e) {
            return "redirect:/erro?mensagem=" + e.getMessage();
        }
    }

    @PostMapping("/agendar")
    public String agendar(@RequestParam int docaId,
                          @RequestParam int distribuidoraId,
                          @RequestParam String data,
                          @RequestParam String horario,
                          @RequestParam int volumePaletes,
                          Model model) {
        try {
            AgendamentoRemessa agendamento = new AgendamentoRemessa();
            agendamento.setDocaId(docaId);
            agendamento.setDistribuidoraId(distribuidoraId);
            agendamento.setData(Date.valueOf(data));
            agendamento.setHorario(horario);
            agendamento.setVolumePaletes(volumePaletes);

            ResultadoAgendamentoRemessa resultado = remessaService.agendar(agendamento);
            if (resultado.isSucesso()) {
                return "redirect:/remessas?mensagem=" + resultado.getMensagem();
            }

            carregarDados(model);
            model.addAttribute("erro", resultado.getMensagem());
            model.addAttribute("sugestoes", resultado.getSugestoes());
            model.addAttribute("agendamentoTentado", agendamento);
            return "remessas/lista";
        } catch (Exception e) {
            return "redirect:/remessas?erro=" + e.getMessage();
        }
    }

    @PostMapping("/docas")
    public String cadastrarDoca(@RequestParam String nome,
                                @RequestParam int capacidadePaletesDiaria) {
        try {
            remessaService.cadastrarDoca(nome, capacidadePaletesDiaria);
            return "redirect:/remessas?mensagem=Doca cadastrada com sucesso";
        } catch (Exception e) {
            return "redirect:/remessas?erro=" + e.getMessage();
        }
    }

    @PostMapping("/distribuidoras")
    public String cadastrarDistribuidora(@RequestParam String nome,
                                         @RequestParam String nivelPrioridade) {
        try {
            remessaService.cadastrarDistribuidora(nome, nivelPrioridade);
            return "redirect:/remessas?mensagem=Distribuidora cadastrada com sucesso";
        } catch (Exception e) {
            return "redirect:/remessas?erro=" + e.getMessage();
        }
    }

    @PostMapping("/excecoes")
    public String cadastrarExcecao(@RequestParam String data,
                                   @RequestParam String motivo) {
        try {
            remessaService.cadastrarExcecao(Date.valueOf(data), motivo);
            return "redirect:/remessas?mensagem=Excecao de calendario cadastrada";
        } catch (Exception e) {
            return "redirect:/remessas?erro=" + e.getMessage();
        }
    }

    private void carregarDados(Model model) throws Exception {
        model.addAttribute("docas", remessaService.listarDocasAtivas());
        model.addAttribute("distribuidoras", remessaService.listarDistribuidorasAtivas());
        model.addAttribute("agendamentos", remessaService.listarAgendamentos());
        model.addAttribute("dataHoje", LocalDate.now().toString());
    }
}
