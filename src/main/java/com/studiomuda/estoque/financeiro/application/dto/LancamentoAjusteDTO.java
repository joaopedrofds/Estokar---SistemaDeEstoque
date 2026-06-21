package com.studiomuda.estoque.financeiro.application.dto;

import com.studiomuda.estoque.financeiro.domain.CategoriaFinanceira;
import com.studiomuda.estoque.financeiro.domain.LancamentoAjuste;

import java.time.LocalDate;

/**
 * DTO de apresentação de um lançamento de ajuste (E-12), com o nome/tipo da
 * categoria (dado de exibição que não pertence ao agregado) — padrão View/DTO
 * do PetCollar ({@code de(...)}).
 */
public class LancamentoAjusteDTO {

    private final String id;
    private final LocalDate dataLancamento;
    private final String categoriaNome;
    private final String categoriaTipo;
    private final double valor;
    private final String descricao;
    private final String username;

    public LancamentoAjusteDTO(String id, LocalDate dataLancamento, String categoriaNome,
                               String categoriaTipo, double valor, String descricao, String username) {
        this.id = id;
        this.dataLancamento = dataLancamento;
        this.categoriaNome = categoriaNome;
        this.categoriaTipo = categoriaTipo;
        this.valor = valor;
        this.descricao = descricao;
        this.username = username;
    }

    public static LancamentoAjusteDTO de(LancamentoAjuste a, CategoriaFinanceira categoria) {
        return new LancamentoAjusteDTO(
                a.getId().getValor(),
                a.getDataLancamento(),
                categoria != null ? categoria.getNome() : null,
                categoria != null ? categoria.getTipo() : null,
                a.getValor(),
                a.getDescricao(),
                a.getUsername());
    }

    public String getId() { return id; }
    public LocalDate getDataLancamento() { return dataLancamento; }
    public String getCategoriaNome() { return categoriaNome; }
    public String getCategoriaTipo() { return categoriaTipo; }
    public double getValor() { return valor; }
    public String getDescricao() { return descricao; }
    public String getUsername() { return username; }
}
