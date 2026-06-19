package com.studiomuda.estoque.jpa.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "faixa_fidelidade")
public class FaixaFidelidadeJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(name = "dias_minimo", nullable = false)
    private Integer diasMinimo;

    @Column(name = "dias_maximo", nullable = false)
    private Integer diasMaximo;

    private Boolean ativa = true;

    @OneToMany(mappedBy = "faixa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BeneficioCategoriaJpaEntity> beneficios = new ArrayList<>();

    @OneToMany(mappedBy = "faixaFidelidade", fetch = FetchType.LAZY)
    private List<ClienteJpaEntity> clientes = new ArrayList<>();

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public Integer getDiasMinimo() { return diasMinimo; }
    public Integer getDiasMaximo() { return diasMaximo; }
    public Boolean getAtiva() { return ativa; }
    public List<BeneficioCategoriaJpaEntity> getBeneficios() { return beneficios; }
    public List<ClienteJpaEntity> getClientes() { return clientes; }

    public void setId(Integer id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDiasMinimo(Integer diasMinimo) { this.diasMinimo = diasMinimo; }
    public void setDiasMaximo(Integer diasMaximo) { this.diasMaximo = diasMaximo; }
    public void setAtiva(Boolean ativa) { this.ativa = ativa; }
}
