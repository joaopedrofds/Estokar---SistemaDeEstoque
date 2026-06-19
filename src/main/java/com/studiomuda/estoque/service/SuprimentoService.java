package com.studiomuda.estoque.service;

import com.studiomuda.estoque.dao.SuprimentoDAO;
import com.studiomuda.estoque.jpa.entity.FornecedorJpaEntity;
import com.studiomuda.estoque.jpa.entity.ItemOrdemCompraJpaEntity;
import com.studiomuda.estoque.jpa.entity.OrdemCompraJpaEntity;
import com.studiomuda.estoque.jpa.entity.ParametroEstoqueJpaEntity;
import com.studiomuda.estoque.jpa.entity.ProdutoJpaEntity;
import com.studiomuda.estoque.jpa.repository.FornecedorJpaRepository;
import com.studiomuda.estoque.jpa.repository.MovimentacaoEstoqueJpaRepository;
import com.studiomuda.estoque.jpa.repository.OrdemCompraJpaRepository;
import com.studiomuda.estoque.jpa.repository.ParametroEstoqueJpaRepository;
import com.studiomuda.estoque.jpa.repository.ProdutoJpaRepository;
import com.studiomuda.estoque.model.OrdemCompra;
import com.studiomuda.estoque.model.ParametroEstoque;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SuprimentoService {
    private final SuprimentoDAO suprimentoDAO;
    private final ObjectProvider<ProdutoJpaRepository> produtoRepositoryProvider;
    private final ObjectProvider<FornecedorJpaRepository> fornecedorRepositoryProvider;
    private final ObjectProvider<ParametroEstoqueJpaRepository> parametroRepositoryProvider;
    private final ObjectProvider<OrdemCompraJpaRepository> ordemRepositoryProvider;
    private final ObjectProvider<MovimentacaoEstoqueJpaRepository> movimentacaoRepositoryProvider;

    public SuprimentoService() {
        this(new SuprimentoDAO(), null, null, null, null, null);
    }

    public SuprimentoService(SuprimentoDAO suprimentoDAO) {
        this(suprimentoDAO, null, null, null, null, null);
    }

    public SuprimentoService(ObjectProvider<ProdutoJpaRepository> produtoRepositoryProvider,
                             ObjectProvider<FornecedorJpaRepository> fornecedorRepositoryProvider,
                             ObjectProvider<ParametroEstoqueJpaRepository> parametroRepositoryProvider,
                             ObjectProvider<OrdemCompraJpaRepository> ordemRepositoryProvider,
                             ObjectProvider<MovimentacaoEstoqueJpaRepository> movimentacaoRepositoryProvider) {
        this.suprimentoDAO = null;
        this.produtoRepositoryProvider = produtoRepositoryProvider;
        this.fornecedorRepositoryProvider = fornecedorRepositoryProvider;
        this.parametroRepositoryProvider = parametroRepositoryProvider;
        this.ordemRepositoryProvider = ordemRepositoryProvider;
        this.movimentacaoRepositoryProvider = movimentacaoRepositoryProvider;
    }

    public SuprimentoService(SuprimentoDAO suprimentoDAO,
                             ObjectProvider<ProdutoJpaRepository> produtoRepositoryProvider,
                             ObjectProvider<FornecedorJpaRepository> fornecedorRepositoryProvider,
                             ObjectProvider<ParametroEstoqueJpaRepository> parametroRepositoryProvider,
                             ObjectProvider<OrdemCompraJpaRepository> ordemRepositoryProvider,
                             ObjectProvider<MovimentacaoEstoqueJpaRepository> movimentacaoRepositoryProvider) {
        this.suprimentoDAO = suprimentoDAO;
        this.produtoRepositoryProvider = produtoRepositoryProvider;
        this.fornecedorRepositoryProvider = fornecedorRepositoryProvider;
        this.parametroRepositoryProvider = parametroRepositoryProvider;
        this.ordemRepositoryProvider = ordemRepositoryProvider;
        this.movimentacaoRepositoryProvider = movimentacaoRepositoryProvider;
    }

    @Transactional(readOnly = true)
    public List<ProdutoJpaEntity> listarProdutos() {
        if (produtoRepositoryProvider != null) {
            return obterProdutoRepository().findAllByOrderByNomeAsc();
        }
        return java.util.Collections.emptyList();
    }

    @Transactional
    public void salvarParametro(int produtoId, String fornecedorNome, int leadTimeDias, int margemSeguranca) throws SQLException {
        validarParametro(produtoId, fornecedorNome, leadTimeDias, margemSeguranca);

        if (parametroRepositoryProvider != null && obterParametroRepository() != null) {
            salvarParametroJpa(produtoId, fornecedorNome, leadTimeDias, margemSeguranca);
        } else {
            suprimentoDAO.salvarParametro(produtoId, fornecedorNome.trim(), leadTimeDias, margemSeguranca);
        }
    }

    private void salvarParametroJpa(int produtoId, String fornecedorNome, int leadTimeDias, int margemSeguranca) {
        ProdutoJpaEntity produto = obterProdutoRepository().findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));

        FornecedorJpaEntity fornecedor = new FornecedorJpaEntity();
        fornecedor.setNome(fornecedorNome.trim());
        fornecedor.setLeadTimeDias(leadTimeDias);
        fornecedor.setAtivo(true);
        fornecedor = obterFornecedorRepository().save(fornecedor);

        ParametroEstoqueJpaEntity parametro = obterParametroRepository().buscarPorProduto(produtoId)
                .orElseGet(ParametroEstoqueJpaEntity::new);
        parametro.setProduto(produto);
        parametro.setFornecedor(fornecedor);
        parametro.setMargemSeguranca(margemSeguranca);
        obterParametroRepository().save(parametro);
    }

    @Transactional(readOnly = true)
    public List<ParametroEstoque> listarParametrosComMetricas() throws SQLException {
        if (parametroRepositoryProvider != null && obterParametroRepository() != null) {
            return listarParametrosComMetricasJpa();
        }
        return listarParametrosComMetricasDao();
    }

    private List<ParametroEstoque> listarParametrosComMetricasJpa() {
        return obterParametroRepository().listarComRelacoes().stream()
                .map(this::mapearParametro)
                .collect(Collectors.toList());
    }

    private List<ParametroEstoque> listarParametrosComMetricasDao() throws SQLException {
        List<ParametroEstoque> parametros = suprimentoDAO.listarParametros();
        for (ParametroEstoque parametro : parametros) {
            preencherMetricas(parametro);
        }
        return parametros;
    }

    private void preencherMetricas(ParametroEstoque parametro) throws SQLException {
        double consumoMedioDiario = suprimentoDAO.calcularConsumoMedioDiario(parametro.getProdutoId());
        parametro.calcularPontoPedido(consumoMedioDiario);
    }

    @Transactional
    public boolean gerarRascunhoSeNecessario(int produtoId) throws SQLException {
        if (parametroRepositoryProvider != null && obterParametroRepository() != null) {
            return gerarRascunhoSeNecessarioJpa(produtoId);
        }
        return gerarRascunhoSeNecessarioDao(produtoId);
    }

    private boolean gerarRascunhoSeNecessarioJpa(int produtoId) {
        ParametroEstoqueJpaEntity parametro = obterParametroRepository().buscarPorProduto(produtoId)
                .orElse(null);
        if (parametro == null) {
            return false;
        }

        ParametroEstoque modelo = mapearParametro(parametro);
        if (!modelo.isReposicaoNecessaria() || obterOrdemRepository().existeRascunhoParaProduto(OrdemCompra.STATUS_RASCUNHO, produtoId)) {
            return false;
        }

        return criarOrdemRascunho(parametro, modelo.calcularQuantidadeSugerida());
    }

    private boolean gerarRascunhoSeNecessarioDao(int produtoId) throws SQLException {
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

    @Transactional
    public int gerarRascunhosPendentes() throws SQLException {
        int geradas = 0;
        for (ParametroEstoque parametro : listarParametrosComMetricas()) {
            if (parametro.isReposicaoNecessaria() && gerarRascunhoSeNecessario(parametro.getProdutoId())) {
                geradas++;
            }
        }
        return geradas;
    }

    @Transactional
    public void atualizarRascunho(int ordemId, int quantidade, double valorUnitario) throws SQLException {
        if (ordemRepositoryProvider != null && obterOrdemRepository() != null) {
            atualizarRascunhoJpa(ordemId, quantidade, valorUnitario);
        } else {
            atualizarRascunhoDao(ordemId, quantidade, valorUnitario);
        }
    }

    private void atualizarRascunhoJpa(int ordemId, int quantidade, double valorUnitario) {
        OrdemCompraJpaEntity ordem = obterOrdemRepository().buscarCompletaPorId(ordemId)
                .orElseThrow(() -> new IllegalArgumentException("Ordem de compra nao encontrada."));
        if (!OrdemCompra.STATUS_RASCUNHO.equals(ordem.getStatus())) {
            throw new IllegalStateException("Apenas ordens em rascunho podem ser ajustadas.");
        }
        if (ordem.getItens().isEmpty()) {
            throw new IllegalStateException("A ordem nao possui itens para ajuste.");
        }

        ItemOrdemCompraJpaEntity item = ordem.getItens().get(0);
        BigDecimal valorUnitarioDecimal = BigDecimal.valueOf(valorUnitario);
        BigDecimal total = valorUnitarioDecimal.multiply(BigDecimal.valueOf(quantidade));

        item.setQuantidade(quantidade);
        item.setValorUnitario(valorUnitarioDecimal);
        ordem.setValorTotal(total);
        obterOrdemRepository().save(ordem);
    }

    private void atualizarRascunhoDao(int ordemId, int quantidade, double valorUnitario) throws SQLException {
        OrdemCompra ordem = suprimentoDAO.buscarOrdemPorId(ordemId);
        if (ordem == null) {
            throw new IllegalArgumentException("Ordem de compra nao encontrada.");
        }
        ordem.ajustarRascunho(quantidade, valorUnitario);
        suprimentoDAO.atualizarRascunho(ordem);
    }

    @Transactional
    public void aprovar(int ordemId) throws SQLException {
        if (ordemRepositoryProvider != null && obterOrdemRepository() != null) {
            alterarStatusJpa(ordemId, OrdemCompra.STATUS_APROVADA);
        } else {
            suprimentoDAO.alterarStatus(ordemId, OrdemCompra.STATUS_APROVADA);
        }
    }

    @Transactional
    public void rejeitar(int ordemId) throws SQLException {
        if (ordemRepositoryProvider != null && obterOrdemRepository() != null) {
            alterarStatusJpa(ordemId, OrdemCompra.STATUS_REJEITADA);
        } else {
            suprimentoDAO.alterarStatus(ordemId, OrdemCompra.STATUS_REJEITADA);
        }
    }

    private void alterarStatusJpa(int ordemId, String status) {
        OrdemCompraJpaEntity ordem = obterOrdemRepository().buscarCompletaPorId(ordemId)
                .orElseThrow(() -> new IllegalArgumentException("Ordem de compra nao encontrada."));
        if (!OrdemCompra.STATUS_RASCUNHO.equals(ordem.getStatus())) {
            throw new IllegalStateException("Apenas ordens em rascunho podem ser alteradas.");
        }

        ordem.setStatus(status);
        ordem.setDataAprovacao(OrdemCompra.STATUS_APROVADA.equals(status) ? Date.valueOf(LocalDate.now()) : null);
        obterOrdemRepository().save(ordem);
    }

    @Transactional(readOnly = true)
    public List<OrdemCompra> listarOrdens() throws SQLException {
        if (ordemRepositoryProvider != null && obterOrdemRepository() != null) {
            return listarOrdensJpa();
        }
        return listarOrdensDao();
    }

    private List<OrdemCompra> listarOrdensJpa() {
        return obterOrdemRepository().listarComItens().stream()
                .map(this::mapearOrdem)
                .collect(Collectors.toList());
    }

    private List<OrdemCompra> listarOrdensDao() throws SQLException {
        return suprimentoDAO.listarOrdens();
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

    private boolean criarOrdemRascunho(ParametroEstoqueJpaEntity parametro, int quantidade) {
        ProdutoJpaEntity produto = parametro.getProduto();
        FornecedorJpaEntity fornecedor = parametro.getFornecedor();

        BigDecimal valorUnitario = produto.getValor() != null ? produto.getValor() : BigDecimal.ZERO;

        OrdemCompraJpaEntity ordem = new OrdemCompraJpaEntity();
        ordem.setCodigoOrdem("SUP-" + UUID.randomUUID());
        ordem.setFornecedor(fornecedor);
        ordem.setStatus(OrdemCompra.STATUS_RASCUNHO);
        ordem.setDataCriacao(Date.valueOf(LocalDate.now()));
        ordem.setValorTotal(valorUnitario.multiply(BigDecimal.valueOf(quantidade)));

        ItemOrdemCompraJpaEntity item = new ItemOrdemCompraJpaEntity();
        item.setOrdemCompra(ordem);
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setValorUnitario(valorUnitario);
        ordem.getItens().add(item);

        obterOrdemRepository().save(ordem);
        return true;
    }

    private ParametroEstoque mapearParametro(ParametroEstoqueJpaEntity entidade) {
        ParametroEstoque parametro = new ParametroEstoque();
        parametro.setId(entidade.getId() != null ? entidade.getId() : 0);
        parametro.setProdutoId(entidade.getProduto().getId() != null ? entidade.getProduto().getId() : 0);
        parametro.setFornecedorId(entidade.getFornecedor().getId() != null ? entidade.getFornecedor().getId() : 0);
        parametro.setMargemSeguranca(entidade.getMargemSeguranca() != null ? entidade.getMargemSeguranca() : 0);
        parametro.setProdutoNome(entidade.getProduto().getNome());
        parametro.setFornecedorNome(entidade.getFornecedor().getNome());
        parametro.setLeadTimeDias(entidade.getFornecedor().getLeadTimeDias() != null ? entidade.getFornecedor().getLeadTimeDias() : 0);
        parametro.setEstoqueAtual(entidade.getProduto().getQuantidade() != null ? entidade.getProduto().getQuantidade() : 0);

        double consumoMedioDiario = calcularConsumoMedioDiario(parametro.getProdutoId());
        parametro.calcularPontoPedido(consumoMedioDiario);
        return parametro;
    }

    private double calcularConsumoMedioDiario(int produtoId) {
        Date dataMinima = Date.valueOf(LocalDate.now().minusDays(90));
        Long totalSaidas = obterMovimentacaoRepository().somarSaidasUltimos90Dias(produtoId, dataMinima);
        Long dias = obterMovimentacaoRepository().calcularDiasConsumo(produtoId, dataMinima);
        if (totalSaidas == null || dias == null || dias <= 0) {
            return 0;
        }
        return BigDecimal.valueOf(totalSaidas).divide(BigDecimal.valueOf(dias), 4, RoundingMode.HALF_UP).doubleValue();
    }

    private OrdemCompra mapearOrdem(OrdemCompraJpaEntity entidade) {
        OrdemCompra ordem = new OrdemCompra();
        ordem.setId(entidade.getId() != null ? entidade.getId() : 0);
        ordem.setCodigoOrdem(entidade.getCodigoOrdem());
        ordem.setFornecedorId(entidade.getFornecedor().getId() != null ? entidade.getFornecedor().getId() : 0);
        ordem.setFornecedorNome(entidade.getFornecedor().getNome());
        ordem.setStatus(entidade.getStatus());
        ordem.setValorTotal(entidade.getValorTotal() != null ? entidade.getValorTotal().doubleValue() : 0);
        ordem.setDataCriacao(entidade.getDataCriacao());
        ordem.setDataAprovacao(entidade.getDataAprovacao());

        if (!entidade.getItens().isEmpty()) {
            ItemOrdemCompraJpaEntity item = entidade.getItens().get(0);
            ordem.setItemId(item.getId() != null ? item.getId() : 0);
            ordem.setProdutoId(item.getProduto().getId() != null ? item.getProduto().getId() : 0);
            ordem.setProdutoNome(item.getProduto().getNome());
            ordem.setQuantidade(item.getQuantidade() != null ? item.getQuantidade() : 0);
            ordem.setValorUnitario(item.getValorUnitario() != null ? item.getValorUnitario().doubleValue() : 0);
        }

        return ordem;
    }

    private ProdutoJpaRepository obterProdutoRepository() {
        ProdutoJpaRepository repository = produtoRepositoryProvider.getIfAvailable();
        if (repository == null) {
            throw new IllegalStateException("Repositório de produtos indisponível.");
        }
        return repository;
    }

    private FornecedorJpaRepository obterFornecedorRepository() {
        FornecedorJpaRepository repository = fornecedorRepositoryProvider.getIfAvailable();
        if (repository == null) {
            throw new IllegalStateException("Repositório de fornecedores indisponível.");
        }
        return repository;
    }

    private ParametroEstoqueJpaRepository obterParametroRepository() {
        ParametroEstoqueJpaRepository repository = parametroRepositoryProvider.getIfAvailable();
        if (repository == null) {
            throw new IllegalStateException("Repositório de parametros indisponível.");
        }
        return repository;
    }

    private OrdemCompraJpaRepository obterOrdemRepository() {
        OrdemCompraJpaRepository repository = ordemRepositoryProvider.getIfAvailable();
        if (repository == null) {
            throw new IllegalStateException("Repositório de ordens indisponível.");
        }
        return repository;
    }

    private MovimentacaoEstoqueJpaRepository obterMovimentacaoRepository() {
        MovimentacaoEstoqueJpaRepository repository = movimentacaoRepositoryProvider.getIfAvailable();
        if (repository == null) {
            throw new IllegalStateException("Repositório de movimentações indisponível.");
        }
        return repository;
    }
}