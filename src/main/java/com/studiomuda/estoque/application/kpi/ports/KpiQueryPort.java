package com.studiomuda.estoque.application.kpi.ports;

import java.util.Map;

public interface KpiQueryPort {
    Map<String, Integer> obterContadores();

    Map<String, Object> obterDadosDashboard();

    void recalcularContadores();
}
