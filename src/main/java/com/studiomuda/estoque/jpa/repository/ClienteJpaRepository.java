package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.ClienteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteJpaRepository extends JpaRepository<ClienteJpaEntity, Integer> {
    List<ClienteJpaEntity> findByAtivoTrueOrderByNomeAsc();
    List<ClienteJpaEntity> findAllByOrderByNomeAsc();
    long countByAtivoTrue();
    long countByAtivoTrueAndTipo(String tipo);
    Optional<ClienteJpaEntity> findByCpfCnpj(String cpfCnpj);
    boolean existsByCpfCnpj(String cpfCnpj);
    boolean existsByCpfCnpjAndIdNot(String cpfCnpj, Integer id);

    @Query("select c from ClienteJpaEntity c " +
            "where (:nome is null or upper(c.nome) like upper(concat('%', :nome, '%'))) " +
            "and (:tipo is null or c.tipo = :tipo) " +
            "and (:ativo is null or c.ativo = :ativo) " +
            "order by c.nome")
    List<ClienteJpaEntity> buscarComFiltros(@Param("nome") String nome,
                                            @Param("tipo") String tipo,
                                            @Param("ativo") Boolean ativo);

    @Query("select distinct c.tipo from ClienteJpaEntity c where c.tipo is not null and c.tipo <> '' order by c.tipo")
    List<String> listarTiposDisponiveis();

    @Query("select c from ClienteJpaEntity c where upper(c.faixaFidelidade.nome) in ('EM RISCO', 'INATIVO') order by c.nome")
    List<ClienteJpaEntity> buscarElegiveisRetencao();
    long countByFaixaFidelidadeId(Integer faixaId);
}
