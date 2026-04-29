package com.studiomuda.estoque.application.pedido;

import com.studiomuda.estoque.application.pedido.dto.SalvarPedidoCommand;
import com.studiomuda.estoque.domain.cliente.Cliente;
import com.studiomuda.estoque.domain.cliente.ClienteRepository;
import com.studiomuda.estoque.domain.cupom.Cupom;
import com.studiomuda.estoque.domain.cupom.CupomRepository;
import com.studiomuda.estoque.domain.pedido.AnaliseInadimplencia;
import com.studiomuda.estoque.domain.pedido.Pedido;
import com.studiomuda.estoque.domain.pedido.PedidoRepository;
import com.studiomuda.estoque.domain.pedido.StatusPagamento;
import com.studiomuda.estoque.domain.pedido.exceptions.ClienteInadimplenteException;
import org.springframework.stereotype.Service;

@Service
public class CriarPedidoUseCase {
    public static final int DIAS_LIMITE_INADIMPLENCIA = 45;

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final CupomRepository cupomRepository;

    public CriarPedidoUseCase(PedidoRepository pedidoRepository,
                              ClienteRepository clienteRepository,
                              CupomRepository cupomRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.cupomRepository = cupomRepository;
    }

    public Pedido executar(SalvarPedidoCommand cmd) {
        AnaliseInadimplencia analise = pedidoRepository.verificarInadimplenciaCliente(
                cmd.clienteId(), DIAS_LIMITE_INADIMPLENCIA);

        if (analise.bloqueado()) {
            bloquearCliente(cmd.clienteId());
            String mensagem = "Cliente bloqueado automaticamente por inadimplência. " +
                    "Existe pendência com " + analise.diasAtraso() + " dias de atraso " +
                    "(pedido #" + analise.pedidoPendenteId() + ").";
            pedidoRepository.registrarAlertaFinanceiro(cmd.clienteId(),
                    analise.pedidoPendenteId(), analise.diasAtraso(), mensagem);
            throw new ClienteInadimplenteException(mensagem, analise);
        }

        StatusPagamento statusPag = StatusPagamento.fromCodigo(cmd.statusPagamento());
        Pedido pedido = Pedido.novo(cmd.dataRequisicao(), cmd.dataEntrega(),
                cmd.clienteId(), cmd.funcionarioId(), cmd.cupomId(), 0.0,
                statusPag, cmd.dataPagamento());
        aplicarCupomSeValido(pedido, cmd.cupomId());

        return pedidoRepository.salvar(pedido);
    }

    private void bloquearCliente(int clienteId) {
        clienteRepository.buscarPorId(clienteId).ifPresent(cliente -> {
            cliente.bloquearPorInadimplencia();
            clienteRepository.atualizar(cliente);
        });
    }

    private void aplicarCupomSeValido(Pedido pedido, Integer cupomId) {
        if (cupomId == null || cupomId <= 0) return;
        Cupom cupom = cupomRepository.buscarPorId(cupomId).orElse(null);
        if (cupom != null && cupom.valido()) {
            pedido.atribuirCupom(cupomId, cupom.valor());
        }
    }
}
