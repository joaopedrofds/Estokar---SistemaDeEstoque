package com.studiomuda.estoque.service;

import com.studiomuda.estoque.dao.SuprimentoDAO;
import com.studiomuda.estoque.model.OrdemCompra;
import com.studiomuda.estoque.model.ParametroEstoque;

import java.sql.SQLException;
import java.util.List;

public class SuprimentoService {
    private final SuprimentoDAO suprimentoDAO;

    public SuprimentoService() {
        this(new SuprimentoDAO());
    }

    public SuprimentoService(SuprimentoDAO suprimentoDAO) {
        this.suprimentoDAO = suprimentoDAO;
    }

    public void salvarParametro(int produtoId, String fornecedorNome, int leadTimeDias, int margemSeguranca) throws SQLException {
        validarParametro(produtoId, fornecedorNome, leadTimeDias, margemSeguranca);
        suprimentoDAO.salvarParametro(produtoId, fornecedorNome.trim(), leadTimeDias, margemSeguranca);
    }

    private void validarParametro(int produtoId, String fornecedorNome, int leadTimeDias, int margemSeguranca) {
        if (produtoId <= 0) {
            throw new IllegalArgumentException("Selecione um produto valido.");
        }
        if (fornecedorNome == null || fornecedorNome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome do fornecedor.");
        }
        if (leadTimeDias <= 0) {
            throw new IllegalArgumentException("O lead time deve ser maior que zero.");
        }
        if (margemSeguranca < 0) {
            throw new IllegalArgumentException("A margem de seguranca nao pode ser negativa.");
        }
    }

    public List<ParametroEstoque> listarParametrosComMetricas() throws SQLException {
        List<ParametroEstoque> parametros = suprimentoDAO.listarParametros();
        for (ParametroEstoque parametro : parametros) {
            preencherMetricas(parametro);
        }
        return parametros;
    }

    public boolean gerarRascunhoSeNecessario(int produtoId) throws SQLException {
        ParametroEstoque parametro = suprimentoDAO.buscarParametroPorProduto(produtoId);
        if (parametro == null) {
            return false;
        }

        preencherMetricas(parametro);
        if (!parametro.isReposicaoNecessaria() || suprimentoDAO.existeRascunhoParaProduto(produtoId)) {
            return false;
        }

        return suprimentoDAO.criarOrdemRascunho(
                parametro.getFornecedorId(),
                produtoId,
                parametro.calcularQuantidadeSugerida()
        );
    }

    public int gerarRascunhosPendentes() throws SQLException {
        int geradas = 0;
        for (ParametroEstoque parametro : listarParametrosComMetricas()) {
            if (parametro.isReposicaoNecessaria() && gerarRascunhoSeNecessario(parametro.getProdutoId())) {
                geradas++;
            }
        }
        return geradas;
    }

    private void preencherMetricas(ParametroEstoque parametro) throws SQLException {
        double consumoMedioDiario = suprimentoDAO.calcularConsumoMedioDiario(parametro.getProdutoId());
        parametro.calcularPontoPedido(consumoMedioDiario);
    }

    public void atualizarRascunho(int ordemId, int quantidade, double valorUnitario) throws SQLException {
        OrdemCompra ordem = suprimentoDAO.buscarOrdemPorId(ordemId);
        if (ordem == null) {
            throw new IllegalArgumentException("Ordem de compra nao encontrada.");
        }
        ordem.ajustarRascunho(quantidade, valorUnitario);
        suprimentoDAO.atualizarRascunho(ordem);
    }

    public void aprovar(int ordemId) throws SQLException {
        suprimentoDAO.alterarStatus(ordemId, OrdemCompra.STATUS_APROVADA);
    }

    public void rejeitar(int ordemId) throws SQLException {
        suprimentoDAO.alterarStatus(ordemId, OrdemCompra.STATUS_REJEITADA);
    }

    public List<OrdemCompra> listarOrdens() throws SQLException {
        return suprimentoDAO.listarOrdens();
    }
}
