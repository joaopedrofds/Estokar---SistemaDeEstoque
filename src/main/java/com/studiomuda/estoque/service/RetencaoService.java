package com.studiomuda.estoque.service;

import com.studiomuda.estoque.jpa.entity.AcaoRetencaoJpaEntity;
import com.studiomuda.estoque.jpa.entity.ClienteJpaEntity;
import com.studiomuda.estoque.jpa.repository.AcaoRetencaoJpaRepository;
import com.studiomuda.estoque.jpa.repository.ClienteJpaRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
//@ConditionalOnBean(AcaoRetencaoJpaRepository.class)
public class RetencaoService {
    private static final List<String> FAIXAS_ELEGIVEIS = Arrays.asList("EM RISCO", "INATIVO");

    private final AcaoRetencaoJpaRepository acaoRepository;
    private final ClienteJpaRepository clienteRepository;
    private final FidelidadeService fidelidadeService;

    public RetencaoService(AcaoRetencaoJpaRepository acaoRepository,
                           ClienteJpaRepository clienteRepository,
                           FidelidadeService fidelidadeService) {
        this.acaoRepository = acaoRepository;
        this.clienteRepository = clienteRepository;
        this.fidelidadeService = fidelidadeService;
    }

    @Transactional(readOnly = true)
    public List<AcaoRetencaoJpaEntity> listarAcoes() {
        return acaoRepository.findAllByOrderByDataCriacaoDesc();
    }

    @Transactional(readOnly = true)
    public List<ClienteJpaEntity> listarClientesElegiveis() {
        return clienteRepository.buscarElegiveisRetencao();
    }

    @Transactional
    public AcaoRetencaoJpaEntity gerarAcao(Integer clienteId, BigDecimal percentual, LocalDate validade) {
        ClienteJpaEntity cliente = fidelidadeService.recalcularCategoria(clienteId);
        String faixa = cliente.getFaixaFidelidade() != null
                ? cliente.getFaixaFidelidade().getNome().trim().toUpperCase(Locale.ROOT)
                : "";
        if (!FAIXAS_ELEGIVEIS.contains(faixa)) {
            throw new IllegalStateException("Acoes de retencao sao exclusivas para clientes Em Risco ou Inativo.");
        }
        if (acaoRepository.findFirstByClienteIdAndAtivaTrue(clienteId).isPresent()) {
            throw new IllegalStateException("O cliente ja possui uma acao de retencao ativa.");
        }
        if (percentual == null || percentual.compareTo(BigDecimal.ZERO) <= 0
                || percentual.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("O percentual deve estar entre 0 e 100.");
        }
        AcaoRetencaoJpaEntity acao = new AcaoRetencaoJpaEntity();
        acao.setCliente(cliente);
        acao.setFaixa(cliente.getFaixaFidelidade());
        acao.setPercentualDesconto(percentual);
        acao.setDataValidade(validade != null ? validade : LocalDate.now().plusDays(30));
        acao.setCodigoCupom("RET-" + clienteId + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
        return acaoRepository.save(acao);
    }

    @Transactional
    public void encerrar(Integer acaoId) {
        AcaoRetencaoJpaEntity acao = acaoRepository.findById(acaoId)
                .orElseThrow(() -> new IllegalArgumentException("Acao de retencao nao encontrada."));
        acao.setAtiva(false);
        acaoRepository.save(acao);
    }
}
