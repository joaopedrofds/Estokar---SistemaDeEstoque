package com.studiomuda.estoque.domain.pedido;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository {

    Pedido salvar(Pedido pedido);

    void atualizar(Pedido pedido);

    Optional<Pedido> buscarPorId(int id);

    List<PedidoComJoins> listarTodos();

    List<PedidoComJoins> listarPorCliente(int clienteId);

    List<PedidoComJoins> buscarComFiltros(String cpfCnpj, String status, LocalDate dataInicio,
                                           LocalDate dataFim, Integer funcionarioId, Integer clienteId,
                                           Integer cupomId);

    void remover(int id);

    AnaliseInadimplencia verificarInadimplenciaCliente(int clienteId, int diasLimite);

    void registrarAlertaFinanceiro(int clienteId, Integer pedidoId, int diasAtraso, String mensagem);

    List<LocalDate> listarDatasCompraPorCliente(int clienteId);
}
