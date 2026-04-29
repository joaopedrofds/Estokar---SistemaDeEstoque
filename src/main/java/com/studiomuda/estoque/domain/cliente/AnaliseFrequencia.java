package com.studiomuda.estoque.domain.cliente;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class AnaliseFrequencia {
    private final int totalPedidos;
    private final Double mediaDiasEntreCompras;
    private final ClassificacaoFrequencia classificacao;

    private AnaliseFrequencia(int totalPedidos, Double mediaDiasEntreCompras, ClassificacaoFrequencia classificacao) {
        this.totalPedidos = totalPedidos;
        this.mediaDiasEntreCompras = mediaDiasEntreCompras;
        this.classificacao = classificacao;
    }

    public static AnaliseFrequencia semCompras() {
        return new AnaliseFrequencia(0, null, ClassificacaoFrequencia.SEM_COMPRAS);
    }

    public static AnaliseFrequencia calcular(List<LocalDate> datasCompra, LocalDate referencia) {
        if (datasCompra == null || datasCompra.isEmpty()) {
            return semCompras();
        }
        double mediaDias = mediaDiasEntreCompras(datasCompra, referencia);
        double mediaArredondada = Math.round(mediaDias * 100.0) / 100.0;
        return new AnaliseFrequencia(
                datasCompra.size(),
                mediaArredondada,
                ClassificacaoFrequencia.desdeMediaDias(mediaDias));
    }

    private static double mediaDiasEntreCompras(List<LocalDate> datasCompra, LocalDate referencia) {
        if (datasCompra.size() == 1) {
            return ChronoUnit.DAYS.between(datasCompra.get(0), referencia);
        }
        long somaIntervalos = 0L;
        for (int i = 1; i < datasCompra.size(); i++) {
            somaIntervalos += ChronoUnit.DAYS.between(datasCompra.get(i - 1), datasCompra.get(i));
        }
        return (double) somaIntervalos / (datasCompra.size() - 1);
    }

    public int totalPedidos() { return totalPedidos; }
    public Double mediaDiasEntreCompras() { return mediaDiasEntreCompras; }
    public ClassificacaoFrequencia classificacao() { return classificacao; }
}
