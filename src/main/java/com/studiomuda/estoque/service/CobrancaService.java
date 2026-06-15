package com.studiomuda.estoque.service;

import com.studiomuda.estoque.dao.AcordoPagamentoDAO;
import com.studiomuda.estoque.dao.ClienteDAO;
import com.studiomuda.estoque.dao.FaturaDAO;
import com.studiomuda.estoque.dao.HistoricoCobrancaDAO;
import com.studiomuda.estoque.dao.PoliticaCreditoDAO;
import com.studiomuda.estoque.model.AcordoPagamento;
import com.studiomuda.estoque.model.Fatura;
import com.studiomuda.estoque.model.HistoricoCobranca;
import com.studiomuda.estoque.model.PoliticaCredito;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;

@Service
public class CobrancaService {
    private final PoliticaCreditoDAO politicaCreditoDAO;
    private final FaturaDAO faturaDAO;
    private final AcordoPagamentoDAO acordoPagamentoDAO;
    private final HistoricoCobrancaDAO historicoCobrancaDAO;
    private final ClienteDAO clienteDAO;

    public CobrancaService() {
        this(new PoliticaCreditoDAO(), new FaturaDAO(), new AcordoPagamentoDAO(), new HistoricoCobrancaDAO(), new ClienteDAO());
    }

    CobrancaService(PoliticaCreditoDAO politicaCreditoDAO,
                    FaturaDAO faturaDAO,
                    AcordoPagamentoDAO acordoPagamentoDAO,
                    HistoricoCobrancaDAO historicoCobrancaDAO,
                    ClienteDAO clienteDAO) {
        this.politicaCreditoDAO = politicaCreditoDAO;
        this.faturaDAO = faturaDAO;
        this.acordoPagamentoDAO = acordoPagamentoDAO;
        this.historicoCobrancaDAO = historicoCobrancaDAO;
        this.clienteDAO = clienteDAO;
    }

    public ResultadoAvaliacaoCredito avaliarVendaPdv(int clienteId) throws SQLException {
        return avaliarVendaPdv(clienteId, LocalDate.now());
    }

    ResultadoAvaliacaoCredito avaliarVendaPdv(int clienteId, LocalDate dataReferencia) throws SQLException {
        PoliticaCredito politica = politicaCreditoDAO.buscarAtiva();
        Fatura fatura = faturaDAO.buscarMaiorAbertaPorCliente(clienteId, dataReferencia);
        if (fatura == null || fatura.getDiasAtraso() <= politica.getDiasLimiteAtraso()) {
            return ResultadoAvaliacaoCredito.liberado();
        }

        AcordoPagamento acordo = acordoPagamentoDAO.buscarAtivoValidoPorCliente(clienteId, dataReferencia);
        if (acordo != null && !acordoPagamentoDAO.possuiParcelaAtrasada(acordo.getId(), dataReferencia)) {
            return ResultadoAvaliacaoCredito.liberado();
        }

        if (acordo != null) {
            acordoPagamentoDAO.registrarPerdaProtecao(acordo.getId());
            registrarHistorico(clienteId, fatura, acordo.getId(),
                    "Acordo perdeu protecao por parcela vencida; cliente voltou ao bloqueio automatico.");
        }

        clienteDAO.bloquearPorInadimplencia(clienteId);
        String mensagem = "Venda bloqueada por inadimplencia. Fatura em aberto com " +
                fatura.getDiasAtraso() + " dias de atraso.";
        registrarHistorico(clienteId, fatura, acordo != null ? acordo.getId() : null, mensagem);
        return ResultadoAvaliacaoCredito.bloqueado(fatura.getId(), fatura.getPedidoId(), fatura.getDiasAtraso(), mensagem);
    }

    public void ativarPolitica(PoliticaCredito politica) throws SQLException {
        politicaCreditoDAO.ativarNova(politica);
    }

    public void registrarContato(HistoricoCobranca historico) throws SQLException {
        historicoCobrancaDAO.inserir(historico);
    }

    public void corrigirContato(int registroOriginalId, HistoricoCobranca correcao) throws SQLException {
        historicoCobrancaDAO.corrigirRegistro(registroOriginalId, correcao);
    }

    private void registrarHistorico(int clienteId, Fatura fatura, Integer acordoId, String descricao) throws SQLException {
        HistoricoCobranca historico = new HistoricoCobranca();
        historico.setClienteId(clienteId);
        historico.setFaturaId(fatura.getId() > 0 ? fatura.getId() : null);
        historico.setAcordoId(acordoId);
        historico.setTipo("BLOQUEIO_AUTOMATICO");
        historico.setDescricao(descricao);
        historico.setUsuario("sistema");
        try {
            historicoCobrancaDAO.inserir(historico);
        } catch (SQLException e) {
            if (!isTabelaInexistente(e)) {
                throw e;
            }
        }
    }

    private boolean isTabelaInexistente(SQLException e) {
        return "42S02".equals(e.getSQLState())
                || (e.getMessage() != null && e.getMessage().toLowerCase().contains("doesn't exist"));
    }
}
