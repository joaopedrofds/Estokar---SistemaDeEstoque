package com.studiomuda.estoque.precificacao.application.service;

import com.studiomuda.estoque.model.Produto;
import com.studiomuda.estoque.observer.ObservadorDePreco;
import com.studiomuda.estoque.observer.PrecoDomainEvent;
import com.studiomuda.estoque.precificacao.application.command.SalvarParametrosPrecificacaoCommand;
import com.studiomuda.estoque.precificacao.application.command.SalvarPoliticaPrecificacaoCommand;
import com.studiomuda.estoque.precificacao.application.command.SimularPrecoCommand;
import com.studiomuda.estoque.precificacao.application.dto.*;
import com.studiomuda.estoque.precificacao.domain.model.*;
import com.studiomuda.estoque.precificacao.infrastructure.persistence.entity.*;
import com.studiomuda.estoque.precificacao.infrastructure.persistence.repository.*;
import com.studiomuda.estoque.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class PrecificacaoDinamicaApplicationService {
    private final ProdutoRepository produtoRepository;
    private final ParametroPrecificacaoJpaRepository parametroRepository;
    private final PoliticaPrecificacaoJpaRepository politicaRepository;
    private final SimulacaoPrecificacaoJpaRepository simulacaoRepository;
    private final ComponentePrecificacaoJpaRepository componenteRepository;
    private final List<ObservadorDePreco> observadores;
    private final MotorPrecificacaoDinamica motor = new MotorPrecificacaoDinamica();

    public PrecificacaoDinamicaApplicationService(ProdutoRepository produtoRepository,
                                                  ParametroPrecificacaoJpaRepository parametroRepository,
                                                  PoliticaPrecificacaoJpaRepository politicaRepository,
                                                  SimulacaoPrecificacaoJpaRepository simulacaoRepository,
                                                  ComponentePrecificacaoJpaRepository componenteRepository,
                                                  List<ObservadorDePreco> observadores) {
        this.produtoRepository = produtoRepository;
        this.parametroRepository = parametroRepository;
        this.politicaRepository = politicaRepository;
        this.simulacaoRepository = simulacaoRepository;
        this.componenteRepository = componenteRepository;
        this.observadores = observadores;
    }

    @Transactional(readOnly = true)
    public PainelPrecificacaoView montarPainel() {
        ParametroPrecificacaoJpaEntity parametros = buscarOuCriarParametrosSomenteLeitura();
        List<ProdutoPrecificavelView> produtos = listarProdutosPrecificaveis();
        List<PoliticaPrecificacaoView> politicas = listarPoliticas();
        List<ResultadoPrecificacaoView> recentes = simulacaoRepository.findTop8ByOrderByDataSimulacaoDesc()
                .stream().map(this::toResultadoView).collect(Collectors.toList());
        return new PainelPrecificacaoView(produtos, politicas, recentes, montarKpis(), toParametroView(parametros));
    }

    @Transactional
    public ResultadoPrecificacaoView simular(SimularPrecoCommand command, String usuarioLogin) {
        Produto produto = carregarProduto(command.getProdutoId());
        ParametroPrecificacaoJpaEntity parametros = buscarOuCriarParametros();
        PoliticaPrecificacao politica = resolverPolitica(command, parametros, produto.getId());
        BigDecimal custoCompra = resolverCusto(command.getCustoCompra(), produto);

        ResultadoPrecificacao resultado = motor.calcular(
                produto.getId(),
                produto.getNome(),
                BigDecimal.valueOf(produto.getValor()),
                custoCompra,
                politica,
                parametros.getMargemMinimaGlobal(),
                parametros.getDescontoMaximoGlobal()
        );

        SimulacaoPrecificacaoJpaEntity simulacao = persistirSimulacao(resultado, usuarioLogin);
        return toResultadoView(simulacao);
    }

    @Transactional
    public void aplicarPreco(Long simulacaoId, String usuarioLogin) {
        SimulacaoPrecificacaoJpaEntity simulacao = simulacaoRepository.findById(simulacaoId)
                .orElseThrow(() -> new IllegalStateException("Simulação não encontrada: " + simulacaoId));
        if (simulacao.getStatus() != StatusPrecificacao.APROVADO) {
            throw new IllegalStateException("Apenas simulações aprovadas podem ser aplicadas.");
        }
        Produto produto = carregarProduto(simulacao.getProdutoId());
        double precoAnterior = produto.getValor();
        produto.setCusto(simulacao.getCustoCompra().doubleValue());
        produtoRepository.save(produto);

        PrecoDomainEvent evento = new PrecoDomainEvent(
                produto.getId(), produto.getNome(), precoAnterior,
                simulacao.getPrecoSugerido().doubleValue(), normalizarUsuario(usuarioLogin));
        notificarObservadores(evento);

        simulacao.setStatus(StatusPrecificacao.APLICADO);
        simulacao.setAplicado(true);
        simulacao.setDataAplicacao(LocalDateTime.now());
        simulacao.setUsuarioResponsavel(normalizarUsuario(usuarioLogin));
        simulacaoRepository.save(simulacao);
    }


    @Transactional
    public void aplicarPrecoLegado(int produtoId, BigDecimal precoNovo, String usuarioLogin) {
        Produto produto = carregarProduto(produtoId);
        BigDecimal custoCompra = resolverCusto(null, produto);
        ParametroPrecificacaoJpaEntity parametros = buscarOuCriarParametros();
        BigDecimal margem = calcularMargem(precoNovo, custoCompra);
        if (margem.compareTo(parametros.getMargemMinimaGlobal()) < 0) {
            throw new IllegalStateException("Preço informado não atende a margem mínima global.");
        }
        PrecoDomainEvent evento = new PrecoDomainEvent(produto.getId(), produto.getNome(), produto.getValor(),
                precoNovo.doubleValue(), normalizarUsuario(usuarioLogin));
        notificarObservadores(evento);
    }

    @Transactional(readOnly = true)
    public List<ResultadoPrecificacaoView> listarHistorico(String status) {
        if (status != null && !status.trim().isEmpty()) {
            if ("BLOQUEADO".equalsIgnoreCase(status)) {
                return simulacaoRepository.findAllByOrderByDataSimulacaoDesc().stream()
                        .filter(simulacao -> simulacao.getStatus() != null && simulacao.getStatus().isBloqueado())
                        .map(this::toResultadoView)
                        .collect(Collectors.toList());
            }
            try {
                StatusPrecificacao statusEnum = StatusPrecificacao.valueOf(status.toUpperCase());
                return simulacaoRepository.findByStatusOrderByDataSimulacaoDesc(statusEnum)
                        .stream().map(this::toResultadoView).collect(Collectors.toList());
            } catch (IllegalArgumentException ex) {
                return new ArrayList<>();
            }
        }
        return simulacaoRepository.findAllByOrderByDataSimulacaoDesc()
                .stream().map(this::toResultadoView).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PoliticaPrecificacaoView> listarPoliticas() {
        Map<Integer, Produto> produtos = produtoRepository.findAll().stream()
                .collect(Collectors.toMap(Produto::getId, Function.identity()));
        return politicaRepository.findAllByOrderByAtualizadoEmDesc().stream()
                .map(p -> toPoliticaView(p, produtos.get(p.getProdutoId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SalvarPoliticaPrecificacaoCommand carregarPoliticaParaEdicao(Long id) {
        PoliticaPrecificacaoJpaEntity entity = politicaRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Política não encontrada: " + id));
        SalvarPoliticaPrecificacaoCommand command = new SalvarPoliticaPrecificacaoCommand();
        command.setId(entity.getId());
        command.setProdutoId(entity.getProdutoId());
        command.setMargemLucroDesejada(entity.getMargemLucroDesejada());
        command.setAliquotaImpostos(entity.getAliquotaImpostos());
        command.setPercentualDespesasOperacionais(entity.getPercentualDespesasOperacionais());
        command.setDescontoMaximoPermitido(entity.getDescontoMaximoPermitido());
        command.setAtiva(entity.isAtiva());
        command.setObservacao(entity.getObservacao());
        return command;
    }

    @Transactional(readOnly = true)
    public SalvarPoliticaPrecificacaoCommand novaPoliticaPadrao() {
        ParametroPrecificacaoView params = buscarParametros();
        SalvarPoliticaPrecificacaoCommand command = new SalvarPoliticaPrecificacaoCommand();
        command.setAtiva(true);
        command.setMargemLucroDesejada(params.getMargemPadraoLucro());
        command.setAliquotaImpostos(params.getImpostoPadraoPercentual());
        command.setPercentualDespesasOperacionais(params.getDespesaOperacionalPadraoPercentual());
        command.setDescontoMaximoPermitido(params.getDescontoMaximoGlobal());
        command.setObservacao("Nova política baseada nos parâmetros globais");
        return command;
    }

    @Transactional
    public PoliticaPrecificacaoView salvarPolitica(SalvarPoliticaPrecificacaoCommand command) {
        carregarProduto(command.getProdutoId());
        PoliticaPrecificacao politicaDominio = new PoliticaPrecificacao(
                command.getId(), command.getProdutoId(), command.getMargemLucroDesejada(),
                command.getAliquotaImpostos(), command.getPercentualDespesasOperacionais(),
                command.getDescontoMaximoPermitido(), command.isAtiva(), command.getObservacao());

        PoliticaPrecificacaoJpaEntity entity = command.getId() != null
                ? politicaRepository.findById(command.getId()).orElse(new PoliticaPrecificacaoJpaEntity())
                : new PoliticaPrecificacaoJpaEntity();

        entity.setProdutoId(politicaDominio.getProdutoId());
        entity.setMargemLucroDesejada(politicaDominio.getMargemLucroDesejada());
        entity.setAliquotaImpostos(politicaDominio.getAliquotaImpostos());
        entity.setPercentualDespesasOperacionais(politicaDominio.getPercentualDespesasOperacionais());
        entity.setDescontoMaximoPermitido(politicaDominio.getDescontoMaximoPermitido());
        entity.setAtiva(politicaDominio.isAtiva());
        entity.setObservacao(politicaDominio.getObservacao());
        entity.setAtualizadoEm(LocalDateTime.now());
        if (entity.getCriadoEm() == null) {
            entity.setCriadoEm(LocalDateTime.now());
        }
        PoliticaPrecificacaoJpaEntity salvo = politicaRepository.save(entity);
        return toPoliticaView(salvo, carregarProduto(salvo.getProdutoId()));
    }

    @Transactional
    public void excluirPolitica(Long id) {
        PoliticaPrecificacaoJpaEntity entity = politicaRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Política não encontrada: " + id));
        entity.setAtiva(false);
        entity.setAtualizadoEm(LocalDateTime.now());
        politicaRepository.save(entity);
    }

    @Transactional
    public ParametroPrecificacaoView salvarParametros(SalvarParametrosPrecificacaoCommand command) {
        ParametroPrecificacaoJpaEntity entity = command.getId() != null
                ? parametroRepository.findById(command.getId()).orElse(new ParametroPrecificacaoJpaEntity())
                : buscarOuCriarParametros();
        entity.setMargemMinimaGlobal(percentual(command.getMargemMinimaGlobal()));
        entity.setDescontoMaximoGlobal(percentual(command.getDescontoMaximoGlobal()));
        entity.setMargemPadraoLucro(percentual(command.getMargemPadraoLucro()));
        entity.setImpostoPadraoPercentual(percentual(command.getImpostoPadraoPercentual()));
        entity.setDespesaOperacionalPadraoPercentual(percentual(command.getDespesaOperacionalPadraoPercentual()));
        entity.setAtualizadoEm(LocalDateTime.now());
        return toParametroView(parametroRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public ParametroPrecificacaoView buscarParametros() {
        return toParametroView(buscarOuCriarParametrosSomenteLeitura());
    }

    @Transactional(readOnly = true)
    public List<ProdutoPrecificavelView> listarProdutosPrecificaveis() {
        Map<Integer, PoliticaPrecificacaoJpaEntity> politicasAtivas = politicaRepository.findByAtivaTrueOrderByAtualizadoEmDesc()
                .stream().collect(Collectors.toMap(PoliticaPrecificacaoJpaEntity::getProdutoId, Function.identity(), (a, b) -> a));
        return produtoRepository.findAll().stream()
                .map(p -> toProdutoView(p, politicasAtivas.containsKey(p.getId())))
                .collect(Collectors.toList());
    }

    private Produto carregarProduto(int produtoId) {
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalStateException("Produto não encontrado: " + produtoId));
    }

    private ParametroPrecificacaoJpaEntity buscarOuCriarParametros() {
        return parametroRepository.findFirstByOrderByIdDesc().orElseGet(() -> {
            ParametroPrecificacaoJpaEntity entity = new ParametroPrecificacaoJpaEntity();
            entity.setAtualizadoEm(LocalDateTime.now());
            return parametroRepository.save(entity);
        });
    }

    private ParametroPrecificacaoJpaEntity buscarOuCriarParametrosSomenteLeitura() {
        return parametroRepository.findFirstByOrderByIdDesc().orElseGet(ParametroPrecificacaoJpaEntity::new);
    }

    private PoliticaPrecificacao resolverPolitica(SimularPrecoCommand command,
                                                  ParametroPrecificacaoJpaEntity parametros,
                                                  int produtoId) {
        BigDecimal margem = command.getMargemLucroDesejada();
        BigDecimal impostos = command.getAliquotaImpostos();
        BigDecimal despesas = command.getPercentualDespesasOperacionais();
        BigDecimal desconto = command.getDescontoMaximoPermitido();

        PoliticaPrecificacaoJpaEntity politicaBanco = politicaRepository
                .findFirstByProdutoIdAndAtivaTrueOrderByAtualizadoEmDesc(produtoId)
                .orElse(null);

        if (margem == null && politicaBanco != null) margem = politicaBanco.getMargemLucroDesejada();
        if (impostos == null && politicaBanco != null) impostos = politicaBanco.getAliquotaImpostos();
        if (despesas == null && politicaBanco != null) despesas = politicaBanco.getPercentualDespesasOperacionais();
        if (desconto == null && politicaBanco != null) desconto = politicaBanco.getDescontoMaximoPermitido();

        if (margem == null) margem = parametros.getMargemPadraoLucro();
        if (impostos == null) impostos = parametros.getImpostoPadraoPercentual();
        if (despesas == null) despesas = parametros.getDespesaOperacionalPadraoPercentual();
        if (desconto == null) desconto = parametros.getDescontoMaximoGlobal();

        return new PoliticaPrecificacao(
                politicaBanco != null ? politicaBanco.getId() : null,
                produtoId, margem, impostos, despesas, desconto, true,
                politicaBanco != null ? politicaBanco.getObservacao() : "Valores aplicados na simulação");
    }

    private BigDecimal resolverCusto(BigDecimal custoInformado, Produto produto) {
        if (custoInformado != null && custoInformado.compareTo(BigDecimal.ZERO) > 0) {
            return dinheiro(custoInformado);
        }
        if (produto.getCusto() != null && produto.getCusto() > 0) {
            return dinheiro(BigDecimal.valueOf(produto.getCusto()));
        }
        return dinheiro(BigDecimal.valueOf(produto.getValor()).multiply(BigDecimal.valueOf(0.55)));
    }

    private SimulacaoPrecificacaoJpaEntity persistirSimulacao(ResultadoPrecificacao resultado, String usuarioLogin) {
        SimulacaoPrecificacaoJpaEntity entity = new SimulacaoPrecificacaoJpaEntity();
        entity.setProdutoId(resultado.getProdutoId());
        entity.setProdutoNome(resultado.getProdutoNome());
        entity.setPrecoAtual(resultado.getPrecoAtual());
        entity.setCustoCompra(resultado.getCustoCompra());
        entity.setValorImpostos(resultado.getValorImpostos());
        entity.setValorDespesasOperacionais(resultado.getValorDespesasOperacionais());
        entity.setCustoTotal(resultado.getCustoTotal());
        entity.setPrecoSugerido(resultado.getPrecoSugerido());
        entity.setPrecoMinimoPermitido(resultado.getPrecoMinimoPermitido());
        entity.setMargemLucroDesejada(resultado.getMargemLucroDesejada());
        entity.setMargemMinimaGlobal(resultado.getMargemMinimaGlobal());
        entity.setMargemReal(resultado.getMargemReal());
        entity.setDescontoMaximoSolicitado(resultado.getDescontoMaximoSolicitado());
        entity.setDescontoMaximoEfetivo(resultado.getDescontoMaximoEfetivo());
        entity.setStatus(resultado.getStatus());
        entity.setJustificativa(resultado.getJustificativa());
        entity.setUsuarioResponsavel(normalizarUsuario(usuarioLogin));
        entity.setDataSimulacao(LocalDateTime.now());
        SimulacaoPrecificacaoJpaEntity salvo = simulacaoRepository.save(entity);

        List<ComponentePrecificacaoJpaEntity> componentes = resultado.getComponentes().stream().map(c -> {
            ComponentePrecificacaoJpaEntity item = new ComponentePrecificacaoJpaEntity();
            item.setSimulacao(salvo);
            item.setNome(c.getNome());
            item.setTipo(c.getTipo());
            item.setPercentual(c.getPercentual());
            item.setValor(c.getValor());
            item.setBaseCalculo(c.getBaseCalculo());
            item.setOrdem(c.getOrdem());
            return item;
        }).collect(Collectors.toList());
        componenteRepository.saveAll(componentes);
        salvo.setComponentes(componentes);
        return salvo;
    }

    private void notificarObservadores(PrecoDomainEvent evento) {
        for (ObservadorDePreco observador : observadores) {
            observador.aoAlterarPreco(evento);
        }
    }

    private ResultadoPrecificacaoView toResultadoView(SimulacaoPrecificacaoJpaEntity entity) {
        List<ComponentePrecificacaoJpaEntity> componentes = entity.getId() == null
                ? new ArrayList<>() : componenteRepository.findBySimulacaoIdOrderByOrdemAsc(entity.getId());
        List<ComponenteCalculoView> componentesView = componentes.stream()
                .map(c -> new ComponenteCalculoView(c.getNome(), c.getTipo(), c.getPercentual(),
                        c.getValor(), c.getBaseCalculo(), c.getOrdem()))
                .collect(Collectors.toList());
        return new ResultadoPrecificacaoView(
                entity.getId(), entity.getProdutoId(), entity.getProdutoNome(), entity.getPrecoAtual(),
                entity.getCustoCompra(), entity.getValorImpostos(), entity.getValorDespesasOperacionais(),
                entity.getCustoTotal(), entity.getPrecoSugerido(), entity.getPrecoMinimoPermitido(),
                entity.getMargemLucroDesejada(), entity.getMargemMinimaGlobal(), entity.getMargemReal(),
                entity.getDescontoMaximoSolicitado(), entity.getDescontoMaximoEfetivo(), entity.getStatus(),
                entity.getJustificativa(), entity.getUsuarioResponsavel(), entity.isAplicado(), entity.getDataSimulacao(),
                componentesView);
    }

    private ProdutoPrecificavelView toProdutoView(Produto produto, boolean possuiPolitica) {
        return new ProdutoPrecificavelView(produto.getId(), produto.getNome(), produto.getTipo(), produto.getQuantidade(),
                dinheiro(BigDecimal.valueOf(produto.getValor())), resolverCusto(null, produto), possuiPolitica);
    }

    private PoliticaPrecificacaoView toPoliticaView(PoliticaPrecificacaoJpaEntity entity, Produto produto) {
        return new PoliticaPrecificacaoView(entity.getId(), entity.getProdutoId(),
                produto != null ? produto.getNome() : "Produto #" + entity.getProdutoId(),
                entity.getMargemLucroDesejada(), entity.getAliquotaImpostos(),
                entity.getPercentualDespesasOperacionais(), entity.getDescontoMaximoPermitido(),
                entity.isAtiva(), entity.getObservacao(), entity.getAtualizadoEm());
    }

    private ParametroPrecificacaoView toParametroView(ParametroPrecificacaoJpaEntity entity) {
        return new ParametroPrecificacaoView(entity.getId(), entity.getMargemMinimaGlobal(), entity.getDescontoMaximoGlobal(),
                entity.getMargemPadraoLucro(), entity.getImpostoPadraoPercentual(),
                entity.getDespesaOperacionalPadraoPercentual(), entity.getAtualizadoEm());
    }

    private KpiPrecificacaoView montarKpis() {
        List<SimulacaoPrecificacaoJpaEntity> todas = simulacaoRepository.findAll();
        BigDecimal somaMargem = BigDecimal.ZERO;
        int quantidadeMargens = 0;
        for (SimulacaoPrecificacaoJpaEntity simulacao : todas) {
            if (simulacao.getMargemReal() != null) {
                somaMargem = somaMargem.add(simulacao.getMargemReal());
                quantidadeMargens++;
            }
        }
        BigDecimal media = quantidadeMargens == 0 ? BigDecimal.ZERO : somaMargem.divide(BigDecimal.valueOf(quantidadeMargens), 2, RoundingMode.HALF_UP);
        long bloqueadas = simulacaoRepository.countByStatus(StatusPrecificacao.BLOQUEADO_DESCONTO)
                + simulacaoRepository.countByStatus(StatusPrecificacao.BLOQUEADO_MARGEM);
        return new KpiPrecificacaoView(produtoRepository.count(), politicaRepository.countByAtivaTrue(),
                simulacaoRepository.countByStatus(StatusPrecificacao.APROVADO)
                        + simulacaoRepository.countByStatus(StatusPrecificacao.APLICADO),
                bloqueadas, media);
    }

    private BigDecimal calcularMargem(BigDecimal preco, BigDecimal custo) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return preco.subtract(custo).multiply(BigDecimal.valueOf(100)).divide(preco, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal dinheiro(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal percentual(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor.setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizarUsuario(String usuarioLogin) {
        return usuarioLogin == null || usuarioLogin.trim().isEmpty() ? "sistema" : usuarioLogin;
    }
}
