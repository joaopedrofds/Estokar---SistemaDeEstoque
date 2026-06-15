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
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CobrancaServiceTest {
    @Test
    void deveBloquearVendaQuandoAtrasoExcedePoliticaESemAcordoValido() throws Exception {
        FakeClienteDAO clienteDAO = new FakeClienteDAO();
        CobrancaService service = new CobrancaService(
                politica(45),
                fatura(10, 80, 55),
                acordo(null, false),
                new FakeHistoricoCobrancaDAO(),
                clienteDAO
        );

        ResultadoAvaliacaoCredito resultado = service.avaliarVendaPdv(10, LocalDate.of(2026, 6, 15));

        assertTrue(resultado.isBloqueado());
        assertEquals(55, resultado.getDiasAtraso());
        assertEquals(10, clienteDAO.clienteBloqueadoId);
    }

    @Test
    void deveLiberarVendaQuandoExisteAcordoAtivoSemParcelaAtrasada() throws Exception {
        FakeClienteDAO clienteDAO = new FakeClienteDAO();
        AcordoPagamento acordoAtivo = new AcordoPagamento();
        acordoAtivo.setId(7);
        acordoAtivo.setClienteId(10);
        acordoAtivo.setStatus("EM_ACORDO");
        CobrancaService service = new CobrancaService(
                politica(45),
                fatura(10, 80, 55),
                acordo(acordoAtivo, false),
                new FakeHistoricoCobrancaDAO(),
                clienteDAO
        );

        ResultadoAvaliacaoCredito resultado = service.avaliarVendaPdv(10, LocalDate.of(2026, 6, 15));

        assertFalse(resultado.isBloqueado());
        assertEquals(-1, clienteDAO.clienteBloqueadoId);
    }

    @Test
    void devePerderProtecaoQuandoParcelaDoAcordoEstaAtrasada() throws Exception {
        FakeAcordoPagamentoDAO acordoDAO = acordo(new AcordoPagamento(), true);
        acordoDAO.acordo.setId(7);
        acordoDAO.acordo.setClienteId(10);
        FakeClienteDAO clienteDAO = new FakeClienteDAO();
        CobrancaService service = new CobrancaService(
                politica(45),
                fatura(10, 80, 55),
                acordoDAO,
                new FakeHistoricoCobrancaDAO(),
                clienteDAO
        );

        ResultadoAvaliacaoCredito resultado = service.avaliarVendaPdv(10, LocalDate.of(2026, 6, 15));

        assertTrue(resultado.isBloqueado());
        assertEquals(7, acordoDAO.acordoSemProtecaoId);
        assertEquals(10, clienteDAO.clienteBloqueadoId);
    }

    @Test
    void historicoNaoPermiteUpdateOuDelete() {
        HistoricoCobrancaDAO dao = new HistoricoCobrancaDAO();

        assertThrows(UnsupportedOperationException.class, () -> dao.atualizar(new HistoricoCobranca()));
        assertThrows(UnsupportedOperationException.class, () -> dao.deletar(1));
    }

    private FakePoliticaCreditoDAO politica(int diasLimite) {
        PoliticaCredito politica = new PoliticaCredito();
        politica.setDiasLimiteAtraso(diasLimite);
        politica.setAtiva(true);
        return new FakePoliticaCreditoDAO(politica);
    }

    private FakeFaturaDAO fatura(int clienteId, int pedidoId, int diasAtraso) {
        Fatura fatura = new Fatura();
        fatura.setId(30);
        fatura.setClienteId(clienteId);
        fatura.setPedidoId(pedidoId);
        fatura.setDiasAtraso(diasAtraso);
        return new FakeFaturaDAO(fatura);
    }

    private FakeAcordoPagamentoDAO acordo(AcordoPagamento acordo, boolean parcelaAtrasada) {
        return new FakeAcordoPagamentoDAO(acordo, parcelaAtrasada);
    }

    private static class FakePoliticaCreditoDAO extends PoliticaCreditoDAO {
        private final PoliticaCredito politica;

        private FakePoliticaCreditoDAO(PoliticaCredito politica) {
            this.politica = politica;
        }

        @Override
        public PoliticaCredito buscarAtiva() {
            return politica;
        }
    }

    private static class FakeFaturaDAO extends FaturaDAO {
        private final Fatura fatura;

        private FakeFaturaDAO(Fatura fatura) {
            this.fatura = fatura;
        }

        @Override
        public Fatura buscarMaiorAbertaPorCliente(int clienteId, LocalDate dataReferencia) {
            return fatura;
        }
    }

    private static class FakeAcordoPagamentoDAO extends AcordoPagamentoDAO {
        private final AcordoPagamento acordo;
        private final boolean parcelaAtrasada;
        private int acordoSemProtecaoId = -1;

        private FakeAcordoPagamentoDAO(AcordoPagamento acordo, boolean parcelaAtrasada) {
            this.acordo = acordo;
            this.parcelaAtrasada = parcelaAtrasada;
        }

        @Override
        public AcordoPagamento buscarAtivoValidoPorCliente(int clienteId, LocalDate dataReferencia) {
            return acordo;
        }

        @Override
        public boolean possuiParcelaAtrasada(int acordoId, LocalDate dataReferencia) {
            return parcelaAtrasada;
        }

        @Override
        public void registrarPerdaProtecao(int acordoId) {
            this.acordoSemProtecaoId = acordoId;
        }
    }

    private static class FakeHistoricoCobrancaDAO extends HistoricoCobrancaDAO {
        @Override
        public void inserir(HistoricoCobranca historico) throws SQLException {
        }
    }

    private static class FakeClienteDAO extends ClienteDAO {
        private int clienteBloqueadoId = -1;

        @Override
        public void bloquearPorInadimplencia(int id) {
            this.clienteBloqueadoId = id;
        }
    }
}
