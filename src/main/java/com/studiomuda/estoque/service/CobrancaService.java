package com.studiomuda.estoque.service;

import com.studiomuda.estoque.jpa.entity.AcordoPagamentoJpaEntity;
import com.studiomuda.estoque.jpa.entity.ClienteJpaEntity;
import com.studiomuda.estoque.jpa.entity.FaturaJpaEntity;
import com.studiomuda.estoque.jpa.entity.HistoricoCobrancaJpaEntity;
import com.studiomuda.estoque.jpa.entity.PoliticaCreditoJpaEntity;
import com.studiomuda.estoque.jpa.repository.AcordoPagamentoJpaRepository;
import com.studiomuda.estoque.jpa.repository.ClienteJpaRepository;
import com.studiomuda.estoque.jpa.repository.FaturaJpaRepository;
import com.studiomuda.estoque.jpa.repository.HistoricoCobrancaJpaRepository;
import com.studiomuda.estoque.jpa.repository.PoliticaCreditoJpaRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
//@ConditionalOnBean(ClienteJpaRepository.class)
public class CobrancaService {
    private static final int LIMITE_PADRAO_DIAS_ATRASO = 45;

    private final ClienteJpaRepository clienteRepository;
    private final PoliticaCreditoJpaRepository politicaRepository;
    private final AcordoPagamentoJpaRepository acordoRepository;
    private final FaturaJpaRepository faturaRepository;
    private final HistoricoCobrancaJpaRepository historicoRepository;

    public CobrancaService(ClienteJpaRepository clienteRepository,
                           PoliticaCreditoJpaRepository politicaRepository,
                           AcordoPagamentoJpaRepository acordoRepository,
                           FaturaJpaRepository faturaRepository,
                           HistoricoCobrancaJpaRepository historicoRepository) {
        this.clienteRepository = clienteRepository;
        this.politicaRepository = politicaRepository;
        this.acordoRepository = acordoRepository;
        this.faturaRepository = faturaRepository;
        this.historicoRepository = historicoRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteJpaEntity> listarClientesAtivos() {
        return clienteRepository.findByAtivoTrueOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public List<PoliticaCreditoJpaEntity> listarPoliticas() {
        return politicaRepository.findAllByOrderByDataInicioDesc();
    }

    @Transactional(readOnly = true)
    public List<FaturaJpaEntity> listarFaturas() {
        return faturaRepository.findAllByOrderByDataVencimentoAsc();
    }

    @Transactional(readOnly = true)
    public List<AcordoPagamentoJpaEntity> listarAcordos() {
        return acordoRepository.findAllByOrderByDataAcordoDesc();
    }

    @Transactional(readOnly = true)
    public List<HistoricoCobrancaJpaEntity> listarHistoricos() {
        return historicoRepository.findAllByOrderByDataContatoDesc();
    }

    @Transactional
    public PoliticaCreditoJpaEntity salvarPolitica(PoliticaCreditoJpaEntity politica) {
        if (Boolean.TRUE.equals(politica.getAtiva())) {
            LocalDate encerramento = politica.getDataInicio() != null ? politica.getDataInicio().minusDays(1) : LocalDate.now();
            for (PoliticaCreditoJpaEntity vigente : politicaRepository.findByAtivaTrueAndDataFimIsNull()) {
                if (politica.getId() == null || !vigente.getId().equals(politica.getId())) {
                    vigente.setAtiva(false);
                    vigente.setDataFim(encerramento);
                    politicaRepository.save(vigente);
                }
            }
        }
        if (politica.getDataInicio() == null) {
            politica.setDataInicio(LocalDate.now());
        }
        return politicaRepository.save(politica);
    }

    @Transactional
    public FaturaJpaEntity salvarFatura(FaturaJpaEntity fatura, Integer clienteId, Integer acordoId) {
        fatura.setCliente(clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado.")));
        fatura.setAcordoPagamento(acordoId != null && acordoId > 0
                ? acordoRepository.findById(acordoId).orElseThrow(() -> new IllegalArgumentException("Acordo nao encontrado."))
                : null);
        if (fatura.getStatus() == null || fatura.getStatus().trim().isEmpty()) {
            fatura.setStatus("PENDENTE");
        }
        return faturaRepository.save(fatura);
    }

    @Transactional
    public AcordoPagamentoJpaEntity salvarAcordo(AcordoPagamentoJpaEntity acordo, Integer clienteId) {
        acordo.setCliente(clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado.")));
        if (acordo.getStatus() == null || acordo.getStatus().trim().isEmpty()) {
            acordo.setStatus("ATIVO");
        }
        return acordoRepository.save(acordo);
    }

    @Transactional
    public HistoricoCobrancaJpaEntity registrarHistorico(HistoricoCobrancaJpaEntity historico,
                                                         Integer clienteId,
                                                         Integer faturaId,
                                                         Integer registroOriginalId) {
        historico.setCliente(clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado.")));
        historico.setFatura(faturaId != null && faturaId > 0
                ? faturaRepository.findById(faturaId).orElseThrow(() -> new IllegalArgumentException("Fatura nao encontrada."))
                : null);
        historico.setRegistroOriginal(registroOriginalId != null && registroOriginalId > 0
                ? historicoRepository.findById(registroOriginalId).orElseThrow(() -> new IllegalArgumentException("Registro original nao encontrado."))
                : null);
        return historicoRepository.save(historico);
    }

    @Transactional
    public AvaliacaoCredito avaliarVenda(Integer clienteId) {
        PoliticaCreditoJpaEntity politica = politicaRepository.findFirstByAtivaTrueAndDataFimIsNullOrderByDataInicioDesc()
                .orElse(null);
        int limiteDias = politica != null && politica.getLimiteDiasAtraso() != null
                ? politica.getLimiteDiasAtraso()
                : LIMITE_PADRAO_DIAS_ATRASO;
        LocalDate hoje = LocalDate.now();
        List<AcordoPagamentoJpaEntity> acordosProtegidos = acordoRepository.buscarAtivosValidos(clienteId, hoje);

        for (AcordoPagamentoJpaEntity acordo : acordosProtegidos) {
            if (!faturaRepository.buscarParcelasAtrasadasDoAcordo(acordo.getId(), hoje).isEmpty()) {
                acordo.setStatus("QUEBRADO");
                acordoRepository.save(acordo);
            }
        }

        boolean possuiAcordoValido = acordoRepository.buscarAtivosValidos(clienteId, hoje).stream()
                .anyMatch(acordo -> faturaRepository.buscarParcelasAtrasadasDoAcordo(acordo.getId(), hoje).isEmpty());
        List<FaturaJpaEntity> vencidas = faturaRepository.buscarVencidasAlemDoLimite(clienteId, hoje.minusDays(limiteDias));

        if (!vencidas.isEmpty() && !possuiAcordoValido) {
            FaturaJpaEntity fatura = vencidas.get(0);
            int diasAtraso = (int) ChronoUnit.DAYS.between(fatura.getDataVencimento(), hoje);
            ClienteJpaEntity cliente = fatura.getCliente();
            cliente.setAtivo(false);
            clienteRepository.save(cliente);
            return AvaliacaoCredito.bloqueada(fatura.getId(), diasAtraso, limiteDias);
        }

        return AvaliacaoCredito.liberada(limiteDias);
    }

    public static class AvaliacaoCredito {
        private final boolean bloqueado;
        private final Integer faturaId;
        private final int diasAtraso;
        private final int limiteDias;

        private AvaliacaoCredito(boolean bloqueado, Integer faturaId, int diasAtraso, int limiteDias) {
            this.bloqueado = bloqueado;
            this.faturaId = faturaId;
            this.diasAtraso = diasAtraso;
            this.limiteDias = limiteDias;
        }

        public static AvaliacaoCredito bloqueada(Integer faturaId, int diasAtraso, int limiteDias) {
            return new AvaliacaoCredito(true, faturaId, diasAtraso, limiteDias);
        }

        public static AvaliacaoCredito liberada(int limiteDias) {
            return new AvaliacaoCredito(false, null, 0, limiteDias);
        }

        public boolean isBloqueado() { return bloqueado; }
        public Integer getFaturaId() { return faturaId; }
        public int getDiasAtraso() { return diasAtraso; }
        public int getLimiteDias() { return limiteDias; }
    }
}
