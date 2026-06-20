package com.studiomuda.estoque.precificacao.domain.model;

import com.studiomuda.estoque.precificacao.domain.iterator.ComponentesCusto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;


public class MotorPrecificacaoDinamica {
    private static final int ESCALA_CALCULO = 6;

    public ResultadoPrecificacao calcular(int produtoId,
                                          String produtoNome,
                                          BigDecimal precoAtual,
                                          BigDecimal custoCompra,
                                          PoliticaPrecificacao politica,
                                          BigDecimal margemMinimaGlobal,
                                          BigDecimal descontoMaximoGlobal) {
        BigDecimal custoBase = dinheiro(custoCompra);
        if (custoBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Informe um custo de compra maior que zero para calcular o preço.");
        }

        ComponentesCusto componentes = montarComponentes(custoBase, politica);
        BigDecimal custoTotal = BigDecimal.ZERO;
        Iterator<ComponenteCusto> iterator = componentes.iterator();
        while (iterator.hasNext()) {
            custoTotal = custoTotal.add(iterator.next().getValor());
        }
        custoTotal = dinheiro(custoTotal);

        BigDecimal margemDesejada = percentual(politica.getMargemLucroDesejada());
        BigDecimal margemMinima = percentual(margemMinimaGlobal);
        BigDecimal precoSugerido = calcularPrecoPorMargem(custoTotal, margemDesejada);
        BigDecimal precoMinimoPermitido = calcularPrecoPorMargem(custoTotal, margemMinima);
        BigDecimal margemReal = calcularMargem(precoSugerido, custoTotal);

        BigDecimal descontoSolicitado = politica.getDescontoMaximoPermitido().min(percentual(descontoMaximoGlobal));
        BigDecimal descontoSeguro = calcularDescontoSeguro(precoSugerido, precoMinimoPermitido);
        BigDecimal descontoEfetivo = descontoSolicitado.min(descontoSeguro).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        StatusPrecificacao status = StatusPrecificacao.APROVADO;
        String justificativa = "Preço aprovado: custo total, impostos e despesas foram cobertos pela margem desejada.";

        if (margemDesejada.compareTo(margemMinima) < 0 || margemReal.compareTo(margemMinima) < 0) {
            status = StatusPrecificacao.BLOQUEADO_MARGEM;
            justificativa = String.format("Margem desejada de %.2f%% está abaixo da margem mínima global de %.2f%%.",
                    margemDesejada, margemMinima);
        } else if (descontoSolicitado.compareTo(descontoSeguro) > 0) {
            status = StatusPrecificacao.BLOQUEADO_DESCONTO;
            justificativa = String.format("Desconto máximo solicitado de %.2f%% derrubaria a margem abaixo do mínimo. Desconto seguro calculado: %.2f%%.",
                    descontoSolicitado, descontoSeguro);
        }

        return new ResultadoPrecificacao(
                produtoId,
                produtoNome,
                dinheiro(precoAtual),
                custoBase,
                valorPorPercentual(custoBase, politica.getAliquotaImpostos()),
                valorPorPercentual(custoBase, politica.getPercentualDespesasOperacionais()),
                custoTotal,
                precoSugerido,
                precoMinimoPermitido,
                margemDesejada,
                margemMinima,
                margemReal,
                descontoSolicitado,
                descontoEfetivo,
                status,
                justificativa,
                componentes
        );
    }

    private ComponentesCusto montarComponentes(BigDecimal custoCompra, PoliticaPrecificacao politica) {
        ComponentesCusto componentes = new ComponentesCusto();
        componentes.adicionar(new ComponenteCusto("Custo de compra do produto", TipoComponenteCusto.CUSTO_COMPRA,
                BigDecimal.ZERO, custoCompra, custoCompra, 1));
        componentes.adicionar(new ComponenteCusto("Impostos sobre compra/venda", TipoComponenteCusto.IMPOSTO,
                politica.getAliquotaImpostos(), valorPorPercentual(custoCompra, politica.getAliquotaImpostos()), custoCompra, 2));
        componentes.adicionar(new ComponenteCusto("Rateio de despesas operacionais", TipoComponenteCusto.DESPESA_OPERACIONAL,
                politica.getPercentualDespesasOperacionais(), valorPorPercentual(custoCompra, politica.getPercentualDespesasOperacionais()), custoCompra, 3));
        return componentes;
    }

    private BigDecimal valorPorPercentual(BigDecimal base, BigDecimal percentual) {
        return dinheiro(base.multiply(percentual(percentual)).divide(BigDecimal.valueOf(100), ESCALA_CALCULO, RoundingMode.HALF_UP));
    }

    private BigDecimal calcularPrecoPorMargem(BigDecimal custoTotal, BigDecimal margemPercentual) {
        BigDecimal divisor = BigDecimal.ONE.subtract(percentual(margemPercentual).divide(BigDecimal.valueOf(100), ESCALA_CALCULO, RoundingMode.HALF_UP));
        if (divisor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Margem inválida para cálculo de preço.");
        }
        return dinheiro(custoTotal.divide(divisor, ESCALA_CALCULO, RoundingMode.HALF_UP));
    }

    private BigDecimal calcularMargem(BigDecimal preco, BigDecimal custo) {
        if (preco.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return preco.subtract(custo)
                .multiply(BigDecimal.valueOf(100))
                .divide(preco, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularDescontoSeguro(BigDecimal precoSugerido, BigDecimal precoMinimoPermitido) {
        if (precoSugerido.compareTo(BigDecimal.ZERO) <= 0 || precoSugerido.compareTo(precoMinimoPermitido) <= 0) {
            return BigDecimal.ZERO;
        }
        return precoSugerido.subtract(precoMinimoPermitido)
                .multiply(BigDecimal.valueOf(100))
                .divide(precoSugerido, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal dinheiro(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal percentual(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor.setScale(2, RoundingMode.HALF_UP);
    }
}
