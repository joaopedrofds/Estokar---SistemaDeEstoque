package com.studiomuda.estoque.application.pedido;

import com.studiomuda.estoque.application.pedido.dto.SalvarPedidoCommand;
import com.studiomuda.estoque.domain.cupom.Cupom;
import com.studiomuda.estoque.domain.cupom.CupomRepository;
import com.studiomuda.estoque.domain.pedido.Pedido;
import com.studiomuda.estoque.domain.pedido.PedidoRepository;
import com.studiomuda.estoque.domain.pedido.StatusPagamento;
import com.studiomuda.estoque.domain.pedido.exceptions.PedidoNaoEncontradoException;
import org.springframework.stereotype.Service;

@Service
public class AtualizarPedidoUseCase {
    private final PedidoRepository pedidoRepository;
    private final CupomRepository cupomRepository;

    public AtualizarPedidoUseCase(PedidoRepository pedidoRepository, CupomRepository cupomRepository) {
        this.pedidoRepository = pedidoRepository;
        this.cupomRepository = cupomRepository;
    }

    public void executar(SalvarPedidoCommand cmd) {
        Pedido existente = pedidoRepository.buscarPorId(cmd.id())
                .orElseThrow(() -> new PedidoNaoEncontradoException(cmd.id()));

        existente.atualizarDatas(cmd.dataRequisicao(), cmd.dataEntrega());
        existente.atualizarStatus(cmd.status());

        StatusPagamento statusPag = StatusPagamento.fromCodigo(cmd.statusPagamento());
        if (statusPag == StatusPagamento.PAGO) {
            existente.marcarComoPago(cmd.dataPagamento());
        } else {
            existente.marcarComoPendente();
        }

        existente.atribuirCupom(null, 0.0);
        if (cmd.cupomId() != null && cmd.cupomId() > 0) {
            Cupom cupom = cupomRepository.buscarPorId(cmd.cupomId()).orElse(null);
            if (cupom != null && cupom.valido()) {
                existente.atribuirCupom(cmd.cupomId(), cupom.valor());
            }
        }

        pedidoRepository.atualizar(existente);
    }
}
