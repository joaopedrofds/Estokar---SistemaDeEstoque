package com.studiomuda.estoque.service;

import com.studiomuda.estoque.jpa.entity.HistoricoAjusteEstoqueJpaEntity;
import com.studiomuda.estoque.jpa.entity.MovimentacaoEstoqueJpaEntity;
import com.studiomuda.estoque.jpa.entity.ParametroAjusteEstoqueJpaEntity;
import com.studiomuda.estoque.jpa.entity.ProdutoJpaEntity;
import com.studiomuda.estoque.jpa.entity.SolicitacaoAjusteEstoqueJpaEntity;
import com.studiomuda.estoque.jpa.repository.HistoricoAjusteEstoqueJpaRepository;
import com.studiomuda.estoque.jpa.repository.MovimentacaoEstoqueJpaRepository;
import com.studiomuda.estoque.jpa.repository.ParametroAjusteEstoqueJpaRepository;
import com.studiomuda.estoque.jpa.repository.ProdutoJpaRepository;
import com.studiomuda.estoque.jpa.repository.SolicitacaoAjusteEstoqueJpaRepository;
import com.studiomuda.estoque.service.ajuste.AbstractAjusteEstoqueTemplate;
import com.studiomuda.estoque.service.ajuste.AjustePorAvariaTemplate;
import com.studiomuda.estoque.service.ajuste.AjustePorCorrecaoTemplate;
import com.studiomuda.estoque.service.ajuste.AjustePorPerdaTemplate;
import com.studiomuda.estoque.service.ajuste.AjustePorSobraTemplate;
import com.studiomuda.estoque.service.ajuste.ContextoAjuste;
import com.studiomuda.estoque.service.ajuste.ResultadoAjuste;
import com.studiomuda.estoque.service.ajuste.StatusAjuste;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class AjusteEstoqueService {
    public static final String TIPO_PERDA = "PERDA";
    public static final String TIPO_SOBRA = "SOBRA";
    public static final String TIPO_AVARIA = "AVARIA";
    public static final String TIPO_CORRECAO_ADMINISTRATIVA = "CORRECAO_ADMINISTRATIVA";

    private static final int LIMITE_PADRAO_SEM_APROVACAO = 5;
    private static final int PERCENTUAL_PADRAO_RISCO_ALTO = 30;

    private final SolicitacaoAjusteEstoqueJpaRepository solicitacaoRepository;
    private final HistoricoAjusteEstoqueJpaRepository historicoRepository;
    private final ParametroAjusteEstoqueJpaRepository parametroRepository;
    private final ProdutoJpaRepository produtoRepository;
    private final MovimentacaoEstoqueJpaRepository movimentacaoRepository;

    public AjusteEstoqueService(ObjectProvider<SolicitacaoAjusteEstoqueJpaRepository> solicitacaoRepository,
                                ObjectProvider<HistoricoAjusteEstoqueJpaRepository> historicoRepository,
                                ObjectProvider<ParametroAjusteEstoqueJpaRepository> parametroRepository,
                                ObjectProvider<ProdutoJpaRepository> produtoRepository,
                                ObjectProvider<MovimentacaoEstoqueJpaRepository> movimentacaoRepository) {
        this.solicitacaoRepository = solicitacaoRepository.getIfAvailable();
        this.historicoRepository = historicoRepository.getIfAvailable();
        this.parametroRepository = parametroRepository.getIfAvailable();
        this.produtoRepository = produtoRepository.getIfAvailable();
        this.movimentacaoRepository = movimentacaoRepository.getIfAvailable();
    }

    @Transactional
    public SolicitacaoAjusteEstoqueJpaEntity solicitarAjuste(Integer produtoId,
                                                             String tipo,
                                                             Integer quantidade,
                                                             String justificativa,
                                                             UsuarioOperacao solicitante) {
        validarJpaDisponivel();
        validarUsuario(solicitante, "solicitante");

        ProdutoJpaEntity produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));
        ParametrosAjuste parametros = buscarParametros();
        String tipoNormalizado = normalizarTipo(tipo);
        AbstractAjusteEstoqueTemplate template = selecionarTemplate(tipoNormalizado);
        ContextoAjuste contexto = new ContextoAjuste(
                produto,
                tipoNormalizado,
                quantidade != null ? quantidade : 0,
                justificativa,
                solicitante.getId(),
                solicitante.getNome(),
                parametros.getLimiteQuantidadeSemAprovacao(),
                parametros.getPercentualRiscoAlto()
        );

        ResultadoAjuste resultado = template.processarSolicitacao(contexto);
        SolicitacaoAjusteEstoqueJpaEntity solicitacao = solicitacaoRepository.save(resultado.getSolicitacao());

        if (!resultado.isExigeAprovacao()) {
            produtoRepository.save(produto);
            MovimentacaoEstoqueJpaEntity movimentacao = template.criarMovimentacaoAplicada(solicitacao);
            if (movimentacao != null) {
                movimentacaoRepository.save(movimentacao);
            }
        }

        registrarHistorico(
                solicitacao,
                null,
                solicitacao.getStatus(),
                resultado.isExigeAprovacao()
                        ? "Solicitacao registrada e retida para aprovacao por risco/alçada."
                        : "Solicitacao registrada e aplicada automaticamente.",
                solicitante
        );
        return solicitacao;
    }

    @Transactional
    public SolicitacaoAjusteEstoqueJpaEntity aprovar(Integer solicitacaoId,
                                                     String motivoDecisao,
                                                     UsuarioOperacao aprovador,
                                                     Collection<String> autoridades) {
        validarJpaDisponivel();
        validarUsuario(aprovador, "aprovador");
        validarAlcada(autoridades);
        validarMotivoDecisao(motivoDecisao);

        SolicitacaoAjusteEstoqueJpaEntity solicitacao = buscarSolicitacaoObrigatoria(solicitacaoId);
        String statusAnterior = solicitacao.getStatus();
        ProdutoJpaEntity produto = produtoRepository.findById(solicitacao.getProdutoId())
                .orElseThrow(() -> new IllegalStateException("Produto do ajuste nao encontrado."));

        AbstractAjusteEstoqueTemplate template = selecionarTemplate(solicitacao.getTipo());
        MovimentacaoEstoqueJpaEntity movimentacao = template.aplicarSolicitacaoAprovada(solicitacao, produto);
        solicitacao.setAprovadorId(aprovador.getId());
        solicitacao.setAprovadorNome(aprovador.getNome());
        solicitacao.setMotivoDecisao(motivoDecisao.trim());

        produtoRepository.save(produto);
        solicitacao = solicitacaoRepository.save(solicitacao);
        if (movimentacao != null) {
            movimentacaoRepository.save(movimentacao);
        }
        registrarHistorico(solicitacao, statusAnterior, solicitacao.getStatus(), "Ajuste aprovado: " + motivoDecisao.trim(), aprovador);
        return solicitacao;
    }

    @Transactional
    public SolicitacaoAjusteEstoqueJpaEntity reprovar(Integer solicitacaoId,
                                                      String motivoDecisao,
                                                      UsuarioOperacao aprovador,
                                                      Collection<String> autoridades) {
        validarJpaDisponivel();
        validarUsuario(aprovador, "aprovador");
        validarAlcada(autoridades);
        validarMotivoDecisao(motivoDecisao);

        SolicitacaoAjusteEstoqueJpaEntity solicitacao = buscarSolicitacaoObrigatoria(solicitacaoId);
        if (!StatusAjuste.PENDENTE_APROVACAO.name().equals(solicitacao.getStatus())) {
            throw new IllegalStateException("Somente solicitacoes pendentes podem ser reprovadas.");
        }

        String statusAnterior = solicitacao.getStatus();
        solicitacao.setStatus(StatusAjuste.REPROVADO.name());
        solicitacao.setAprovadorId(aprovador.getId());
        solicitacao.setAprovadorNome(aprovador.getNome());
        solicitacao.setMotivoDecisao(motivoDecisao.trim());
        solicitacao.setDataDecisao(Timestamp.valueOf(LocalDateTime.now()));
        solicitacao = solicitacaoRepository.save(solicitacao);
        registrarHistorico(solicitacao, statusAnterior, solicitacao.getStatus(), "Ajuste reprovado: " + motivoDecisao.trim(), aprovador);
        return solicitacao;
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoAjusteEstoqueJpaEntity> listarSolicitacoes(String status) {
        if (solicitacaoRepository == null) {
            return Collections.emptyList();
        }
        if (status != null && !status.trim().isEmpty()) {
            List<SolicitacaoAjusteEstoqueJpaEntity> filtradas =
                    solicitacaoRepository.findByStatusOrderByDataSolicitacaoDescIdDesc(status.trim());
            inicializarProdutos(filtradas);
            return filtradas;
        }
        List<SolicitacaoAjusteEstoqueJpaEntity> solicitacoes =
                solicitacaoRepository.findAllByOrderByDataSolicitacaoDescIdDesc();
        inicializarProdutos(solicitacoes);
        return solicitacoes;
    }

    @Transactional(readOnly = true)
    public DetalheAjuste buscarDetalhe(Integer solicitacaoId) {
        validarJpaDisponivel();
        SolicitacaoAjusteEstoqueJpaEntity solicitacao = buscarSolicitacaoObrigatoria(solicitacaoId);
        if (solicitacao.getProduto() != null) {
            solicitacao.getProduto().getNome();
            solicitacao.getProduto().getQuantidade();
        }
        List<HistoricoAjusteEstoqueJpaEntity> historico =
                historicoRepository.findBySolicitacaoIdOrderByDataEventoDescIdDesc(solicitacaoId);
        return new DetalheAjuste(solicitacao, historico);
    }

    @Transactional(readOnly = true)
    public List<ProdutoJpaEntity> listarProdutos() {
        if (produtoRepository == null) {
            return Collections.emptyList();
        }
        return produtoRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public ParametrosAjuste buscarParametros() {
        if (parametroRepository == null) {
            return new ParametrosAjuste(LIMITE_PADRAO_SEM_APROVACAO, PERCENTUAL_PADRAO_RISCO_ALTO);
        }
        return parametroRepository.findFirstByOrderByIdAsc()
                .map(parametro -> new ParametrosAjuste(
                        valorOuPadrao(parametro.getLimiteQuantidadeSemAprovacao(), LIMITE_PADRAO_SEM_APROVACAO),
                        valorOuPadrao(parametro.getPercentualRiscoAlto(), PERCENTUAL_PADRAO_RISCO_ALTO)
                ))
                .orElse(new ParametrosAjuste(LIMITE_PADRAO_SEM_APROVACAO, PERCENTUAL_PADRAO_RISCO_ALTO));
    }

    public boolean possuiAlcadaAprovacao(Collection<String> autoridades) {
        if (autoridades == null) {
            return false;
        }
        for (String autoridade : autoridades) {
            if (autoridade == null) {
                continue;
            }
            String valor = autoridade.toUpperCase();
            if (valor.equals("ROLE_DIRETOR")
                    || valor.equals("ROLE_ADMINISTRADOR")
                    || valor.equals("ROLE_GERENTE")
                    || valor.equals("ROLE_GERENTE_OPERACIONAL")) {
                return true;
            }
        }
        return false;
    }

    private AbstractAjusteEstoqueTemplate selecionarTemplate(String tipo) {
        String normalizado = normalizarTipo(tipo);
        switch (normalizado) {
            case TIPO_PERDA:
                return new AjustePorPerdaTemplate();
            case TIPO_SOBRA:
                return new AjustePorSobraTemplate();
            case TIPO_AVARIA:
                return new AjustePorAvariaTemplate();
            case TIPO_CORRECAO_ADMINISTRATIVA:
                return new AjustePorCorrecaoTemplate();
            default:
                throw new IllegalArgumentException("Tipo de ajuste invalido.");
        }
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o tipo de ajuste.");
        }
        return tipo.trim().toUpperCase();
    }

    private void inicializarProdutos(List<SolicitacaoAjusteEstoqueJpaEntity> solicitacoes) {
        for (SolicitacaoAjusteEstoqueJpaEntity solicitacao : solicitacoes) {
            if (solicitacao.getProduto() != null) {
                solicitacao.getProduto().getNome();
                solicitacao.getProduto().getQuantidade();
            }
        }
    }

    private SolicitacaoAjusteEstoqueJpaEntity buscarSolicitacaoObrigatoria(Integer solicitacaoId) {
        if (solicitacaoId == null || solicitacaoId <= 0) {
            throw new IllegalArgumentException("Solicitacao de ajuste invalida.");
        }
        return solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitacao de ajuste nao encontrada."));
    }

    private void registrarHistorico(SolicitacaoAjusteEstoqueJpaEntity solicitacao,
                                    String statusAnterior,
                                    String statusNovo,
                                    String descricao,
                                    UsuarioOperacao usuario) {
        HistoricoAjusteEstoqueJpaEntity historico = new HistoricoAjusteEstoqueJpaEntity();
        historico.setSolicitacaoId(solicitacao.getId());
        historico.setStatusAnterior(statusAnterior);
        historico.setStatusNovo(statusNovo);
        historico.setDescricao(descricao);
        historico.setUsuarioId(usuario.getId());
        historico.setUsuarioNome(usuario.getNome());
        historico.setDataEvento(Timestamp.valueOf(LocalDateTime.now()));
        historicoRepository.save(historico);
    }

    private void validarJpaDisponivel() {
        if (solicitacaoRepository == null || historicoRepository == null || parametroRepository == null
                || produtoRepository == null || movimentacaoRepository == null) {
            throw new IllegalStateException("Modulo de ajuste de estoque requer JPA habilitado.");
        }
    }

    private void validarUsuario(UsuarioOperacao usuario, String papel) {
        if (usuario == null || usuario.getId() <= 0 || usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o " + papel + " responsavel pela operacao.");
        }
    }

    private void validarAlcada(Collection<String> autoridades) {
        if (!possuiAlcadaAprovacao(autoridades)) {
            throw new IllegalStateException("Apenas gerente, administrador ou diretor pode decidir ajustes pendentes.");
        }
    }

    private void validarMotivoDecisao(String motivoDecisao) {
        if (motivoDecisao == null || motivoDecisao.trim().length() < 10 || motivoDecisao.trim().length() > 300) {
            throw new IllegalArgumentException("O motivo da decisao deve ter entre 10 e 300 caracteres.");
        }
    }

    private int valorOuPadrao(Integer valor, int padrao) {
        return valor != null ? valor : padrao;
    }

    public static class UsuarioOperacao {
        private final int id;
        private final String nome;

        public UsuarioOperacao(int id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        public int getId() { return id; }
        public String getNome() { return nome; }
    }

    public static class ParametrosAjuste {
        private final int limiteQuantidadeSemAprovacao;
        private final int percentualRiscoAlto;

        public ParametrosAjuste(int limiteQuantidadeSemAprovacao, int percentualRiscoAlto) {
            this.limiteQuantidadeSemAprovacao = limiteQuantidadeSemAprovacao;
            this.percentualRiscoAlto = percentualRiscoAlto;
        }

        public int getLimiteQuantidadeSemAprovacao() { return limiteQuantidadeSemAprovacao; }
        public int getPercentualRiscoAlto() { return percentualRiscoAlto; }
    }

    public static class DetalheAjuste {
        private final SolicitacaoAjusteEstoqueJpaEntity solicitacao;
        private final List<HistoricoAjusteEstoqueJpaEntity> historico;

        public DetalheAjuste(SolicitacaoAjusteEstoqueJpaEntity solicitacao,
                             List<HistoricoAjusteEstoqueJpaEntity> historico) {
            this.solicitacao = solicitacao;
            this.historico = historico;
        }

        public SolicitacaoAjusteEstoqueJpaEntity getSolicitacao() { return solicitacao; }
        public List<HistoricoAjusteEstoqueJpaEntity> getHistorico() { return historico; }
    }
}
