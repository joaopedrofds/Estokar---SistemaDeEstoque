package com.studiomuda.estoque.security;

public enum RecursoAcesso {
    PRODUTO("Produtos"),
    CUPOM("Cupons"),
    PEDIDO("Pedidos"),
    ESTOQUE("Estoque"),
    SUPRIMENTO("Suprimentos"),
    REMESSA("Remessas"),
    CLIENTE("Clientes"),
    FUNCIONARIO("Funcionários"),
    DASHBOARD("Dashboard"),
    KPI("KPI"),
    DEVOLUCAO("Devoluções"),
    FINANCEIRO("Relatórios financeiros"),
    ACESSO("Administração de acesso"),
    API("API"),
    HOME("Home");

    private final String descricao;

    RecursoAcesso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
