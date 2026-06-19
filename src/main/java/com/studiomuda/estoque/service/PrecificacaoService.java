package com.studiomuda.estoque.service;

import com.studiomuda.estoque.model.*;
import com.studiomuda.estoque.observer.*;
import com.studiomuda.estoque.repository.*;
import com.studiomuda.estoque.strategy.ContextoPrecificacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de domínio — Motor de Precificação Dinâmica.
 *
 * Implementa Template Method para estrutura fixa do cálculo:
 *   1. carregarDados() 2. selecionarStrategy() 3. calcular()
 *   4. validarMargem() 5. retornarResultado()
 *
 * Atua como Subject do Observer ao aplicar o preço calculado.
 *
 * Padrão de Design: Template Method + Observer (GoF)
 * Nível Tático DDD: Domain Service
 * Camada: Aplicação (Arquitetura Limpa)
 * Persistência: ORM via Spring Data JPA
 */
@Service
public class PrecificacaoService {

    @Autowired private RegraPrecificacaoRepository regraRepo;
    @Autowired private SimulacaoPrecoRepository simulacaoRepo;
    @Autowired private ParametroPrecificacaoRepository parametroRepo;
    @Autowired private ProdutoRepository produtoRepo;

    private final List<ObservadorDePreco> observadores;

    @Autowired
    public PrecificacaoService(List<ObservadorDePreco> observadores) {
        this.observadores = observadores;
    }

    // ─── Template Method ─────────────────────────────────────────────────────

    /**
     * Template Method — estrutura fixa do cálculo de precificação.
     * Etapas invariáveis, algoritmo variável (Strategy).
     */
    public ResultadoSimulacao simular(int produtoId, String usuarioLogin) {
        // Etapa 1: Carregar dados
        Produto produto = carregarProduto(produtoId);
        ParametroPrecificacao params = carregarParametros();
        RegraPrecificacao regra = carregarRegra(produtoId, params);

        // Etapa 2: Selecionar e executar Strategy
        ContextoPrecificacao ctx = new ContextoPrecificacao(regra.getTipoEstrategia());
        double precoSugerido = ctx.calcular(
            produto.getValor(),
            produto.getCusto() > 0 ? produto.getCusto() : produto.getValor() * 0.55,
            produto.getQuantidade(),
            regra.getValorParametro()
        );

        // Etapa 3: Validar desconto máximo
        double descontoAplicado = produto.getValor() > 0
            ? ((produto.getValor() - precoSugerido) / produto.getValor()) * 100 : 0;
        if (descontoAplicado > params.getDescontoMaximoGlobal()) {
            precoSugerido = produto.getValor() *
                (1 - params.getDescontoMaximoGlobal() / 100.0);
        }

        // Etapa 4: Calcular margem e validar
        double custo = produto.getCusto() > 0
            ? produto.getCusto() : produto.getValor() * 0.55;
        ResultadoSimulacao resultado = new ResultadoSimulacao(
            produtoId, produto.getNome(),
            produto.getValor(), custo,
            precoSugerido, params.getMargemMinimaGlobal(),
            ctx.tipo(), ctx.descricao()
        );

        // Etapa 5: Persistir simulação via JPA
        persistirSimulacao(resultado, usuarioLogin);
        return resultado;
    }

    /**
     * Aplica o preço sugerido — dispara Observer que atualiza produto e histórico.
     * Valida que o preço atende à margem mínima antes de aplicar.
     */
    @Transactional
    public void aplicarPreco(int produtoId, double precoNovo, String usuarioLogin) {
        Produto produto = carregarProduto(produtoId);
        ParametroPrecificacao params = carregarParametros();
        double precoAnterior = produto.getValor();
        double custo = produto.getCusto() > 0 ? produto.getCusto() : produto.getValor() * 0.55;

        // Validar margem mínima
        double margemCalculada = precoNovo > 0
            ? ((precoNovo - custo) / precoNovo) * 100.0
            : 0;
        if (margemCalculada < params.getMargemMinimaGlobal()) {
            throw new IllegalStateException(
                String.format("Preço R$ %.2f não atende margem mínima de %.1f%%. Margem seria %.1f%%. Preço mínimo: R$ %.2f",
                    precoNovo, params.getMargemMinimaGlobal(), margemCalculada,
                    custo / (1 - params.getMargemMinimaGlobal() / 100.0))
            );
        }

        // Validar desconto máximo
        double descontoAplicado = precoAnterior > 0
            ? ((precoAnterior - precoNovo) / precoAnterior) * 100 : 0;
        if (descontoAplicado > params.getDescontoMaximoGlobal()) {
            throw new IllegalStateException(
                String.format("Desconto de %.1f%% excede o máximo global de %.1f%%",
                    descontoAplicado, params.getDescontoMaximoGlobal())
            );
        }

        // Dispara Observer — atualiza produto + registra histórico
        PrecoDomainEvent evento = new PrecoDomainEvent(
            produtoId, produto.getNome(),
            precoAnterior, precoNovo, usuarioLogin
        );
        notificarObservadores(evento);

        // Marca última simulação como APLICADO
        simulacaoRepo.findByProdutoIdOrderByDataSimulacaoDesc(produtoId)
            .stream().findFirst().ifPresent(s -> {
                s.setStatus("APLICADO");
                s.setAplicado(true);
                simulacaoRepo.save(s);
            });
    }

    // ─── Métodos auxiliares do Template Method ────────────────────────────────

    private Produto carregarProduto(int produtoId) {
        return produtoRepo.findById(produtoId)
            .orElseThrow(() -> new IllegalStateException("Produto não encontrado: " + produtoId));
    }

    private ParametroPrecificacao carregarParametros() {
        return parametroRepo.findFirstByOrderByIdDesc()
            .orElseGet(() -> {
                ParametroPrecificacao p = new ParametroPrecificacao();
                p.setAtualizadoEm(LocalDateTime.now());
                return parametroRepo.save(p);
            });
    }

    private RegraPrecificacao carregarRegra(int produtoId, ParametroPrecificacao params) {
        return regraRepo.findFirstByProdutoIdAndAtivoTrueOrderByDataCriacaoDesc(produtoId)
            .orElseGet(() -> {
                RegraPrecificacao padrao = new RegraPrecificacao();
                padrao.setProdutoId(produtoId);
                padrao.setTipoEstrategia("MARGEM_FIXA");
                padrao.setValorParametro(params.getMarkupPadrao());
                padrao.setAtivo(true);
                return padrao;
            });
    }

    @Transactional
    private void persistirSimulacao(ResultadoSimulacao r, String usuario) {
        SimulacaoPreco s = new SimulacaoPreco();
        s.setProdutoId(r.getProdutoId());
        s.setPrecoAtual(r.getPrecoAtual());
        s.setCustoProduto(r.getCustoProduto());
        s.setTipoEstrategia(r.getTipoEstrategia());
        s.setPrecoSugerido(r.getPrecoSugerido());
        s.setMargemCalculada(r.getMargemCalculada());
        s.setStatus(r.getStatus());
        s.setJustificativa(r.getJustificativa());
        s.setUsuarioResponsavel(usuario != null ? usuario : "sistema");
        s.setAplicado(false);
        s.setDataSimulacao(LocalDateTime.now());
        simulacaoRepo.save(s);
    }

    private void notificarObservadores(PrecoDomainEvent evento) {
        for (ObservadorDePreco obs : observadores) obs.aoAlterarPreco(evento);
    }

    // ─── Métodos de consulta ──────────────────────────────────────────────────

    public List<RegraPrecificacao> listarRegras() {
        return regraRepo.findAllByOrderByDataCriacaoDesc();
    }

    public ParametroPrecificacao buscarParametros() {
        return carregarParametros();
    }

    public List<SimulacaoPreco> listarSimulacoes() {
        return simulacaoRepo.findAllByOrderByDataSimulacaoDesc();
    }

    @Transactional
    public RegraPrecificacao salvarRegra(RegraPrecificacao regra) {
        regra.setDataCriacao(LocalDateTime.now());
        return regraRepo.save(regra);
    }

    @Transactional
    public ParametroPrecificacao salvarParametros(ParametroPrecificacao params) {
        params.setAtualizadoEm(LocalDateTime.now());
        return parametroRepo.save(params);
    }

    @Transactional
    public void excluirRegra(int id) {
        regraRepo.deleteById(id);
    }
}