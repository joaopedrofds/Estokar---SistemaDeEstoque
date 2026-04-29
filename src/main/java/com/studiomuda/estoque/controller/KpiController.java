package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.application.kpi.ConsultarKpiUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/kpis")
public class KpiController {
    private final ConsultarKpiUseCase consultarKpi;

    public KpiController(ConsultarKpiUseCase consultarKpi) {
        this.consultarKpi = consultarKpi;
    }

    @GetMapping("/contadores")
    public ResponseEntity<?> obterContadores() {
        try {
            return ResponseEntity.ok(consultarKpi.contadores());
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> obterDadosDashboard() {
        try {
            return ResponseEntity.ok(consultarKpi.dashboard());
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/recalcular")
    public ResponseEntity<?> recalcularContadores() {
        try {
            consultarKpi.recalcularContadores();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("mensagem", "Contadores recalculados com sucesso!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
