package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    @Query("select distinct p.tipo from Produto p where p.tipo is not null and trim(p.tipo) <> '' order by p.tipo")
    List<String> findDistinctTipos();

    long countByTipo(String tipo);

    long countByQuantidadeGreaterThan(int quantidade);

    long countByTipoAndQuantidadeGreaterThan(String tipo, int quantidade);

    long countByQuantidade(int quantidade);

    long countByTipoAndQuantidade(String tipo, int quantidade);
}
