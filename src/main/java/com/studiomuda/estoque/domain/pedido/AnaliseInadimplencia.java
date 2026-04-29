package com.studiomuda.estoque.domain.pedido;

import java.time.LocalDate;

public final class AnaliseInadimplencia {
    private final boolean bloqueado;
    private final Integer pedidoPendenteId;
    private final LocalDate dataPedidoPendente;
    private final int diasAtraso;

    private AnaliseInadimplencia(boolean bloqueado, Integer pedidoPendenteId,
                                  LocalDate dataPedidoPendente, int diasAtraso) {
        this.bloqueado = bloqueado;
        this.pedidoPendenteId = pedidoPendenteId;
        this.dataPedidoPendente = dataPedidoPendente;
        this.diasAtraso = diasAtraso;
    }

    public static AnaliseInadimplencia naoBloqueado() {
        return new AnaliseInadimplencia(false, null, null, 0);
    }

    public static AnaliseInadimplencia bloqueado(int pedidoPendenteId, LocalDate dataPedidoPendente, int diasAtraso) {
        return new AnaliseInadimplencia(true, pedidoPendenteId, dataPedidoPendente, diasAtraso);
    }

    public boolean bloqueado() { return bloqueado; }
    public Integer pedidoPendenteId() { return pedidoPendenteId; }
    public LocalDate dataPedidoPendente() { return dataPedidoPendente; }
    public int diasAtraso() { return diasAtraso; }
}
