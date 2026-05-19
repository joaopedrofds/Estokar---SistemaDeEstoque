package com.studiomuda.estoque.dao;

import com.studiomuda.estoque.conexao.Conexao;
import com.studiomuda.estoque.model.CategoriaFinanceira;
import com.studiomuda.estoque.model.RelatorioCategoriaLinha;
import com.studiomuda.estoque.model.RelatorioGerado;
import com.studiomuda.estoque.model.RelatorioIndicadorLinha;
import com.studiomuda.estoque.model.TemplateRelatorio;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioDAO {
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final MovimentacaoEstoqueDAO movimentacaoDAO = new MovimentacaoEstoqueDAO();
    private final LancamentoAjusteDAO lancamentoAjusteDAO = new LancamentoAjusteDAO();
    private final TemplateRelatorioDAO templateRelatorioDAO = new TemplateRelatorioDAO();
    private final CategoriaFinanceiraDAO categoriaFinanceiraDAO = new CategoriaFinanceiraDAO();

    public RelatorioGerado gerarRelatorio(int templateId,
                                          LocalDate dataInicio,
                                          LocalDate dataFim,
                                          Integer usuarioId,
                                          String username) throws SQLException {
        TemplateRelatorio template = templateRelatorioDAO.buscarPorId(templateId);
        if (template == null || !template.isAtivo()) {
            throw new SQLException("Template de relatório inválido ou inativo.");
        }

        long dias = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
        LocalDate inicioAnterior = dataInicio.minusDays(dias);
        LocalDate fimAnterior = dataInicio.minusDays(1);

        Date sqlInicio = Date.valueOf(dataInicio);
        Date sqlFim = Date.valueOf(dataFim);
        Date sqlInicioAnterior = Date.valueOf(inicioAnterior);
        Date sqlFimAnterior = Date.valueOf(fimAnterior);

        Map<Integer, CategoriaFinanceira> categoriasTemplate = carregarCategoriasObrigatorias(template);
        Map<Integer, ValoresCategoria> valoresAtuais = calcularValoresPorCategoria(categoriasTemplate, sqlInicio, sqlFim);
        Map<Integer, ValoresCategoria> valoresAnteriores = calcularValoresPorCategoria(categoriasTemplate, sqlInicioAnterior, sqlFimAnterior);

        double receitaOperacional = 0;
        double custoOperacional = 0;
        double totalAjustesReceita = 0;
        double totalAjustesDespesa = 0;

        List<RelatorioCategoriaLinha> linhas = new ArrayList<>();
        for (CategoriaFinanceira categoria : categoriasTemplate.values()) {
            ValoresCategoria atual = valoresAtuais.getOrDefault(categoria.getId(), ValoresCategoria.zero());
            ValoresCategoria anterior = valoresAnteriores.getOrDefault(categoria.getId(), ValoresCategoria.zero());

            RelatorioCategoriaLinha linha = new RelatorioCategoriaLinha();
            linha.setCategoriaId(categoria.getId());
            linha.setCategoriaNome(categoria.getNome());
            linha.setTipoCategoria(categoria.getTipo());
            linha.setValorPeriodo(atual.valorTotal());
            linha.setValorPeriodoAnterior(anterior.valorTotal());
            linha.setVariacaoPercentual(calcularVariacao(atual.valorTotal(), anterior.valorTotal()));
            linha.setOrigemRastreio(atual.rastreio);
            linha.setAjusteManual(atual.valorAjuste > 0);
            linhas.add(linha);

            if ("RECEITA".equalsIgnoreCase(categoria.getTipo())) {
                receitaOperacional += atual.valorAutomatico;
                totalAjustesReceita += atual.valorAjuste;
            } else {
                custoOperacional += atual.valorAutomatico;
                totalAjustesDespesa += atual.valorAjuste;
            }
        }

        PedidoDAO.ResumoFinanceiroPedido resumoPedidos = pedidoDAO.resumirReceitaPaga(sqlInicio, sqlFim);
        double resultadoOperacional = receitaOperacional - custoOperacional;
        double resultadoConsolidado = resultadoOperacional + totalAjustesReceita - totalAjustesDespesa;

        RelatorioGerado relatorio = new RelatorioGerado();
        relatorio.setTemplateId(template.getId());
        relatorio.setTemplateNome(template.getNome());
        relatorio.setDataInicio(sqlInicio);
        relatorio.setDataFim(sqlFim);
        relatorio.setDataInicioAnterior(sqlInicioAnterior);
        relatorio.setDataFimAnterior(sqlFimAnterior);
        relatorio.setGeradoPorUsuarioId(usuarioId);
        relatorio.setGeradoPorUsername(username);
        relatorio.setReceitaOperacional(receitaOperacional);
        relatorio.setCustoOperacional(custoOperacional);
        relatorio.setResultadoOperacional(resultadoOperacional);
        relatorio.setTotalAjustesReceita(totalAjustesReceita);
        relatorio.setTotalAjustesDespesa(totalAjustesDespesa);
        relatorio.setResultadoConsolidado(resultadoConsolidado);
        relatorio.setQuantidadePedidos(resumoPedidos.getQuantidadePedidos());
        relatorio.setLinhasCategoria(linhas);

        PedidoDAO.ResumoFinanceiroPedido resumoAnterior = pedidoDAO.resumirReceitaPaga(sqlInicioAnterior, sqlFimAnterior);
        relatorio.setLinhasIndicador(calcularIndicadores(
                template.getIndicadores(),
                receitaOperacional,
                custoOperacional,
                resultadoConsolidado,
                resumoPedidos.getQuantidadePedidos(),
                resumoAnterior.getReceitaTotal(),
                resumoAnterior.getQuantidadePedidos()
        ));

        persistirSnapshot(relatorio);
        return relatorio;
    }

    public List<RelatorioGerado> listarHistorico(int limite) throws SQLException {
        List<RelatorioGerado> lista = new ArrayList<>();
        String sql = "SELECT * FROM relatorio_gerado ORDER BY data_geracao DESC LIMIT ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCabecalho(rs));
                }
            }
        }
        return lista;
    }

    public RelatorioGerado buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM relatorio_gerado WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    RelatorioGerado relatorio = mapearCabecalho(rs);
                    relatorio.setLinhasCategoria(listarLinhasCategoria(conn, id));
                    relatorio.setLinhasIndicador(listarLinhasIndicador(conn, id));
                    return relatorio;
                }
            }
        }
        return null;
    }

    private Map<Integer, CategoriaFinanceira> carregarCategoriasObrigatorias(TemplateRelatorio template) throws SQLException {
        Map<Integer, CategoriaFinanceira> mapa = new HashMap<>();
        for (Integer categoriaId : template.getCategoriaIds()) {
            CategoriaFinanceira categoria = categoriaFinanceiraDAO.buscarPorId(categoriaId);
            if (categoria != null && categoria.isAtivo()) {
                mapa.put(categoria.getId(), categoria);
            }
        }
        return mapa;
    }

    private Map<Integer, ValoresCategoria> calcularValoresPorCategoria(Map<Integer, CategoriaFinanceira> categorias,
                                                                       Date inicio,
                                                                       Date fim) throws SQLException {
        Map<Integer, ValoresCategoria> valores = new HashMap<>();
        for (CategoriaFinanceira categoria : categorias.values()) {
            valores.put(categoria.getId(), resolverValorCategoria(categoria, inicio, fim));
        }
        return valores;
    }

    private ValoresCategoria resolverValorCategoria(CategoriaFinanceira categoria, Date inicio, Date fim) throws SQLException {
        String origem = categoria.getOrigemSistema() == null ? "" : categoria.getOrigemSistema();
        double valorAutomatico = 0;
        String rastreio = "Sem lançamentos no período";

        switch (origem) {
            case "PEDIDO_PAGO":
                PedidoDAO.ResumoFinanceiroPedido resumo = pedidoDAO.resumirReceitaPaga(inicio, fim);
                valorAutomatico = resumo.getReceitaTotal();
                rastreio = "PedidoDAO: " + resumo.getQuantidadePedidos() + " pedido(s) pago(s)";
                break;
            case "MOVIMENTACAO_SAIDA":
                valorAutomatico = movimentacaoDAO.somarCustoSaida(inicio, fim);
                rastreio = "MovimentacaoDAO: saídas de estoque";
                break;
            case "MOVIMENTACAO_ENTRADA_DEVOLUCAO":
                valorAutomatico = movimentacaoDAO.somarDevolucoes(inicio, fim);
                rastreio = "MovimentacaoDAO: entradas de devolução/estorno";
                break;
            default:
                rastreio = "Categoria sem origem automática";
                break;
        }

        double valorAjustes = lancamentoAjusteDAO.somarPorCategoria(categoria.getId(), inicio, fim);
        if (valorAjustes != 0) {
            rastreio = rastreio + " + LancamentoAjusteDAO: ajustes manuais";
        }

        return new ValoresCategoria(valorAutomatico, valorAjustes, rastreio);
    }

    private List<RelatorioIndicadorLinha> calcularIndicadores(List<String> indicadores,
                                                              double receita,
                                                              double custo,
                                                              double resultado,
                                                              int qtdPedidos,
                                                              double receitaAnterior,
                                                              int qtdPedidosAnterior) {
        List<RelatorioIndicadorLinha> linhas = new ArrayList<>();
        if (indicadores == null) {
            return linhas;
        }

        for (String indicador : indicadores) {
            RelatorioIndicadorLinha linha = new RelatorioIndicadorLinha();
            linha.setIndicador(indicador);

            switch (indicador) {
                case "MARGEM_BRUTA":
                    double margem = receita > 0 ? ((receita - custo) / receita) * 100 : 0;
                    double margemAnterior = receitaAnterior > 0
                            ? ((receitaAnterior - custo) / receitaAnterior) * 100
                            : 0;
                    linha.setValor(margem);
                    linha.setValorAnterior(margemAnterior);
                    linha.setVariacaoPercentual(calcularVariacao(margem, margemAnterior));
                    linha.setFormulaDescricao("(Receita Total − Custo Total) / Receita Total × 100");
                    break;
                case "TICKET_MEDIO":
                    double ticket = qtdPedidos > 0 ? receita / qtdPedidos : 0;
                    double ticketAnterior = qtdPedidosAnterior > 0 ? receitaAnterior / qtdPedidosAnterior : 0;
                    linha.setValor(ticket);
                    linha.setValorAnterior(ticketAnterior);
                    linha.setVariacaoPercentual(calcularVariacao(ticket, ticketAnterior));
                    linha.setFormulaDescricao("Receita Total / Quantidade de Pedidos Pagos");
                    break;
                case "RESULTADO_LIQUIDO":
                    linha.setValor(resultado);
                    linha.setValorAnterior(null);
                    linha.setVariacaoPercentual(null);
                    linha.setFormulaDescricao("Resultado Operacional + Ajustes de Receita − Ajustes de Despesa");
                    break;
                default:
                    linha.setValor(0);
                    linha.setFormulaDescricao("Indicador não mapeado");
                    break;
            }
            linhas.add(linha);
        }
        return linhas;
    }

    private Double calcularVariacao(double atual, double anterior) {
        if (anterior == 0) {
            return atual == 0 ? 0.0 : 100.0;
        }
        return ((atual - anterior) / Math.abs(anterior)) * 100;
    }

    private void persistirSnapshot(RelatorioGerado relatorio) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int relatorioId = inserirCabecalho(conn, relatorio);
                relatorio.setId(relatorioId);
                inserirLinhasCategoria(conn, relatorioId, relatorio.getLinhasCategoria());
                inserirLinhasIndicador(conn, relatorioId, relatorio.getLinhasIndicador());
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private int inserirCabecalho(Connection conn, RelatorioGerado relatorio) throws SQLException {
        String sql = "INSERT INTO relatorio_gerado (" +
                "template_id, template_nome, data_inicio, data_fim, data_inicio_anterior, data_fim_anterior, " +
                "gerado_por_usuario_id, gerado_por_username, receita_operacional, custo_operacional, " +
                "resultado_operacional, total_ajustes_receita, total_ajustes_despesa, resultado_consolidado, quantidade_pedidos" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, relatorio.getTemplateId());
            stmt.setString(2, relatorio.getTemplateNome());
            stmt.setDate(3, new Date(relatorio.getDataInicio().getTime()));
            stmt.setDate(4, new Date(relatorio.getDataFim().getTime()));
            stmt.setDate(5, new Date(relatorio.getDataInicioAnterior().getTime()));
            stmt.setDate(6, new Date(relatorio.getDataFimAnterior().getTime()));
            if (relatorio.getGeradoPorUsuarioId() != null && relatorio.getGeradoPorUsuarioId() > 0) {
                stmt.setInt(7, relatorio.getGeradoPorUsuarioId());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }
            stmt.setString(8, relatorio.getGeradoPorUsername());
            stmt.setDouble(9, relatorio.getReceitaOperacional());
            stmt.setDouble(10, relatorio.getCustoOperacional());
            stmt.setDouble(11, relatorio.getResultadoOperacional());
            stmt.setDouble(12, relatorio.getTotalAjustesReceita());
            stmt.setDouble(13, relatorio.getTotalAjustesDespesa());
            stmt.setDouble(14, relatorio.getResultadoConsolidado());
            stmt.setInt(15, relatorio.getQuantidadePedidos());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Falha ao persistir relatório gerado.");
    }

    private void inserirLinhasCategoria(Connection conn, int relatorioId, List<RelatorioCategoriaLinha> linhas) throws SQLException {
        String sql = "INSERT INTO relatorio_categoria_linha (" +
                "relatorio_id, categoria_id, categoria_nome, tipo_categoria, valor_periodo, valor_periodo_anterior, " +
                "variacao_percentual, origem_rastreio, ajuste_manual" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (RelatorioCategoriaLinha linha : linhas) {
                stmt.setInt(1, relatorioId);
                stmt.setInt(2, linha.getCategoriaId());
                stmt.setString(3, linha.getCategoriaNome());
                stmt.setString(4, linha.getTipoCategoria());
                stmt.setDouble(5, linha.getValorPeriodo());
                stmt.setDouble(6, linha.getValorPeriodoAnterior());
                if (linha.getVariacaoPercentual() != null) {
                    stmt.setDouble(7, linha.getVariacaoPercentual());
                } else {
                    stmt.setNull(7, java.sql.Types.DOUBLE);
                }
                stmt.setString(8, linha.getOrigemRastreio());
                stmt.setBoolean(9, linha.isAjusteManual());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private void inserirLinhasIndicador(Connection conn, int relatorioId, List<RelatorioIndicadorLinha> linhas) throws SQLException {
        String sql = "INSERT INTO relatorio_indicador_linha (" +
                "relatorio_id, indicador, valor, valor_anterior, variacao_percentual, formula_descricao" +
                ") VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (RelatorioIndicadorLinha linha : linhas) {
                stmt.setInt(1, relatorioId);
                stmt.setString(2, linha.getIndicador());
                stmt.setDouble(3, linha.getValor());
                if (linha.getValorAnterior() != null) {
                    stmt.setDouble(4, linha.getValorAnterior());
                } else {
                    stmt.setNull(4, java.sql.Types.DOUBLE);
                }
                if (linha.getVariacaoPercentual() != null) {
                    stmt.setDouble(5, linha.getVariacaoPercentual());
                } else {
                    stmt.setNull(5, java.sql.Types.DOUBLE);
                }
                stmt.setString(6, linha.getFormulaDescricao());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private List<RelatorioCategoriaLinha> listarLinhasCategoria(Connection conn, int relatorioId) throws SQLException {
        List<RelatorioCategoriaLinha> linhas = new ArrayList<>();
        String sql = "SELECT * FROM relatorio_categoria_linha WHERE relatorio_id = ? ORDER BY tipo_categoria DESC, categoria_nome";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, relatorioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RelatorioCategoriaLinha linha = new RelatorioCategoriaLinha();
                    linha.setId(rs.getInt("id"));
                    linha.setRelatorioId(rs.getInt("relatorio_id"));
                    linha.setCategoriaId(rs.getInt("categoria_id"));
                    linha.setCategoriaNome(rs.getString("categoria_nome"));
                    linha.setTipoCategoria(rs.getString("tipo_categoria"));
                    linha.setValorPeriodo(rs.getDouble("valor_periodo"));
                    linha.setValorPeriodoAnterior(rs.getDouble("valor_periodo_anterior"));
                    double variacao = rs.getDouble("variacao_percentual");
                    if (!rs.wasNull()) {
                        linha.setVariacaoPercentual(variacao);
                    }
                    linha.setOrigemRastreio(rs.getString("origem_rastreio"));
                    linha.setAjusteManual(rs.getBoolean("ajuste_manual"));
                    linhas.add(linha);
                }
            }
        }
        return linhas;
    }

    private List<RelatorioIndicadorLinha> listarLinhasIndicador(Connection conn, int relatorioId) throws SQLException {
        List<RelatorioIndicadorLinha> linhas = new ArrayList<>();
        String sql = "SELECT * FROM relatorio_indicador_linha WHERE relatorio_id = ? ORDER BY indicador";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, relatorioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RelatorioIndicadorLinha linha = new RelatorioIndicadorLinha();
                    linha.setId(rs.getInt("id"));
                    linha.setRelatorioId(rs.getInt("relatorio_id"));
                    linha.setIndicador(rs.getString("indicador"));
                    linha.setValor(rs.getDouble("valor"));
                    double valorAnterior = rs.getDouble("valor_anterior");
                    if (!rs.wasNull()) {
                        linha.setValorAnterior(valorAnterior);
                    }
                    double variacao = rs.getDouble("variacao_percentual");
                    if (!rs.wasNull()) {
                        linha.setVariacaoPercentual(variacao);
                    }
                    linha.setFormulaDescricao(rs.getString("formula_descricao"));
                    linhas.add(linha);
                }
            }
        }
        return linhas;
    }

    private RelatorioGerado mapearCabecalho(ResultSet rs) throws SQLException {
        RelatorioGerado relatorio = new RelatorioGerado();
        relatorio.setId(rs.getInt("id"));
        relatorio.setTemplateId(rs.getInt("template_id"));
        relatorio.setTemplateNome(rs.getString("template_nome"));
        relatorio.setDataInicio(rs.getDate("data_inicio"));
        relatorio.setDataFim(rs.getDate("data_fim"));
        relatorio.setDataInicioAnterior(rs.getDate("data_inicio_anterior"));
        relatorio.setDataFimAnterior(rs.getDate("data_fim_anterior"));
        int usuarioId = rs.getInt("gerado_por_usuario_id");
        if (!rs.wasNull()) {
            relatorio.setGeradoPorUsuarioId(usuarioId);
        }
        relatorio.setGeradoPorUsername(rs.getString("gerado_por_username"));
        relatorio.setDataGeracao(rs.getTimestamp("data_geracao"));
        relatorio.setReceitaOperacional(rs.getDouble("receita_operacional"));
        relatorio.setCustoOperacional(rs.getDouble("custo_operacional"));
        relatorio.setResultadoOperacional(rs.getDouble("resultado_operacional"));
        relatorio.setTotalAjustesReceita(rs.getDouble("total_ajustes_receita"));
        relatorio.setTotalAjustesDespesa(rs.getDouble("total_ajustes_despesa"));
        relatorio.setResultadoConsolidado(rs.getDouble("resultado_consolidado"));
        relatorio.setQuantidadePedidos(rs.getInt("quantidade_pedidos"));
        return relatorio;
    }

    private static class ValoresCategoria {
        private final double valorAutomatico;
        private final double valorAjuste;
        private final String rastreio;

        private ValoresCategoria(double valorAutomatico, double valorAjuste, String rastreio) {
            this.valorAutomatico = valorAutomatico;
            this.valorAjuste = valorAjuste;
            this.rastreio = rastreio;
        }

        private double valorTotal() {
            return valorAutomatico + valorAjuste;
        }

        private static ValoresCategoria zero() {
            return new ValoresCategoria(0, 0, "Sem lançamentos no período");
        }
    }
}
