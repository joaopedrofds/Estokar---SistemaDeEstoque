package com.studiomuda.estoque.application.kpi;

import com.studiomuda.estoque.application.kpi.ports.KpiQueryPort;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConsultarKpiUseCase {
    private final KpiQueryPort kpiQuery;

    public ConsultarKpiUseCase(KpiQueryPort kpiQuery) {
        this.kpiQuery = kpiQuery;
    }

    public Map<String, Integer> contadores() {
        return kpiQuery.obterContadores();
    }

    public Map<String, Object> dashboard() {
        return kpiQuery.obterDadosDashboard();
    }

    public void recalcularContadores() {
        kpiQuery.recalcularContadores();
    }
}
